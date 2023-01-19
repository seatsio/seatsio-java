package seatsio;

import kong.unirest.HttpRequest;
import kong.unirest.RawResponse;
import seatsio.exceptions.RateLimitExceededException;

import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;
import static seatsio.json.SeatsioGson.gson;

public class SeatsioException extends RuntimeException {

    public List<ApiError> errors;
    public String requestId;

    public SeatsioException(String message) {
        super(message);
    }

    public SeatsioException(Throwable t, HttpRequest request) {
        super("Error while executing " + request.getHttpMethod() + " " + request.getUrl(), t);
    }

    protected SeatsioException(List<ApiError> errors, String requestId) {
        super(exceptionMessage(errors));
        this.errors = errors;
        this.requestId = requestId;
    }

    private SeatsioException(HttpRequest request, RawResponse response) {
        super(exceptionMessage(request, response));
    }

    public static SeatsioException from(HttpRequest request, RawResponse response, byte[] responseBody) {
        if (response.getHeaders().getFirst("Content-Type").contains("application/json")) {
            SeatsioExceptionTO parsedException = fromJson(responseBody);
            if (response.getStatus() == 429) {
                return new RateLimitExceededException(parsedException.errors, parsedException.requestId);
            }
            return new SeatsioException(parsedException.errors, parsedException.requestId);
        }
        return new SeatsioException(request, response);
    }

    private static String exceptionMessage(List<ApiError> errors) {
        return errors.stream().map(ApiError::getMessage).collect(joining(", "));
    }

    private static String exceptionMessage(HttpRequest request, RawResponse response) {
        return request.getHttpMethod() + " " + request.getUrl() + " resulted in a " + response.getStatus() + " " + response.getStatusText() + " response. Body: " + response.getContentAsString();
    }

    private static SeatsioExceptionTO fromJson(byte[] responseBody) {
        return gson().fromJson(new String(responseBody, UTF_8), SeatsioExceptionTO.class);
    }

    private static class SeatsioExceptionTO {

        public List<ApiError> errors;
        public String requestId;

    }
}
