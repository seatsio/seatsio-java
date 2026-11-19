package seatsio;

import kong.unirest.HttpRequest;
import kong.unirest.Unirest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import seatsio.exceptions.RateLimitExceededException;
import seatsio.util.UnirestWrapper;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seatsio.FakeServer.Reply.closeConnectionWithoutAnswering;
import static seatsio.FakeServer.Reply.error;
import static seatsio.FakeServer.Reply.json;
import static seatsio.FakeServer.Reply.status;

public class IdempotentRetryTest {

    private static final int APACHE_DEFAULT_ATTEMPTS = 1 + 3;

    private FakeServer server;

    @AfterEach
    public void stopServer() throws Exception {
        Thread.interrupted();
        if (server != null) {
            server.close();
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"POST", "PUT", "DELETE", "PATCH"})
    public void writesAreNotRetriedByDefaultWhenTheConnectionFails(String method) {
        server = FakeServer.replying(closeConnectionWithoutAnswering(), status(200));

        SeatsioException e = assertThrows(SeatsioException.class, () -> wrapper().stringResponse(request(method)));

        assertThat(server.requests()).hasSize(1);
        assertThat(e.idempotencyKey).isEqualTo(server.sentIdempotencyKeys().get(0));
    }

    @ParameterizedTest
    @ValueSource(strings = {"POST", "PUT", "DELETE", "PATCH"})
    public void writesAreRetriedWithTheSameKeyWhenEnabled(String method) {
        server = FakeServer.replying(closeConnectionWithoutAnswering(), status(200));

        wrapper().retryNonIdempotentRequests(true).stringResponse(request(method));

        assertThat(server.sentIdempotencyKeys()).hasSize(2).doesNotContainNull();
        assertThat(server.sentIdempotencyKeys().get(1)).isEqualTo(server.sentIdempotencyKeys().get(0));
    }

    @Test
    public void writesAreNotRetriedAfterRetriesAreDisabledAgain() {
        server = FakeServer.replying(closeConnectionWithoutAnswering(), status(200));

        assertThrows(SeatsioException.class, () -> wrapper().retryNonIdempotentRequests(true).retryNonIdempotentRequests(false).stringResponse(request("POST")));

        assertThat(server.requests()).hasSize(1);
    }

    @Test
    public void connectionFailureRetriesStopAfterMaxRetries() {
        server = FakeServer.replying(closeConnectionWithoutAnswering());

        SeatsioException e = assertThrows(SeatsioException.class, () -> wrapper().maxRetries(2).retryNonIdempotentRequests(true).stringResponse(request("POST")));

        assertThat(server.requests()).hasSize(3);
        assertThat(server.sentIdempotencyKeys()).containsOnly(e.idempotencyKey);
    }

    @Test
    public void getsAreRetriedByTheHttpClientOnlyNotOnTopOfIt() {
        server = FakeServer.replying(closeConnectionWithoutAnswering());

        assertThrows(SeatsioException.class, () -> wrapper().retryNonIdempotentRequests(true).stringResponse(request("GET")));

        assertThat(server.requests()).hasSize(APACHE_DEFAULT_ATTEMPTS);
    }

    @Test
    public void connectionRefusedIsRetriedWhenEnabled() {
        server = FakeServer.replying(status(200));
        String url = server.url("/");
        closeServer();

        long start = System.currentTimeMillis();
        assertThrows(SeatsioException.class, () -> wrapper().maxRetries(1).retryNonIdempotentRequests(true).stringResponse(Unirest.post(url).body("{}")));

        assertThat(System.currentTimeMillis() - start).isGreaterThanOrEqualTo(400);
    }

    @Test
    public void requestConstructionErrorsAreNotRetried() {
        long start = System.currentTimeMillis();

        assertThrows(SeatsioException.class, () -> wrapper().retryNonIdempotentRequests(true).stringResponse(Unirest.post("unknownProtocol:///x").body("{}")));

        assertThat(System.currentTimeMillis() - start).isLessThan(400);
    }

    @Test
    public void writesAreRetriedOn429WithTheSameKey() {
        server = FakeServer.replying(error(429, "RATE_LIMIT_EXCEEDED"), status(200));

        wrapper().stringResponse(request("POST"));

        assertThat(server.sentIdempotencyKeys()).hasSize(2);
        assertThat(server.sentIdempotencyKeys().get(1)).isEqualTo(server.sentIdempotencyKeys().get(0));
    }

