package seatsio;

import kong.unirest.HttpRequest;
import kong.unirest.RawResponse;
import seatsio.events.BestAvailableObjectsNotFoundException;
import seatsio.exceptions.RateLimitExceededException;
import seatsio.util.ErrorResponse;
import seatsio.util.IdempotencyKeyStatus;
import seatsio.util.UnirestWrapper;

import java.util.List;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;

public class SeatsioException extends RuntimeException {

    public List<ApiError> errors;
    public String requestId;
    public UUID idempotencyKey;
    /**
     * Whether the server honored the idempotency key of this request, according to the Idempotency-Key-Status header of
     * the response: true or false, or null when there was no response or the response didn't say.
     */
    public Boolean idempotencyKeyHonored;
    /**
     * True if the server didn't execute the request again but replayed the recorded response of an earlier request with
     * the same idempotency key.
     */
    public boolean replayed;
    private boolean outcomeIsUnknown;
    private boolean safeToRetryWithSameKey;

    public SeatsioException(String message) {
        super(message);
    }

    public SeatsioException(Throwable t, HttpRequest request) {
        this(t, request, true, false);
    }

    private SeatsioException(Throwable t, HttpRequest request, boolean outcomeIsUnknown, boolean safeToRetryWithSameKey) {
        super("Error while executing " + request.getHttpMethod() + " " + request.getUrl(), t);
        this.idempotencyKey = idempotencyKey(request);
        this.outcomeIsUnknown = outcomeIsUnknown;
        this.safeToRetryWithSameKey = safeToRetryWithSameKey && idempotencyKey != null;
    }

    /**
     * A request that got no response.
     *
     * @param outcomeIsUnknown       false if the request never reached the server
     * @param safeToRetryWithSameKey true if a retry with the same idempotency key can't execute the request twice
     */
    public static SeatsioException fromConnectionFailure(Throwable t, HttpRequest request, boolean outcomeIsUnknown, boolean safeToRetryWithSameKey) {
        return new SeatsioException(t, request, outcomeIsUnknown, safeToRetryWithSameKey);
    }

    protected SeatsioException(List<ApiError> errors, String requestId) {
        super(exceptionMessage(errors));
        this.errors = errors;
        this.requestId = requestId;
    }

    protected SeatsioException(String message, List<ApiError> errors, String requestId) {
        super(message);
        this.errors = errors;
        this.requestId = requestId;
    }

    private SeatsioException(HttpRequest request, RawResponse response, byte[] responseBody) {
        super(exceptionMessage(request, response, responseBody));
    }

    public static SeatsioException from(HttpRequest request, RawResponse response, byte[] responseBody) {
        return from(request, response, responseBody, false, false);
    }

    /**
     * @param routeHonorsKeys whether the last response of the same route said the server honors idempotency keys for it.
     *                        Used when this response doesn't say, e.g. a 502 from a proxy.
     * @param mayHaveRun      whether an earlier attempt of this request may have been executed (e.g. its response was
     *                        lost), whatever status the final response has.
     */
    public static SeatsioException from(HttpRequest request, RawResponse response, byte[] responseBody, boolean routeHonorsKeys, boolean mayHaveRun) {
        SeatsioException exception = create(request, response, responseBody);
        IdempotencyKeyStatus status = IdempotencyKeyStatus.of(response);
        exception.idempotencyKey = idempotencyKey(request);
        exception.idempotencyKeyHonored = status == null ? null : status.keyHonored();
        exception.replayed = status == IdempotencyKeyStatus.REPLAYED;
        exception.outcomeIsUnknown = response.getStatus() >= 500
                || UnirestWrapper.isIdempotencyKeyInUse(response, responseBody)
                || (mayHaveRun && !exception.replayed);
        exception.safeToRetryWithSameKey = exception.outcomeIsUnknown
                && exception.idempotencyKey != null
                && (status == null ? routeHonorsKeys : status.keyHonored())
                && !exception.replayed;
        return exception;
    }

    /**
     * True if the request may or may not have been executed: the response was lost after the request was sent, the server
     * answered with a 5xx, the key was still in use after all retries (the first request may still succeed),
     * or an earlier attempt may have been executed and the final response is an error that doesn't say.
     * False for a request that never reached the server, and for a 4xx of a request no attempt of which may have run,
     * other than IDEMPOTENCY_KEY_IN_USE.
     */
    public boolean outcomeIsUnknown() {
        return outcomeIsUnknown;
    }

    /**
     * True if the same call, made again with this exception's idempotencyKey (see SeatsioClient.withIdempotencyKey()),
     * can't execute the request twice and may give a different result. That holds when the request never reached the
     * server, or when its outcome is unknown and the server honors idempotency keys for it: known from the response or,
     * when the response was lost, from the last response of the same route. Retry promptly: the server keeps a key for
     * 10 minutes.
     */
    public boolean isSafeToRetryWithSameKey() {
        return safeToRetryWithSameKey;
    }

    private static UUID idempotencyKey(HttpRequest request) {
        String key = request.getHeaders().getFirst(SeatsioClient.IDEMPOTENCY_KEY_HEADER);
        return key.isEmpty() ? null : UUID.fromString(key);
    }

    private static SeatsioException create(HttpRequest request, RawResponse response, byte[] responseBody) {
        ErrorResponse errorResponse = ErrorResponse.parseIfAny(response, responseBody);
        if (errorResponse == null) {
            if (response.getStatus() == 429) {
                return new RateLimitExceededException(exceptionMessage(request, response, responseBody));
            }
            return new SeatsioException(request, response, responseBody);
        }
        if (!errorResponse.hasErrorCode()) {
            String message = exceptionMessage(request, response, responseBody);
            if (response.getStatus() == 429) {
                return new RateLimitExceededException(message, errorResponse.errors, errorResponse.requestId);
            }
            return new SeatsioException(message, errorResponse.errors, errorResponse.requestId);
        }
        if (response.getStatus() == 429) {
            return new RateLimitExceededException(errorResponse.errors, errorResponse.requestId);
        } else if (isBestAvailableObjectsNotFound(errorResponse)) {
            return new BestAvailableObjectsNotFoundException(errorResponse.errors, errorResponse.requestId);
        }
        return new SeatsioException(errorResponse.errors, errorResponse.requestId);
    }

    private static boolean isBestAvailableObjectsNotFound(ErrorResponse errorResponse) {
        return errorResponse.errors.stream().anyMatch(e -> "BEST_AVAILABLE_OBJECTS_NOT_FOUND".equals(e.getCode()));
    }

    private static String exceptionMessage(List<ApiError> errors) {
        return errors.stream().map(ApiError::getMessage).collect(joining(", "));
    }

    private static String exceptionMessage(HttpRequest request, RawResponse response, byte[] responseBody) {
        return request.getHttpMethod() + " " + request.getUrl() + " resulted in a " + response.getStatus() + " " + response.getStatusText() + " response. Body: " + new String(responseBody, UTF_8);
    }
}
