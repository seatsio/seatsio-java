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
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

public class UnirestWrapper {

    private static UnirestInstance unirest = new UnirestInstance(new Config()
            .socketTimeout(10000)
            .concurrency(200, 200));

    private static final Set<HttpMethod> SAFE_METHODS = Set.of(HttpMethod.GET, HttpMethod.HEAD, HttpMethod.OPTIONS, HttpMethod.TRACE);
    private static final Pattern V4_UUID = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}", Pattern.CASE_INSENSITIVE);

    static final long RETRY_WINDOW_MILLIS = Duration.ofMinutes(5).toMillis();

    private final String secretKey;
    private final String workspaceKey;
    private final String idempotencyKey;
    private final SharedState state;

    public UnirestWrapper(String secretKey, String workspaceKey) {
        this(secretKey, workspaceKey, RetryClock.SYSTEM);
    }

    UnirestWrapper(String secretKey, String workspaceKey, RetryClock clock) {
        this(secretKey, workspaceKey, null, new SharedState(clock));
    }

    private UnirestWrapper(String secretKey, String workspaceKey, String idempotencyKey, SharedState state) {
        this.secretKey = secretKey;
        this.workspaceKey = workspaceKey;
        this.idempotencyKey = idempotencyKey;
        this.state = state;
    }

    public UnirestWrapper withIdempotencyKey(String idempotencyKey) {
        if (idempotencyKey == null || !V4_UUID.matcher(idempotencyKey).matches()) {
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
            request.header(SeatsioClient.IDEMPOTENCY_KEY_HEADER, idempotencyKey != null ? idempotencyKey : UUID.randomUUID().toString());
        }
    }

    private byte[] execute(HttpRequest request) {
        Write write = isSafe(request) ? null : new Write(request);
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
                    // The HTTP client already retries a GET whose response was lost, so retrying those here would
                    // multiply the attempts. It does not retry a connection that could not be set up: do that here.
                    if (neverSent(e) && waitBeforeRetry(retryCount, null)) {
                        continue;
                    }
                    throw SeatsioException.fromConnectionFailure(e, request, !neverSent(e), false);
                }
                if (write.failed(e) && waitBeforeRetry(retryCount, write)) {
                    continue;
                }
                throw write.connectionFailure(e);
            }
            if (write != null) {
                write.answered(response.get(), responseBody.get());
            }
            if (!isRetryable(response.get(), responseBody.get()) || !waitBeforeRetry(retryCount, write)) {
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

    private boolean waitBeforeRetry(int retryCount, Write write) {
        if (retryCount >= state.maxRetries) {
            return false;
        }
        long delay = (long) Math.pow(2, retryCount + 2) * 100;
        if (write != null && !write.mayRetryAfter(delay)) {
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
            throw SeatsioException.from(request, response, responseBody, write != null && write.routeHonorsKeys());
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

    /**
     * Whether a connection failure happened before any byte of the request reached the server, so the request cannot
     * have run. This is a whitelist: any other failure (a read timeout, a connection reset, a broken pipe on a stale
     * pooled connection, ...) is treated as "may have been sent", which is the safe default.
     */
    static boolean neverSent(UnirestException e) {
        Throwable cause = e.getCause();
        // ConnectTimeoutException is an InterruptedIOException, so it must be checked by type, not by "timeout"
        return cause instanceof ConnectException
                || cause instanceof ConnectTimeoutException
                || cause instanceof UnknownHostException
                || cause instanceof SSLHandshakeException;
    }

    /**
     * The attempts of one POST, PUT, DELETE or PATCH request, all sent with the same Idempotency-Key.
     */
    private class Write {

        private final HttpRequest<?> request;
        private final long start;
        private boolean mayHaveReachedTheServer;
        private boolean mayHaveRun;

        Write(HttpRequest<?> request) {
            this.request = request;
            this.start = state.clock.millis();
        }

        void answered(RawResponse response, byte[] responseBody) {
            mayHaveReachedTheServer = true;
            if (isIdempotencyKeyInUse(response, responseBody)) {
                mayHaveRun = true;
            }
            state.remember(IdempotencyKeyStatus.of(response));
        }

        /**
         * Records a connection failure. Returns whether the SDK may retry it, retries left and time permitting.
         */
        boolean failed(UnirestException e) {
            if (neverSent(e)) {
                return true;
            }
            mayHaveReachedTheServer = true;
            mayHaveRun = true;
            return routeHonorsKeys();
        }

        boolean mayRetryAfter(long delay) {
            return !mayHaveReachedTheServer || startsWithinRetryWindow(delay);
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

    /**
     * Shared by a wrapper and the copies made with withIdempotencyKey(): they use the same secret key, so the same
     * company, and the server honors keys for the same writes.
     */
    private static class SharedState {
        final RetryClock clock;
        volatile int maxRetries = 5;
        /**
         * Whether the last response with a known Idempotency-Key-Status said the server honors idempotency keys.
         * One flag for all writes: the kill switch and the company flag apply to every POST and DELETE at once.
         * null means unknown.
         */
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
