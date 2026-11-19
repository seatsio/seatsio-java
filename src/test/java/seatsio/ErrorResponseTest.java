package seatsio;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import seatsio.FakeServer.Reply;
import seatsio.exceptions.RateLimitExceededException;
import seatsio.util.UnirestWrapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seatsio.util.UnirestWrapper.get;

public class ErrorResponseTest {

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"errors\": []}", "{\"errors\": null}", "null", "malformed", "[]", "{\"errors\": [null]}"})
    public void jsonErrorResponseWithoutAnErrorCodeGivesAGenericException(String body) throws Exception {
        try (FakeServer server = FakeServer.replying(Reply.json(500, body))) {
            SeatsioException e = assertThrows(SeatsioException.class, () -> new UnirestWrapper("secretKey", null).stringResponse(get(server.url("/"))));

            assertThat(e.getMessage()).isEqualTo("GET " + server.url("/") + " resulted in a 500 Fake response. Body: " + body);
            assertThat(e.errors).isNull();
        }
    }

    @Test
    public void errorResponseWithoutContentTypeGivesAGenericException() throws Exception {
        try (FakeServer server = FakeServer.replying(Reply.withoutContentType(500, "{}"))) {
            SeatsioException e = assertThrows(SeatsioException.class, () -> new UnirestWrapper("secretKey", null).stringResponse(get(server.url("/"))));

            assertThat(e.getMessage()).endsWith("resulted in a 500 Fake response. Body: {}");
        }
    }

    @Test
    public void rateLimitWithoutAnErrorCodeStillGivesARateLimitExceededException() throws Exception {
        try (FakeServer server = FakeServer.replying(Reply.json(429, "{}"))) {
            SeatsioException e = assertThrows(RateLimitExceededException.class, () -> new UnirestWrapper("secretKey", null).maxRetries(0).stringResponse(get(server.url("/"))));

            assertThat(e.getMessage()).endsWith("resulted in a 429 Fake response. Body: {}");
        }
    }
}
