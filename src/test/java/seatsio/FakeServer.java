package seatsio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

public class FakeServer implements AutoCloseable {

    private final ServerSocket serverSocket;
    private final List<Reply> replies;
    private final List<Request> requests = Collections.synchronizedList(new ArrayList<>());
    private final ConcurrentLinkedQueue<Socket> sockets = new ConcurrentLinkedQueue<>();

    private FakeServer(List<Reply> replies) throws IOException {
        this.replies = replies;
        this.serverSocket = new ServerSocket(0, 50, InetAddress.getLoopbackAddress());
        Thread thread = new Thread(this::serve, "fake-server");
        thread.setDaemon(true);
        thread.start();
    }

    public static FakeServer replying(Reply... replies) {
        try {
            return new FakeServer(List.of(replies));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String url(String path) {
        return "http://localhost:" + serverSocket.getLocalPort() + path;
    }

    public List<Request> requests() {
        return new ArrayList<>(requests);
    }

    public List<String> sentIdempotencyKeys() {
        return requests().stream().map(r -> r.header(SeatsioClient.IDEMPOTENCY_KEY_HEADER)).toList();
    }

    @Override
    public void close() throws IOException {
        serverSocket.close();
        for (Socket socket : sockets) {
            socket.close();
        }
    }

    private void serve() {
        while (!serverSocket.isClosed()) {
            try (Socket socket = serverSocket.accept()) {
                sockets.add(socket);
                Request request = readRequest(socket.getInputStream());
                if (request == null) {
                    continue;
                }
                requests.add(request);
                Reply reply = replies.get(Math.min(requests.size(), replies.size()) - 1);
                reply.beforeReplying.accept(request);
                if (reply.computedBy != null) {
                    reply = reply.computedBy.apply(request);
                }
                if (reply.status != null) {
                    write(socket.getOutputStream(), reply);
                }
            } catch (IOException e) {
                // server closed, or client went away
            }
        }
    }

    private static Request readRequest(InputStream in) throws IOException {
        String requestLine = readLine(in);
        if (requestLine == null || requestLine.isEmpty()) {
            return null;
        }
        Map<String, List<String>> headers = new HashMap<>();
        String line;
        while ((line = readLine(in)) != null && !line.isEmpty()) {
            int colon = line.indexOf(':');
            headers.computeIfAbsent(line.substring(0, colon).trim().toLowerCase(Locale.ROOT), k -> new ArrayList<>()).add(line.substring(colon + 1).trim());
        }
        int contentLength = Integer.parseInt(headers.getOrDefault("content-length", List.of("0")).get(0));
        byte[] body = in.readNBytes(contentLength);
        String[] parts = requestLine.split(" ");
        return new Request(parts[0], parts[1], headers, new String(body, UTF_8));
    }

    private static String readLine(InputStream in) throws IOException {
        ByteArrayOutputStream line = new ByteArrayOutputStream();
        int b;
        while ((b = in.read()) != -1) {
            if (b == '\n') {
                return line.toString(ISO_8859_1).replaceAll("\r$", "");
            }
            line.write(b);
        }
        return line.size() == 0 ? null : line.toString(ISO_8859_1);
    }

    private static void write(OutputStream out, Reply reply) throws IOException {
        byte[] body = reply.body.getBytes(UTF_8);
        StringBuilder head = new StringBuilder("HTTP/1.1 " + reply.status + " Fake\r\n");
        if (reply.contentType != null) {
            head.append("Content-Type: ").append(reply.contentType).append("\r\n");
        }
        head.append("Content-Length: ").append(body.length).append("\r\n");
        head.append("Connection: close\r\n\r\n");
        out.write(head.toString().getBytes(ISO_8859_1));
        out.write(body);
        out.flush();
    }

    public record Request(String method, String path, Map<String, List<String>> headers, String body) {

        public String header(String name) {
            List<String> values = headers(name);
            return values.isEmpty() ? null : values.get(0);
        }

        public List<String> headers(String name) {
            return headers.getOrDefault(name.toLowerCase(Locale.ROOT), List.of());
        }
    }

    public static class Reply {

        private final Integer status;
        private final String contentType;
        private final String body;
        private final Consumer<Request> beforeReplying;
        private final Function<Request, Reply> computedBy;

        private Reply(Integer status, String contentType, String body, Consumer<Request> beforeReplying) {
            this(status, contentType, body, beforeReplying, null);
        }

        private Reply(Integer status, String contentType, String body, Consumer<Request> beforeReplying, Function<Request, Reply> computedBy) {
            this.status = status;
            this.contentType = contentType;
            this.body = body;
            this.beforeReplying = beforeReplying;
            this.computedBy = computedBy;
        }

        public static Reply computedBy(Function<Request, Reply> computedBy) {
            return new Reply(null, null, "", r -> {
            }, computedBy);
        }

        public static Reply of(int status, String contentType, String body) {
            return new Reply(status, contentType == null || contentType.isEmpty() ? null : contentType, body, r -> {
            });
        }

        public static Reply status(int status) {
            return new Reply(status, "text/plain", "", r -> {
            });
        }

        public static Reply json(int status, String body) {
            return new Reply(status, "application/json", body, r -> {
            });
        }

        public static Reply error(int status, String code) {
            return json(status, "{\"errors\": [{\"code\": \"" + code + "\", \"message\": \"" + code + "\"}], \"requestId\": \"aRequestId\"}");
        }

        public static Reply withoutContentType(int status, String body) {
            return new Reply(status, null, body, r -> {
            });
        }

        public static Reply closeConnectionWithoutAnswering() {
            return new Reply(null, null, "", r -> {
            });
        }

        public Reply andBeforeReplying(Consumer<Request> action) {
            return new Reply(status, contentType, body, action);
        }
    }
}
