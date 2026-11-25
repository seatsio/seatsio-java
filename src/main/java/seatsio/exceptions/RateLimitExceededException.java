package seatsio.exceptions;

import seatsio.ApiError;
import seatsio.SeatsioException;

import java.util.List;

public class RateLimitExceededException extends SeatsioException {

    public RateLimitExceededException(List<ApiError> errors, String requestId) {
        super(errors, requestId);
    }

    public RateLimitExceededException(String message) {
        super(message);
    }

    public RateLimitExceededException(String message, List<ApiError> errors, String requestId) {
        super(message, errors, requestId);
    }
}
