package seatsio.exceptions;

import kong.unirest.HttpRequest;
import kong.unirest.RawResponse;
import seatsio.ApiError;
import seatsio.SeatsioException;

import java.util.List;

public class RateLimitExceededException extends SeatsioException {

    public RateLimitExceededException(List<ApiError> errors, String requestId, HttpRequest request, RawResponse response) {
        super(errors, requestId, request, response);
    }
}
