package seatsio.util;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.BaseRequest;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
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

    public static GetRequest get(String url, String secretKey, String workspaceKey) {
        return authenticate(Unirest.get(url), secretKey, workspaceKey);
    }

    public static HttpRequestWithBody post(String url, String secretKey, String workspaceKey) {
        return authenticate(Unirest.post(url), secretKey, workspaceKey);
    }

    public static HttpRequestWithBody delete(String url, String secretKey, String workspaceKey) {
        return authenticate(Unirest.delete(url), secretKey, workspaceKey);
    }

    private static <T extends HttpRequest> T authenticate(T request, String secretKey, String workspaceKey) {
        request.basicAuth(secretKey, null);
        if (workspaceKey != null) {
            request.header("X-Workspace-Key", workspaceKey);
        }
        return request;
    }

}
