package seatsio.util;

import kong.unirest.*;
import seatsio.SeatsioException;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

import static java.nio.charset.StandardCharsets.UTF_8;

public class UnirestWrapper {

    private static UnirestInstance unirest = new UnirestInstance(new Config()
            .connectTimeout(10000)
            .socketTimeout(30000)
            .concurrency(200, 200));

    private int maxRetries = 5;
    private int connectTimeoutMillis = 10000;
    private int socketTimeoutMillis = 30000;
    private final String secretKey;
    private final String workspaceKey;

    public UnirestWrapper(String secretKey, String workspaceKey) {
        this.secretKey = secretKey;
        this.workspaceKey = workspaceKey;
    }

    public String stringResponse(HttpRequest request) {
        byte[] response = execute(request, 0);
        return new String(response, UTF_8);
    }

    public byte[] binaryResponse(HttpRequest request) {
        return execute(request, 0);
    }

    private byte[] execute(HttpRequest request, int retryCount) {
        try {
            AtomicReference<RawResponse> response = new AtomicReference<>();
            AtomicReference<byte[]> responseBody = new AtomicReference<>();
            request.header("X-Client-Lib", "Java");
            request.connectTimeout(connectTimeoutMillis);
            request.socketTimeout(socketTimeoutMillis);
            authenticate(request, secretKey, workspaceKey).thenConsume(r -> {
                response.set((RawResponse) r);
                responseBody.set(response.get().getContentAsBytes());
            });
            if (retryCount >= maxRetries || response.get().getStatus() != 429) {
                return processResponse(request, response.get(), responseBody.get());
            } else {
                long waitTime = (long) Math.pow(2, retryCount + 2) * 100;
                sleep(waitTime);
                return execute(request, ++retryCount);
            }
        } catch (UnirestException e) {
            throw new SeatsioException(e, request);
        }
    }

    private byte[] processResponse(HttpRequest request, RawResponse response, byte[] responseBody) {
        if (response.getStatus() >= 400) {
            throw SeatsioException.from(request, response, responseBody);
        } else {
            return responseBody;
        }
    }

    public static GetRequest get(String url) {
        return unirest.get(url);
    }

    public static HttpRequestWithBody post(String url) {
        return unirest.post(url);
    }

    public static HttpRequestWithBody delete(String url) {
        return unirest.delete(url);
    }

    private <T extends HttpRequest> T authenticate(T request, String secretKey, String workspaceKey) {
        request.basicAuth(secretKey, null);
        if (workspaceKey != null) {
            request.header("X-Workspace-Key", workspaceKey);
        }
        return request;
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public UnirestWrapper maxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
        return this;
    }

    public UnirestWrapper connectTimeout(Duration connectTimeout) {
        this.connectTimeoutMillis = Math.toIntExact(connectTimeout.toMillis());
        return this;
    }

    public UnirestWrapper socketTimeout(Duration socketTimeout) {
        this.socketTimeoutMillis = Math.toIntExact(socketTimeout.toMillis());
        return this;
    }

    public static void reinitializeHttpConnectionPool() {
        unirest.shutDown(false);
    }

}
