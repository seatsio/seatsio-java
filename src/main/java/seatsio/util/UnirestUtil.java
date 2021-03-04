package seatsio.util;

import kong.unirest.*;
import seatsio.SeatsioException;

import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.base.Charsets.UTF_8;

public class UnirestUtil {

    public static String stringResponse(HttpRequest request) {
        byte[] response = execute(request);
        return new String(response, UTF_8);
    }

    public static byte[] binaryResponse(HttpRequest request) {
        return execute(request);
    }

    private static byte[] execute(HttpRequest request) {
        AtomicReference<byte[]> normalResponse = new AtomicReference<>();
        AtomicReference<SeatsioException> errorResponse = new AtomicReference<>();

        try {
            request.thenConsume(r -> {
                RawResponse rawResponse = (RawResponse) r;
                if (rawResponse.getStatus() >= 400) {
                    errorResponse.set(SeatsioException.from(request, rawResponse));
                } else {
                    normalResponse.set(rawResponse.getContentAsBytes());
                }
            });
        } catch (RuntimeException e) {
            throw new SeatsioException(e, request);
        }

        if (normalResponse.get() != null) {
            return normalResponse.get();
        } else {
            throw errorResponse.get();
        }
    }

    public static GetRequest get(String url, String secretKey, String workspaceKey) {
        return authenticate(Unirest.get(url), secretKey, workspaceKey);
    }

    public static GetRequest get(String url, String secretKey) {
        return authenticate(Unirest.get(url), secretKey, null);
    }

    public static HttpRequestWithBody post(String url, String secretKey, String workspaceKey) {
        return authenticate(Unirest.post(url), secretKey, workspaceKey);
    }

    public static HttpRequestWithBody post(String url, String secretKey) {
        return authenticate(Unirest.post(url), secretKey, null);
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
