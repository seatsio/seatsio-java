package seatsio.util;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;

public class UnirestUtil {

    public static <T> HttpResponse<T> unirest(UnirestExceptionThrowingSupplier<HttpResponse<T>> supplier) {
        try {
            HttpResponse<T> response = supplier.get();
            if (response.getStatus() >= 400) {
                throw new RuntimeException("Error: " + response.getStatus() + ". Reason: " + response.getBody().toString());
            }
            return response;
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }
}
