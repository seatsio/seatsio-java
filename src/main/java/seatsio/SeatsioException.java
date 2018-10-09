package seatsio;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.request.HttpRequest;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

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

    private SeatsioException(List<ApiError> errors, String requestId, HttpRequest request, HttpResponse response) {
        super(exceptionMessage(errors, requestId, request, response));
        this.errors = errors;
        this.requestId = requestId;
    }

    private SeatsioException(HttpRequest request, HttpResponse response) {
        super(exceptionMessage(request, response));
    }

    public static SeatsioException from(HttpRequest request, HttpResponse response) {
        if (response.getHeaders().getFirst("Content-Type").contains("application/json")) {
            SeatsioException parsedException = fromJson(response);
            return new SeatsioException(parsedException.errors, parsedException.requestId, request, response);
        }
        return new SeatsioException(request, response);
    }

    private static String exceptionMessage(List<ApiError> errors, String requestId, HttpRequest request, HttpResponse response) {
        String exceptionMessage = request.getHttpMethod() + " " + request.getUrl() + " resulted in a " + response.getStatus() + " " + response.getStatusText() + " response.";
        exceptionMessage += " Reason: " + errors.stream().map(ApiError::getMessage).collect(joining(", ")) + ".";
        exceptionMessage += " Request ID: " + requestId;
        return exceptionMessage;
    }

    private static String exceptionMessage(HttpRequest request, HttpResponse response) {
        return request.getHttpMethod() + " " + request.getUrl() + " resulted in a " + response.getStatus() + " " + response.getStatusText() + " response.";
    }

    private static SeatsioException fromJson(HttpResponse<?> response) {
        try {
            return gson().fromJson(new InputStreamReader(response.getRawBody(), "UTF-8"), SeatsioException.class);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
