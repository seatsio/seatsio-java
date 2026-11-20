package seatsio;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import kong.unirest.Unirest;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import seatsio.util.UnirestWrapper;

import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seatsio.Httpbin.httpbinUrl;
import static seatsio.util.UnirestWrapper.delete;
import static seatsio.util.UnirestWrapper.get;
import static seatsio.util.UnirestWrapper.post;

public class IdempotencyKeyTest {

    @Test
    public void postsGetAnIdempotencyKey() {
        String key = sentIdempotencyKey(new UnirestWrapper("secretKey", null).stringResponse(post(httpbinUrl("/anything")).body("{}")));

        assertThat(UUID.fromString(key).version()).isEqualTo(4);
    }

    @Test
    public void deletesGetAnIdempotencyKey() {
        String key = sentIdempotencyKey(new UnirestWrapper("secretKey", null).stringResponse(delete(httpbinUrl("/anything"))));

        assertThat(UUID.fromString(key).version()).isEqualTo(4);
    }

    @Test
    public void getsDoNotGetAnIdempotencyKey() {
        String response = new UnirestWrapper("secretKey", null).stringResponse(get(httpbinUrl("/anything")));

        assertThat(JsonParser.parseString(response).getAsJsonObject().getAsJsonObject("headers").has("Idempotency-Key")).isFalse();
    }

    @Test
    public void eachRequestGetsItsOwnKey() {
        UnirestWrapper wrapper = new UnirestWrapper("secretKey", null);

        String first = sentIdempotencyKey(wrapper.stringResponse(post(httpbinUrl("/anything")).body("{}")));
        String second = sentIdempotencyKey(wrapper.stringResponse(post(httpbinUrl("/anything")).body("{}")));

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    public void failedRequestsExposeTheirIdempotencyKey() {
        try {
            new UnirestWrapper("secretKey", null).stringResponse(post(httpbinUrl("/status/500")).body("{}"));
            fail("expected exception");
        } catch (SeatsioException e) {
            assertThat(UUID.fromString(e.idempotencyKey).version()).isEqualTo(4);
        }
    }

    @Test
    public void connectionFailuresExposeTheirIdempotencyKey() {
        try {
            new UnirestWrapper("secretKey", null).stringResponse(post("http://localhost:1/anything").body("{}"));
            fail("expected exception");
        } catch (SeatsioException e) {
            assertThat(UUID.fromString(e.idempotencyKey).version()).isEqualTo(4);
        }
    }

    @Test
    public void aClientCanRetryWithTheSameIdempotencyKey() {
        String key = UUID.randomUUID().toString();
        UnirestWrapper wrapper = new UnirestWrapper("secretKey", null).withIdempotencyKey(key);

        assertThat(sentIdempotencyKey(wrapper.stringResponse(post(httpbinUrl("/anything")).body("{}")))).isEqualTo(key);
        assertThat(sentIdempotencyKey(wrapper.stringResponse(post(httpbinUrl("/anything")).body("{}")))).isEqualTo(key);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "notAUuid", "f81d4fae-7dec-11d0-a765-00a0c91e6bf6", "00000000-0000-0000-0000-000000000000", "1-1-1-1-1"})
    public void suppliedKeyMustBeAV4Uuid(String key) {
        assertThrows(IllegalArgumentException.class, () -> new SeatsioClient("secretKey", null, "http://localhost:1").withIdempotencyKey(key));
    }

    @Test
    public void suppliedKeyIsCaseInsensitive() {
        String key = UUID.randomUUID().toString().toUpperCase();

        String sentKey = sentIdempotencyKey(new UnirestWrapper("secretKey", null).withIdempotencyKey(key).stringResponse(post(httpbinUrl("/anything")).body("{}")));

        assertThat(sentKey).isEqualTo(key);
    }

    @Test
    public void putsGetAnIdempotencyKey() {
        String key = sentIdempotencyKey(new UnirestWrapper("secretKey", null).stringResponse(Unirest.put(httpbinUrl("/anything")).body("{}")));

        assertThat(UUID.fromString(key).version()).isEqualTo(4);
    }

    @Test
    public void patchesGetAnIdempotencyKey() {
        String key = sentIdempotencyKey(new UnirestWrapper("secretKey", null).stringResponse(Unirest.patch(httpbinUrl("/anything")).body("{}")));

        assertThat(UUID.fromString(key).version()).isEqualTo(4);
    }

    @ParameterizedTest
    @MethodSource("errorResponses")
    public void errorResponsesExposeTheKey(MockResponse reply) throws Exception {
        try (MockWebServer server = new MockWebServer()) {
            server.enqueue(reply);
            server.start();

            SeatsioException e = assertThrows(SeatsioException.class, () -> new UnirestWrapper("secretKey", null).stringResponse(post(server.url("/").toString()).body("{}")));

            assertThat(e.idempotencyKey).isEqualTo(server.takeRequest().getHeaders().get(SeatsioClient.IDEMPOTENCY_KEY_HEADER));
        }
    }

    @ParameterizedTest
    @MethodSource("errorResponses")
    public void errorResponsesOfGetsHaveNoKey(MockResponse reply) throws Exception {
        try (MockWebServer server = new MockWebServer()) {
            server.enqueue(reply);
            server.start();

            SeatsioException e = assertThrows(SeatsioException.class, () -> new UnirestWrapper("secretKey", null).stringResponse(get(server.url("/").toString())));

            assertThat(e.idempotencyKey).isNull();
        }
    }

    static Stream<MockResponse> errorResponses() {
        return Stream.of(
                json(400, "{\"errors\": [{\"code\": \"EVENT_NOT_FOUND\", \"message\": \"EVENT_NOT_FOUND\"}]}"),
                json(500, "{}"),
                json(500, "{\"errors\": []}"),
                json(500, "malformed"),
                new MockResponse.Builder().code(500).setHeader("Content-Type", "text/plain").build(),
                new MockResponse.Builder().code(500).body("{}").build()
        );
    }

    private static MockResponse json(int code, String body) {
        return new MockResponse.Builder().code(code).setHeader("Content-Type", "application/json").body(body).build();
    }

    @Test
    public void connectionFailuresOfGetsHaveNoKey() {
        SeatsioException e = assertThrows(SeatsioException.class, () -> new UnirestWrapper("secretKey", null).stringResponse(get("http://localhost:1/anything")));

        assertThat(e.idempotencyKey).isNull();
    }

    private static String sentIdempotencyKey(String httpbinResponse) {
        JsonElement header = JsonParser.parseString(httpbinResponse).getAsJsonObject().getAsJsonObject("headers").get("Idempotency-Key");
        // httpbingo returns header values as arrays, httpbin as strings
        return header.isJsonArray() ? header.getAsJsonArray().get(0).getAsString() : header.getAsString();
    }
}
