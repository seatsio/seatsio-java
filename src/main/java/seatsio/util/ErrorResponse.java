package seatsio.util;

import com.google.gson.JsonParseException;
import kong.unirest.RawResponse;
import seatsio.ApiError;

import java.util.List;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;
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
            if (errorResponse == null) {
                return null;
            }
            errorResponse.errors = errorResponse.errors == null ? List.of() : errorResponse.errors.stream().filter(Objects::nonNull).collect(toList());
            return errorResponse;
        } catch (JsonParseException e) {
            return null;
        }
    }

    public static boolean isIdempotencyKeyInUse(RawResponse response, byte[] responseBody) {
        if (response.getStatus() != 409) {
            return false;
        }
        ErrorResponse errorResponse = parseIfAny(response, responseBody);
        return errorResponse != null && "IDEMPOTENCY_KEY_IN_USE".equals(errorResponse.firstErrorCode());
    }

    public boolean hasErrorCode() {
        return !errors.isEmpty();
    }

    public String firstErrorCode() {
        return hasErrorCode() ? errors.get(0).getCode() : null;
    }
}
