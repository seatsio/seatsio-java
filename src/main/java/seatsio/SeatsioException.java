package seatsio;

import kong.unirest.HttpRequest;
import kong.unirest.RawResponse;
import seatsio.events.BestAvailableObjectsNotFoundException;
import seatsio.exceptions.RateLimitExceededException;
import seatsio.util.ErrorResponse;
import seatsio.util.UnirestWrapper;

import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;

public class SeatsioException extends RuntimeException {

    public List<ApiError> errors;
    public String requestId;
    public String idempotencyKey;
    private boolean outcomeIsUnknown;

    public SeatsioException(String message) {
        super(message);
    }

    public SeatsioException(Throwable t, HttpRequest request) {
        super("Error while executing " + request.getHttpMethod() + " " + request.getUrl(), t);
        this.idempotencyKey = idempotencyKey(request);
        this.outcomeIsUnknown = true;
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
        SeatsioException exception = create(request, response, responseBody);
        exception.idempotencyKey = idempotencyKey(request);
        exception.outcomeIsUnknown = response.getStatus() >= 500 || UnirestWrapper.isIdempotencyKeyInUse(response, responseBody);
        return exception;
    }

    /**
     * True if the request may or may not have been executed: the connection failed, the server answered with a 5xx, or the key was still in use after all retries (the first request may still succeed).
     * Only then can retrying the same operation (with the same idempotencyKey) give a different result.
     */
    public boolean outcomeIsUnknown() {
        return outcomeIsUnknown;
    }

    private static String idempotencyKey(HttpRequest request) {
        String key = request.getHeaders().getFirst(SeatsioClient.IDEMPOTENCY_KEY_HEADER);
        return key.isEmpty() ? null : key;
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
