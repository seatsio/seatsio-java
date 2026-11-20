package seatsio.util;

import kong.unirest.*;
import org.apache.http.client.ClientProtocolException;
import seatsio.SeatsioClient;
import seatsio.SeatsioException;

import java.io.IOException;
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

    private final String secretKey;
    private final String workspaceKey;
    private final String idempotencyKey;
    private final RetrySettings retrySettings;

    public UnirestWrapper(String secretKey, String workspaceKey) {
        this(secretKey, workspaceKey, null, new RetrySettings());
    }

    private UnirestWrapper(String secretKey, String workspaceKey, String idempotencyKey, RetrySettings retrySettings) {
        this.secretKey = secretKey;
        this.workspaceKey = workspaceKey;
        this.idempotencyKey = idempotencyKey;
        this.retrySettings = retrySettings;
    }

    public UnirestWrapper withIdempotencyKey(String idempotencyKey) {
        if (idempotencyKey == null || !V4_UUID.matcher(idempotencyKey).matches()) {
            throw new IllegalArgumentException("Idempotency key " + idempotencyKey + " is not a v4 UUID");
        }
        return new UnirestWrapper(secretKey, workspaceKey, idempotencyKey, retrySettings);
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
        for (int retryCount = 0; ; retryCount++) {
            try {
                AtomicReference<RawResponse> response = new AtomicReference<>();
                AtomicReference<byte[]> responseBody = new AtomicReference<>();
                request.thenConsume(r -> {
                    response.set((RawResponse) r);
                    responseBody.set(response.get().getContentAsBytes());
                });
                if (!isRetryable(response.get(), responseBody.get()) || !waitBeforeRetry(retryCount)) {
                    return processResponse(request, response.get(), responseBody.get());
                }
            } catch (UnirestException e) {
                if (!isRetryable(request, e) || !waitBeforeRetry(retryCount)) {
                    throw new SeatsioException(e, request);
                }
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

    private boolean isRetryable(HttpRequest request, UnirestException e) {
        return retrySettings.retryWritesOnNetworkErrors
                && !isSafe(request)
                && e.getCause() instanceof IOException
                && !(e.getCause() instanceof ClientProtocolException);
    }

    private static boolean isSafe(HttpRequest request) {
        return SAFE_METHODS.contains(request.getHttpMethod());
    }

    private boolean waitBeforeRetry(int retryCount) {
        if (retryCount >= retrySettings.maxRetries) {
            return false;
        }
        try {
            Thread.sleep((long) Math.pow(2, retryCount + 2) * 100);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
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

    public UnirestWrapper maxRetries(int maxRetries) {
        retrySettings.maxRetries = maxRetries;
        return this;
    }

    public UnirestWrapper retryWritesOnNetworkErrors(boolean retryWritesOnNetworkErrors) {
        retrySettings.retryWritesOnNetworkErrors = retryWritesOnNetworkErrors;
        return this;
    }

    private static class RetrySettings {
        volatile int maxRetries = 5;
        volatile boolean retryWritesOnNetworkErrors = false;
    }

    public static void reinitializeHttpConnectionPool() {
        unirest.shutDown(false);
    }
}
