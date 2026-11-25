package seatsio.util;

import kong.unirest.*;
import org.apache.http.conn.ConnectTimeoutException;
import seatsio.SeatsioClient;
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
            request.header(SeatsioClient.IDEMPOTENCY_KEY_HEADER, (idempotencyKey != null ? idempotencyKey : UUID.randomUUID()).toString());
        }
    }

    private byte[] execute(HttpRequest request) {
        Write write = isSafe(request) ? null : new Write(request);
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
                if (write == null) {
                    if (neverSent(e) && waitBeforeRetry(retryCount, null, start + NEVER_SENT_RETRY_WINDOW_MILLIS)) {
                        continue;
                    }
                    throw SeatsioException.fromConnectionFailure(e, request, !neverSent(e), false);
                }
                long deadline = neverSent(e) ? start + NEVER_SENT_RETRY_WINDOW_MILLIS : Long.MAX_VALUE;
                if (write.failed(e) && waitBeforeRetry(retryCount, write, deadline)) {
                    continue;
                }
                throw write.connectionFailure(e);
            }
            if (write != null) {
                write.answered(response.get(), responseBody.get());
            }
            if (!isRetryable(response.get(), responseBody.get()) || !waitBeforeRetry(retryCount, write, Long.MAX_VALUE)) {
                return processResponse(request, response.get(), responseBody.get(), write);
            }
        }
    }

    private static boolean isRetryable(RawResponse response, byte[] responseBody) {
        return response.getStatus() == 429 || isIdempotencyKeyInUse(response, responseBody);
    }

    public static boolean isIdempotencyKeyInUse(RawResponse response, byte[] responseBody) {
        if (response.getStatus() != 409) {
            return false;
        }
        ErrorResponse errorResponse = ErrorResponse.parseIfAny(response, responseBody);
        return errorResponse != null && "IDEMPOTENCY_KEY_IN_USE".equals(errorResponse.firstErrorCode());
    }

    private static boolean isSafe(HttpRequest request) {
        return SAFE_METHODS.contains(request.getHttpMethod());
    }

    private boolean waitBeforeRetry(int retryCount, Write write, long deadline) {
        if (retryCount >= state.maxRetries) {
            return false;
        }
        long delay = (long) Math.pow(2, retryCount + 2) * 100;
        if (write != null && !write.mayRetryAfter(delay)) {
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

    private byte[] processResponse(HttpRequest request, RawResponse response, byte[] responseBody, Write write) {
        if (response.getStatus() >= 400) {
            throw SeatsioException.from(request, response, responseBody, write != null && write.routeHonorsKeys(), write != null && write.mayHaveRun);
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

    private class Write {

        private final HttpRequest<?> request;
        private final long start;
        private boolean mayHaveRun;

        Write(HttpRequest<?> request) {
            this.request = request;
            this.start = state.clock.millis();
        }

        void answered(RawResponse response, byte[] responseBody) {
            if (isIdempotencyKeyInUse(response, responseBody)) {
                mayHaveRun = true;
            }
            state.remember(IdempotencyKeyStatus.of(response));
        }

        boolean failed(UnirestException e) {
            if (neverSent(e)) {
                return true;
            }
            mayHaveRun = true;
            return routeHonorsKeys();
        }

        boolean mayRetryAfter(long delay) {
            return !mayHaveRun || startsWithinRetryWindow(delay);
        }

        private boolean startsWithinRetryWindow(long delay) {
            return state.clock.millis() + delay - start <= RETRY_WINDOW_MILLIS;
        }

        boolean routeHonorsKeys() {
            return state.honorsKeys();
        }

        SeatsioException connectionFailure(UnirestException e) {
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
