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

    protected SeatsioException(List<ApiError> errors, String requestId, HttpRequest request, RawResponse response) {
        super(exceptionMessage(errors, requestId, request, response));
        this.errors = errors;
        this.requestId = requestId;
    }

    private SeatsioException(HttpRequest request, RawResponse response) {
        super(exceptionMessage(request, response));
    }

    public static SeatsioException from(HttpRequest request, RawResponse response, byte[] responseBody) {
        if (response.getHeaders().getFirst("Content-Type").contains("application/json")) {
            SeatsioException parsedException = fromJson(responseBody);
            if (response.getStatus() == 429) {
                return new RateLimitExceededException(parsedException.errors, parsedException.requestId, request, response);
            }
            return new SeatsioException(parsedException.errors, parsedException.requestId, request, response);
        }
        return new SeatsioException(request, response);
    }

    private static String exceptionMessage(List<ApiError> errors, String requestId, HttpRequest request, RawResponse response) {
        String exceptionMessage = request.getHttpMethod() + " " + request.getUrl() + " resulted in a " + response.getStatus() + " " + response.getStatusText() + " response.";
        exceptionMessage += " Reason: " + errors.stream().map(ApiError::getMessage).collect(joining(", ")) + ".";
        exceptionMessage += " Request ID: " + requestId;
        return exceptionMessage;
    }

    private static String exceptionMessage(HttpRequest request, RawResponse response) {
        return request.getHttpMethod() + " " + request.getUrl() + " resulted in a " + response.getStatus() + " " + response.getStatusText() + " response.";
    }

    private static SeatsioException fromJson(byte[] responseBody) {
        return gson().fromJson(new String(responseBody, UTF_8), SeatsioException.class);
    }
}
