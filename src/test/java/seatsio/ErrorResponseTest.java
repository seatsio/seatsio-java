package seatsio;

import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import seatsio.exceptions.RateLimitExceededException;
import seatsio.util.UnirestWrapper;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seatsio.util.UnirestWrapper.get;

public class ErrorResponseTest {

    private final MockWebServer server = new MockWebServer();

    @BeforeEach
    public void startServer() throws IOException {
        server.start();
    }

    @AfterEach
    public void stopServer() {
        server.close();
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"errors\": []}", "{\"errors\": null}", "{\"errors\": [null]}"})
    public void jsonObjectWithoutAnErrorCodeGivesAGenericMessageAndNoErrors(String body) {
        server.enqueue(json(500, body));

        SeatsioException e = assertThrows(SeatsioException.class, () -> new UnirestWrapper("secretKey", null).stringResponse(get(url())));

        assertThat(e.getMessage()).isEqualTo("GET " + url() + " resulted in a 500 Server Error response. Body: " + body);
        assertThat(e.errors).isEmpty();
    }

    @Test
    public void requestIdIsKeptWhenThereIsNoErrorCode() {
        String body = "{\"errors\": [], \"requestId\": \"abc\"}";
        server.enqueue(json(400, body));

        SeatsioException e = assertThrows(SeatsioException.class, () -> new UnirestWrapper("secretKey", null).stringResponse(get(url())));

        assertThat(e.getMessage()).isEqualTo("GET " + url() + " resulted in a 400 Client Error response. Body: " + body);
        assertThat(e.errors).isEmpty();
        assertThat(e.requestId).isEqualTo("abc");
    }

    @ParameterizedTest
    @ValueSource(strings = {"null", "malformed", "[]"})
    public void bodyThatIsNotAJsonObjectGivesAGenericException(String body) {
        server.enqueue(json(500, body));

        SeatsioException e = assertThrows(SeatsioException.class, () -> new UnirestWrapper("secretKey", null).stringResponse(get(url())));

        assertThat(e.getMessage()).isEqualTo("GET " + url() + " resulted in a 500 Server Error response. Body: " + body);
        assertThat(e.errors).isNull();
        assertThat(e.requestId).isNull();
    }

    @Test
    public void errorResponseWithoutContentTypeGivesAGenericException() {
        server.enqueue(new MockResponse.Builder().code(500).body("{}").build());

        SeatsioException e = assertThrows(SeatsioException.class, () -> new UnirestWrapper("secretKey", null).stringResponse(get(url())));

        assertThat(e.getMessage()).endsWith("resulted in a 500 Server Error response. Body: {}");
    }

    @Test
    public void rateLimitWithoutAnErrorCodeStillGivesARateLimitExceededException() {
        String body = "{\"errors\": [], \"requestId\": \"abc\"}";
        server.enqueue(json(429, body));

        SeatsioException e = assertThrows(RateLimitExceededException.class, () -> new UnirestWrapper("secretKey", null).maxRetries(0).stringResponse(get(url())));

        assertThat(e.getMessage()).endsWith("resulted in a 429 Client Error response. Body: " + body);
        assertThat(e.errors).isEmpty();
        assertThat(e.requestId).isEqualTo("abc");
    }

    private String url() {
        return server.url("/").toString();
    }

    private static MockResponse json(int code, String body) {
        return new MockResponse.Builder().code(code).setHeader("Content-Type", "application/json").body(body).build();
    }
}