    @Test
    public void writesAreRetriedOnKeyInUseWithTheSameKey() {
        server = FakeServer.replying(error(409, "IDEMPOTENCY_KEY_IN_USE"), status(200));

        wrapper().stringResponse(request("POST"));

        assertThat(server.sentIdempotencyKeys()).hasSize(2);
        assertThat(server.sentIdempotencyKeys().get(1)).isEqualTo(server.sentIdempotencyKeys().get(0));
    }

    @Test
    public void retriesOn429AndKeyInUseAlsoHappenWhenWriteRetriesAreEnabled() {
        server = FakeServer.replying(error(429, "RATE_LIMIT_EXCEEDED"), error(409, "IDEMPOTENCY_KEY_IN_USE"), status(200));

        wrapper().retryNonIdempotentRequests(true).stringResponse(request("POST"));

        assertThat(server.requests()).hasSize(3);
    }

    @Test
    public void callerKeySurvivesARetry() {
        String key = UUID.randomUUID().toString();
        server = FakeServer.replying(error(409, "IDEMPOTENCY_KEY_IN_USE"), status(200));

        wrapper().withIdempotencyKey(key).stringResponse(request("POST"));

        assertThat(server.sentIdempotencyKeys()).containsExactly(key, key);
    }

    @Test
    public void keyInUseErrorIsThrownWhenRetriesRunOut() {
        server = FakeServer.replying(error(409, "IDEMPOTENCY_KEY_IN_USE"));

        SeatsioException e = assertThrows(SeatsioException.class, () -> wrapper().maxRetries(2).stringResponse(request("POST")));

        assertThat(server.requests()).hasSize(3);
        assertThat(e.errors.get(0).getCode()).isEqualTo("IDEMPOTENCY_KEY_IN_USE");
        assertThat(server.sentIdempotencyKeys()).containsOnly(e.idempotencyKey);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"errors\": [{\"code\": \"SOME_OTHER_CONFLICT\", \"message\": \"conflict\"}]}",
            "{\"errors\": []}",
            "{}",
            "not json",
            ""
    })
    public void otherConflictsAreNotRetried(String body) {
        server = FakeServer.replying(json(409, body), status(200));

        SeatsioException e = assertThrows(SeatsioException.class, () -> wrapper().stringResponse(request("POST")));

        assertThat(server.requests()).hasSize(1);
        assertThat(e.idempotencyKey).isNotNull();
    }

    @Test
    public void doesNotRetryWhenTheThreadIsInterrupted() {
        Thread client = Thread.currentThread();
        server = FakeServer.replying(error(429, "RATE_LIMIT_EXCEEDED").andBeforeReplying(r -> client.interrupt()), status(200));

        SeatsioException e = assertThrows(SeatsioException.class, () -> wrapper().stringResponse(request("POST")));

        assertThat(server.requests()).hasSize(1);
        assertThat(e.idempotencyKey).isNotNull();
        assertThat(Thread.interrupted()).isTrue();
    }

    @Test
    public void doesNotRetryAConnectionFailureWhenTheThreadIsInterrupted() {
        Thread client = Thread.currentThread();
        server = FakeServer.replying(closeConnectionWithoutAnswering().andBeforeReplying(r -> client.interrupt()), status(200));

        assertThrows(SeatsioException.class, () -> wrapper().retryNonIdempotentRequests(true).stringResponse(request("POST")));

        assertThat(server.requests()).hasSize(1);
        assertThat(Thread.interrupted()).isTrue();
    }

    @Test
    public void retrySettingsAreSharedWithCopiesThatHaveAKey() {
        server = FakeServer.replying(closeConnectionWithoutAnswering(), status(200));
        UnirestWrapper wrapper = wrapper();
        UnirestWrapper copy = wrapper.withIdempotencyKey(UUID.randomUUID().toString());

        wrapper.retryNonIdempotentRequests(true);
        copy.stringResponse(request("POST"));

        assertThat(server.requests()).hasSize(2);
    }

    @Test
    public void headersAreSentOncePerAttempt() {
        server = FakeServer.replying(error(429, "RATE_LIMIT_EXCEEDED"), status(200));

        wrapper().stringResponse(request("POST"));

        assertThat(server.requests()).hasSize(2);
        for (FakeServer.Request request : server.requests()) {
            assertThat(request.headers("X-Client-Lib")).hasSize(1);
            assertThat(request.headers("Authorization")).hasSize(1);
            assertThat(request.headers(SeatsioClient.IDEMPOTENCY_KEY_HEADER)).hasSize(1);
        }
    }

    @Test
    public void maxRetriesIsSharedWithClientsThatHaveAKey() {
        server = FakeServer.replying(error(429, "RATE_LIMIT_EXCEEDED"));
        SeatsioClient client = new SeatsioClient("secretKey", null, server.url(""));
        SeatsioClient copy = client.withIdempotencyKey(UUID.randomUUID().toString());

        client.maxRetries(2);
        assertThrows(RateLimitExceededException.class, () -> copy.events.update("anEvent", new seatsio.events.UpdateEventParams().withKey("anotherEvent")));

        assertThat(server.requests()).hasSize(3);
    }

    @Test
    public void outcomeIsUnknownWhenTheConnectionFails() {
        server = FakeServer.replying(closeConnectionWithoutAnswering());

        SeatsioException e = assertThrows(SeatsioException.class, () -> wrapper().stringResponse(request("POST")));

        assertThat(e.outcomeIsUnknown()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(ints = {500, 502, 503})
    public void outcomeIsUnknownAfterA5xx(int status) {
        server = FakeServer.replying(error(status, "SOME_ERROR"));

        SeatsioException e = assertThrows(SeatsioException.class, () -> wrapper().stringResponse(request("POST")));

        assertThat(e.outcomeIsUnknown()).isTrue();
    }

    @Test
    public void outcomeIsUnknownWhenTheKeyIsStillInUseAfterAllRetries() {
        server = FakeServer.replying(error(409, "IDEMPOTENCY_KEY_IN_USE"));

        SeatsioException e = assertThrows(SeatsioException.class, () -> wrapper().maxRetries(0).stringResponse(request("POST")));

        assertThat(e.outcomeIsUnknown()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 404, 409, 422})
    public void outcomeIsKnownAfterA4xx(int status) {
        server = FakeServer.replying(error(status, "SOME_ERROR"));

        SeatsioException e = assertThrows(SeatsioException.class, () -> wrapper().stringResponse(request("POST")));

        assertThat(e.outcomeIsUnknown()).isFalse();
    }

    @Test
    public void theLastScriptedReplyIsRepeatedOnceTheScriptRunsOut() {
        server = FakeServer.replying(error(429, "RATE_LIMIT_EXCEEDED"));

        assertThrows(SeatsioException.class, () -> wrapper().maxRetries(2).stringResponse(request("POST")));

        assertThat(server.requests()).hasSize(3);
    }

    @Test
    public void anOperationWithUnknownOutcomeCanBeRetriedWithTheKeyOfTheException() {
        server = FakeServer.replying(error(500, "SOME_ERROR"), json(200, "{}"));
        SeatsioClient client = new SeatsioClient("secretKey", null, server.url(""));
        String key = assertThrows(SeatsioException.class,
                () -> client.events.update("anEvent", new seatsio.events.UpdateEventParams().withKey("anotherEvent"))).idempotencyKey;

        client.withIdempotencyKey(key).events.update("anEvent", new seatsio.events.UpdateEventParams().withKey("anotherEvent"));

        assertThat(server.sentIdempotencyKeys()).containsExactly(key, key);
    }

    @Test
    public void seatsioClientCanEnableRetriesOfWrites() {
        server = FakeServer.replying(closeConnectionWithoutAnswering(), json(200, "{}"));
        SeatsioClient client = new SeatsioClient("secretKey", null, server.url("")).retryNonIdempotentRequests(true);

        client.withIdempotencyKey(UUID.randomUUID().toString()).events.update("anEvent", new seatsio.events.UpdateEventParams().withKey("anotherEvent"));

        assertThat(server.requests()).hasSize(2);
    }

    private static UnirestWrapper wrapper() {
        return new UnirestWrapper("secretKey", null);
    }

    private HttpRequest<?> request(String method) {
        if (method.equals("GET")) {
            return Unirest.get(server.url("/"));
        }
        return Unirest.request(method, server.url("/")).body("{}");
    }

    private void closeServer() {
        try {
            server.close();
            server = null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
