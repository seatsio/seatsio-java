package seatsio.util;

import kong.unirest.*;
import org.apache.http.conn.ConnectTimeoutException;
import seatsio.SeatsioException;

import javax.net.ssl.SSLHandshakeException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static java.nio.charset.StandardCharsets.UTF_8;

public class UnirestWrapper {

    private static UnirestInstance unirest = new UnirestInstance(new Config()
            .socketTimeout(10000)
            .concurrency(200, 200));

    private static final Set<HttpMethod> SAFE_METHODS = Set.of(HttpMethod.GET, HttpMethod.HEAD, HttpMethod.OPTIONS, HttpMethod.TRACE);

    static final long RETRY_WINDOW_MILLIS = Duration.ofMinutes(5).toMillis();
    static final long NEVER_SENT_RETRY_WINDOW_MILLIS = Duration.ofSeconds(30).toMillis();

    private final String secretKey;
    private final String workspaceKey;
    private final UUID idempotencyKey;
    private final SharedState state;

    public UnirestWrapper(String secretKey, String workspaceKey) {
        this(secretKey, workspaceKey, RetryClock.SYSTEM);
    }

    UnirestWrapper(String secretKey, String workspaceKey, RetryClock clock) {
        this(secretKey, workspaceKey, null, new SharedState(clock));
    }

    private UnirestWrapper(String secretKey, String workspaceKey, UUID idempotencyKey, SharedState state) {
        this.secretKey = secretKey;
        this.workspaceKey = workspaceKey;
        this.idempotencyKey = idempotencyKey;
        this.state = state;
    }

