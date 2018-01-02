package seatsio.util;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.BaseRequest;
import seatsio.SeatsioException;

import java.io.InputStream;

public class UnirestUtil {

    public static HttpResponse<String> stringResponse(BaseRequest request) {
        return handleExceptions(request, request::asString);
    }

    public static HttpResponse<InputStream> binaryResponse(BaseRequest request) {
        return handleExceptions(request, request::asBinary);
    }

    private static <T> HttpResponse<T> handleExceptions(BaseRequest request, UnirestExceptionThrowingSupplier<HttpResponse<T>> s) {
        HttpResponse<T> response = execute(request, s);
        if (response.getStatus() >= 400) {
            throw SeatsioException.from(request.getHttpRequest(), response);
        }
        return response;
    }

    private static <T> HttpResponse<T> execute(BaseRequest request, UnirestExceptionThrowingSupplier<HttpResponse<T>> s) {
        try {
            return s.get();
        } catch (UnirestException | RuntimeException e) {
            throw new SeatsioException(e, request.getHttpRequest());
        }
    }
}
