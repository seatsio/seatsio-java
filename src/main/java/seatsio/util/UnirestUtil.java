package seatsio.util;

import kong.unirest.*;
import seatsio.SeatsioException;

import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.base.Charsets.UTF_8;

public class UnirestUtil {

    private static int MAX_RETRIES = 5;
    private static UnirestInstance unirest = new UnirestInstance(new Config().concurrency(200, 200));

    public static String stringResponse(HttpRequest request) {
        byte[] response = execute(request, 0);
        return new String(response, UTF_8);
    }

    public static byte[] binaryResponse(HttpRequest request) {
        return execute(request, 0);
    }

    private static byte[] execute(HttpRequest request, int retryCount) {
        try {
            AtomicReference<RawResponse> response = new AtomicReference<>();
            AtomicReference<byte[]> responseBody = new AtomicReference<>();
            request.thenConsume(r -> {
                response.set((RawResponse) r);
                responseBody.set(response.get().getContentAsBytes());
            });
            if (retryCount >= MAX_RETRIES || response.get().getStatus() != 429) {
                return processResponse(request, response.get(), responseBody.get());
            } else {
                long waitTime = (long) Math.pow(2, retryCount + 2) * 100;
                sleep(waitTime);
                return execute(request, ++retryCount);
            }
        } catch (UnirestException e) {
            throw new SeatsioException(e, request);
        }
    }

    private static byte[] processResponse(HttpRequest request, RawResponse response, byte[] responseBody) {
        if (response.getStatus() >= 400) {
            throw SeatsioException.from(request, response, responseBody);
        } else {
            return responseBody;
        }
    }

    public static GetRequest get(String url, String secretKey, String workspaceKey) {
        return authenticate(unirest.get(url), secretKey, workspaceKey);
    }

    public static HttpRequestWithBody post(String url, String secretKey, String workspaceKey) {
        return authenticate(unirest.post(url), secretKey, workspaceKey);
    }

    public static HttpRequestWithBody delete(String url, String secretKey, String workspaceKey) {
        return authenticate(unirest.delete(url), secretKey, workspaceKey);
    }

    private static <T extends HttpRequest> T authenticate(T request, String secretKey, String workspaceKey) {
        request.basicAuth(secretKey, null);
        if (workspaceKey != null) {
            request.header("X-Workspace-Key", workspaceKey);
        }
        return request;
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
