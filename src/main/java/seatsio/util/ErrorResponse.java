package seatsio.util;

import com.google.gson.JsonParseException;
import kong.unirest.RawResponse;
import seatsio.ApiError;

import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static seatsio.json.SeatsioGson.gson;

public class ErrorResponse {

    public List<ApiError> errors;
    public String requestId;

    public static ErrorResponse parseIfAny(RawResponse response, byte[] responseBody) {
        if (!response.getHeaders().getFirst("Content-Type").contains("application/json")) {
            return null;
        }
        try {
            ErrorResponse errorResponse = gson().fromJson(new String(responseBody, UTF_8), ErrorResponse.class);
            if (errorResponse == null || errorResponse.errors == null || errorResponse.errors.isEmpty() || errorResponse.errors.contains(null)) {
                return null;
            }
            return errorResponse;
        } catch (JsonParseException e) {
            return null;
        }
    }

    public String firstErrorCode() {
        return errors.get(0).getCode();
    }
}