    public UnirestWrapper withIdempotencyKey(UUID idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.version() != 4 || idempotencyKey.variant() != 2) {
            throw new IllegalArgumentException("Idempotency key " + idempotencyKey + " is not a v4 UUID");
        }
        return new UnirestWrapper(secretKey, workspaceKey, idempotencyKey, state);
    }

    public String stringResponse(HttpRequest request) {
        byte[] response = binaryResponse(request);
        return new String(response, UTF_8);
    }

    public byte[] binaryResponse(HttpRequest request) {
        addIdempotencyKey(request);
        request.header("X-Client-Lib", "Java");
        authenticate(request, secretKey, workspaceKey);
        return execute(request);
    }

    private void addIdempotencyKey(HttpRequest request) {
        if (!isSafe(request)) {
            request.header(IdempotencyKeyStatus.KEY_HEADER, (idempotencyKey != null ? idempotencyKey : UUID.randomUUID()).toString());
        }
    }

    private byte[] execute(HttpRequest request) {
        Call call = isSafe(request) ? new Read(request) : new Write(request);
        long start = state.clock.millis();
        for (int retryCount = 0; ; retryCount++) {
            AtomicReference<RawResponse> response = new AtomicReference<>();
            AtomicReference<byte[]> responseBody = new AtomicReference<>();
            try {
                request.thenConsume(r -> {
                    response.set((RawResponse) r);
                    responseBody.set(response.get().getContentAsBytes());
                });
            } catch (UnirestException e) {
                long deadline = neverSent(e) ? start + NEVER_SENT_RETRY_WINDOW_MILLIS : Long.MAX_VALUE;
                if (call.failed(e) && waitBeforeRetry(retryCount, call, deadline)) {
                    continue;
                }
                throw call.connectionFailure(e);
            }
            call.answered(response.get(), responseBody.get());
            if (!isRetryable(response.get(), responseBody.get()) || !waitBeforeRetry(retryCount, call, Long.MAX_VALUE)) {
                return processResponse(request, response.get(), responseBody.get(), call);
            }
        }
    }

    private static boolean isRetryable(RawResponse response, byte[] responseBody) {
        return response.getStatus() == 429 || ErrorResponse.isIdempotencyKeyInUse(response, responseBody);
    }

    private static boolean isSafe(HttpRequest request) {
        return SAFE_METHODS.contains(request.getHttpMethod());
    }

    private boolean waitBeforeRetry(int retryCount, Call call, long deadline) {
        if (retryCount >= state.maxRetries) {
            return false;
        }
        long delay = (long) Math.pow(2, retryCount + 2) * 100;
        if (!call.mayRetryAfter(delay)) {
            return false;
        }
        if (deadline != Long.MAX_VALUE && state.clock.millis() + delay > deadline) {
            return false;
        }
        try {
            state.clock.sleep(delay);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    private byte[] processResponse(HttpRequest request, RawResponse response, byte[] responseBody, Call call) {
        if (response.getStatus() >= 400) {
            throw call.error(response, responseBody);
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

    public UnirestWrapper maxRetries(int maxRetries) {
        state.maxRetries = maxRetries;
        return this;
    }

    public static void reinitializeHttpConnectionPool() {
        unirest.shutDown(false);
    }

    static boolean neverSent(UnirestException e) {
        Throwable cause = e.getCause();
        // ConnectTimeoutException is an InterruptedIOException, so it must be checked by type, not by "timeout"
        return cause instanceof ConnectException
                || cause instanceof ConnectTimeoutException
                || cause instanceof UnknownHostException
                || cause instanceof SSLHandshakeException;
    }

    private interface Call {

        void answered(RawResponse response, byte[] responseBody);

        boolean failed(UnirestException e);

        boolean mayRetryAfter(long delay);

        SeatsioException connectionFailure(UnirestException e);

        SeatsioException error(RawResponse response, byte[] responseBody);
    }

    private static class Read implements Call {

        private final HttpRequest<?> request;

        Read(HttpRequest<?> request) {
            this.request = request;
        }

        @Override
        public void answered(RawResponse response, byte[] responseBody) {
        }

        @Override
        public boolean failed(UnirestException e) {
            return neverSent(e);
        }

        @Override
        public boolean mayRetryAfter(long delay) {
            return true;
        }

        @Override
        public SeatsioException connectionFailure(UnirestException e) {
            return SeatsioException.fromConnectionFailure(e, request, !neverSent(e), false);
        }

        @Override
        public SeatsioException error(RawResponse response, byte[] responseBody) {
            return SeatsioException.from(request, response, responseBody, false, false);
        }
    }

    private class Write implements Call {

        private final HttpRequest<?> request;
        private final long start;
        private boolean mayHaveRun;

        Write(HttpRequest<?> request) {
            this.request = request;
            this.start = state.clock.millis();
        }

        @Override
        public void answered(RawResponse response, byte[] responseBody) {
            if (ErrorResponse.isIdempotencyKeyInUse(response, responseBody)) {
                mayHaveRun = true;
            }
            state.remember(IdempotencyKeyStatus.of(response));
        }

        @Override
        public boolean failed(UnirestException e) {
            if (neverSent(e)) {
                return true;
            }
            mayHaveRun = true;
            return routeHonorsKeys();
        }

        @Override
        public boolean mayRetryAfter(long delay) {
            return !mayHaveRun || startsWithinRetryWindow(delay);
        }

        private boolean startsWithinRetryWindow(long delay) {
            return state.clock.millis() + delay - start <= RETRY_WINDOW_MILLIS;
        }

        boolean routeHonorsKeys() {
            return state.honorsKeys();
        }

        @Override
        public SeatsioException error(RawResponse response, byte[] responseBody) {
            return SeatsioException.from(request, response, responseBody, routeHonorsKeys(), mayHaveRun);
        }

        @Override
        public SeatsioException connectionFailure(UnirestException e) {
            boolean safeToRetryWithSameKey = !mayHaveRun || (routeHonorsKeys() && startsWithinRetryWindow(0));
            return SeatsioException.fromConnectionFailure(e, request, mayHaveRun, safeToRetryWithSameKey);
        }
    }

    private static class SharedState {
        final RetryClock clock;
        volatile int maxRetries = 5;
        private volatile Boolean keysHonored;

        SharedState(RetryClock clock) {
            this.clock = clock;
        }

        void remember(IdempotencyKeyStatus status) {
            if (status != null) {
                keysHonored = status.keyHonored();
            }
        }

        boolean honorsKeys() {
            return Boolean.TRUE.equals(keysHonored);
        }
    }
}
