package seatsio;

import kong.unirest.HttpRequest;
import kong.unirest.Unirest;
import mockwebserver3.Dispatcher;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import mockwebserver3.QueueDispatcher;
import mockwebserver3.RecordedRequest;
import mockwebserver3.SocketEffect;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import seatsio.exceptions.RateLimitExceededException;
import seatsio.util.UnirestWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class IdempotentRetryTest {

    private static final int APACHE_DEFAULT_ATTEMPTS = 1 + 3;

    private final MockWebServer server = new MockWebServer();

    @BeforeEach
    public void startServer() throws IOException {
        ((QueueDispatcher) server.getDispatcher()).setFailFast(true);
        server.start();
    }

    @AfterEach
    public void stopServer() {
        Thread.interrupted();
        server.close();
    }

    @ParameterizedTest
    @ValueSource(strings = {"POST", "PUT", "DELETE", "PATCH"})
    public void writesAreNotRetriedByDefaultWhenTheConnectionFails(String method) {
        replyWith(closeConnectionWithoutAnswering(), status(200));

        SeatsioException e = assertThrows(SeatsioException.class, () -> wrapper().stringResponse(request(method)));

        assertThat(server.getRequestCount()).isEqualTo(1);
        assertThat(e.idempotencyKey).isEqualTo(sentIdempotencyKeys().get(0));
    }

    @ParameterizedTest
    @ValueSource(strings = {"POST", "PUT", "DELETE", "PATCH"})
    public void writesAreRetriedWithTheSameKeyWhenEnabled(String method) {
        replyWith(closeConnectionWithoutAnswering(), status(200));

        wrapper().retryWritesOnNetworkErrors(true).stringResponse(request(method));

        List<String> keys = sentIdempotencyKeys();
        assertThat(keys).hasSize(2).doesNotContainNull();
        assertThat(keys.get(1)).isEqualTo(keys.get(0));
    }

    @Test
    public void writesAreNotRetriedAfterRetriesAreDisabledAgain() {
        replyWith(closeConnectionWithoutAnswering(), status(200));

        assertThrows(SeatsioException.class, () -> wrapper().retryWritesOnNetworkErrors(true).retryWritesOnNetworkErrors(false).stringResponse(request("POST")));

        assertThat(server.getRequestCount()).isEqualTo(1);
    }

    @Test
    public void connectionFailureRetriesStopAfterMaxRetries() {
        replyWith(3, closeConnectionWithoutAnswering());

        SeatsioException e = assertThrows(SeatsioException.class, () -> wrapper().maxRetries(2).retryWritesOnNetworkErrors(true).stringResponse(request("POST")));

        assertThat(server.getRequestCount()).isEqualTo(3);
        assertThat(sentIdempotencyKeys()).containsOnly(e.idempotencyKey);
    }

    @Test
    public void getsAreRetriedByTheHttpClientOnlyNotOnTopOfIt() {
        replyWith(APACHE_DEFAULT_ATTEMPTS, closeConnectionWithoutAnswering());

        assertThrows(SeatsioException.class, () -> wrapper().retryWritesOnNetworkErrors(true).stringResponse(request("GET")));

        assertThat(server.getRequestCount()).isEqualTo(APACHE_DEFAULT_ATTEMPTS);
    }

    @Test
    public void connectionRefusedIsRetriedWhenEnabled() {
        String url = server.url("/").toString();
        server.close();

        long start = System.currentTimeMillis();
        assertThrows(SeatsioException.class, () -> wrapper().maxRetries(1).retryWritesOnNetworkErrors(true).stringResponse(Unirest.post(url).body("{}")));

        assertThat(System.currentTimeMillis() - start).isGreaterThanOrEqualTo(400);
    }

    @Test
    public void requestConstructionErrorsAreNotRetried() {
        long start = System.currentTimeMillis();

        assertThrows(SeatsioException.class, () -> wrapper().retryWritesOnNetworkErrors(true).stringResponse(Unirest.post("unknownProtocol:///x").body("{}")));

        assertThat(System.currentTimeMillis() - start).isLessThan(400);
    }

    @Test
    public void writesAreRetriedOn429WithTheSameKey() {
        replyWith(error(429, "RATE_LIMIT_EXCEEDED"), status(200));

        wrapper().stringResponse(request("POST"));

        List<String> keys = sentIdempotencyKeys();
        assertThat(keys).hasSize(2);
        assertThat(keys.get(1)).isEqualTo(keys.get(0));
    }

    @Test
    public void writesAreRetriedOnKeyInUseWithTheSameKey() {
        replyWith(error(409, "IDEMPOTENCY_KEY_IN_USE"), status(200));

        wrapper().stringResponse(request("POST"));

        List<String> keys = sentIdempotencyKeys();
        assertThat(keys).hasSize(2);
        assertThat(keys.get(1)).isEqualTo(keys.get(0));
    }

    @Test
    public void retriesOn429AndKeyInUseAlsoHappenWhenWriteRetriesAreEnabled() {
        replyWith(error(429, "RATE_LIMIT_EXCEEDED"), error(409, "IDEMPOTENCY_KEY_IN_USE"), status(200));

        wrapper().retryWritesOnNetworkErrors(true).stringResponse(request("POST"));

        assertThat(server.getRequestCount()).isEqualTo(3);
    }

    @Test
    public void callerKeySurvivesARetry() {
        String key = UUID.randomUUID().toString();
        replyWith(error(409, "IDEMPOTENCY_KEY_IN_USE"), status(200));

        wrapper().withIdempotencyKey(key).stringResponse(request("POST"));

        assertThat(sentIdempotencyKeys()).containsExactly(key, key);
    }

    @Test
    public void keyInUseErrorIsThrownWhenRetriesRunOut() {
        replyWith(3, error(409, "IDEMPOTENCY_KEY_IN_USE"));

        SeatsioException e = assertThrows(SeatsioException.class, () -> wrapper().maxRetries(2).stringResponse(request("POST")));

        assertThat(server.getRequestCount()).isEqualTo(3);
        assertThat(e.errors.get(0).getCode()).isEqualTo("IDEMPOTENCY_KEY_IN_USE");
        assertThat(sentIdempotencyKeys()).containsOnly(e.idempotencyKey);
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
        replyWith(json(409, body), status(200));

        SeatsioException e = assertThrows(SeatsioException.class, () -> wrapper().stringResponse(request("POST")));

        assertThat(server.getRequestCount()).isEqualTo(1);
        assertThat(e.idempotencyKey).isNotNull();
    }

    @Test
    public void doesNotRetryWhenTheThreadIsInterrupted() {
        server.setDispatcher(interruptingTheClientAndReplying(Thread.currentThread(), error(429, "RATE_LIMIT_EXCEEDED")));

        SeatsioException e = assertThrows(SeatsioException.class, () -> wrapper().stringResponse(request("POST")));

        assertThat(server.getRequestCount()).isEqualTo(1);
        assertThat(e.idempotencyKey).isNotNull();
        assertThat(Thread.interrupted()).isTrue();
    }

    @Test
    public void doesNotRetryAConnectionFailureWhenTheThreadIsInterrupted() {
        server.setDispatcher(interruptingTheClientAndReplying(Thread.currentThread(), closeConnectionWithoutAnswering()));

        assertThrows(SeatsioException.class, () -> wrapper().retryWritesOnNetworkErrors(true).stringResponse(request("POST")));

        assertThat(server.getRequestCount()).isEqualTo(1);
        assertThat(Thread.interrupted()).isTrue();
    }

    @Test
    public void retrySettingsAreSharedWithCopiesThatHaveAKey() {
        replyWith(closeConnectionWithoutAnswering(), status(200));
        UnirestWrapper wrapper = wrapper();
        UnirestWrapper copy = wrapper.withIdempotencyKey(UUID.randomUUID().toString());

        wrapper.retryWritesOnNetworkErrors(true);
        copy.stringResponse(request("POST"));

        assertThat(server.getRequestCount()).isEqualTo(2);
    }

    @Test
    public void headersAreSentOncePerAttempt() throws InterruptedException {
        replyWith(error(429, "RATE_LIMIT_EXCEEDED"), status(200));

        wrapper().stringResponse(request("POST"));

        assertThat(server.getRequestCount()).isEqualTo(2);
        for (int i = 0; i < 2; i++) {
            RecordedRequest request = server.takeRequest();
            assertThat(request.getHeaders().values("X-Client-Lib")).hasSize(1);
            assertThat(request.getHeaders().values("Authorization")).hasSize(1);
            assertThat(request.getHeaders().values(SeatsioClient.IDEMPOTENCY_KEY_HEADER)).hasSize(1);
        }
    }

    @Test
    public void maxRetriesIsSharedWithClientsThatHaveAKey() {
        replyWith(3, error(429, "RATE_LIMIT_EXCEEDED"));
        SeatsioClient client = new SeatsioClient("secretKey", null, baseUrl());
        SeatsioClient copy = client.withIdempotencyKey(UUID.randomUUID().toString());

        client.maxRetries(2);
        assertThrows(RateLimitExceededException.class, () -> copy.events.update("anEvent", new seatsio.events.UpdateEventParams().withKey("anotherEvent")));

        assertThat(server.getRequestCount()).isEqualTo(3);
    }

    @Test
    public void outcomeIsUnknownWhenTheConnectionFails() {
        replyWith(closeConnectionWithoutAnswering());

        SeatsioException e = assertThrows(SeatsioException.class, () -> wrapper().stringResponse(request("POST")));

        assertThat(e.outcomeIsUnknown()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(ints = {500, 502, 503})
    public void outcomeIsUnknownAfterA5xx(int status) {
        replyWith(error(status, "SOME_ERROR"));

        SeatsioException e = assertThrows(SeatsioException.class, () -> wrapper().stringResponse(request("POST")));

        assertThat(e.outcomeIsUnknown()).isTrue();
    }

    @Test
    public void outcomeIsUnknownWhenTheKeyIsStillInUseAfterAllRetries() {
        replyWith(error(409, "IDEMPOTENCY_KEY_IN_USE"));

        SeatsioException e = assertThrows(SeatsioException.class, () -> wrapper().maxRetries(0).stringResponse(request("POST")));

        assertThat(e.outcomeIsUnknown()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 404, 409, 422})
    public void outcomeIsKnownAfterA4xx(int status) {
        replyWith(error(status, "SOME_ERROR"));

        SeatsioException e = assertThrows(SeatsioException.class, () -> wrapper().stringResponse(request("POST")));

        assertThat(e.outcomeIsUnknown()).isFalse();
    }

    @Test
    public void anOperationWithUnknownOutcomeCanBeRetriedWithTheKeyOfTheException() {
        replyWith(error(500, "SOME_ERROR"), json(200, "{}"));
        SeatsioClient client = new SeatsioClient("secretKey", null, baseUrl());
        String key = assertThrows(SeatsioException.class,
                () -> client.events.update("anEvent", new seatsio.events.UpdateEventParams().withKey("anotherEvent"))).idempotencyKey;

        client.withIdempotencyKey(key).events.update("anEvent", new seatsio.events.UpdateEventParams().withKey("anotherEvent"));

        assertThat(sentIdempotencyKeys()).containsExactly(key, key);
    }

    @Test
    public void seatsioClientCanEnableRetriesOfWrites() {
        replyWith(closeConnectionWithoutAnswering(), json(200, "{}"));
        SeatsioClient client = new SeatsioClient("secretKey", null, baseUrl()).retryWritesOnNetworkErrors(true);

        client.withIdempotencyKey(UUID.randomUUID().toString()).events.update("anEvent", new seatsio.events.UpdateEventParams().withKey("anotherEvent"));

        assertThat(server.getRequestCount()).isEqualTo(2);
    }

    private static UnirestWrapper wrapper() {
        return new UnirestWrapper("secretKey", null);
    }

    private HttpRequest<?> request(String method) {
        String url = server.url("/").toString();
        if (method.equals("GET")) {
            return Unirest.get(url);
        }
        return Unirest.request(method, url).body("{}");
    }

    private String baseUrl() {
        String url = server.url("/").toString();
        return url.substring(0, url.length() - 1);
    }

    private void replyWith(MockResponse... replies) {
        for (MockResponse reply : replies) {
            server.enqueue(reply);
        }
    }

    private void replyWith(int times, MockResponse reply) {
        for (int i = 0; i < times; i++) {
            server.enqueue(reply);
        }
    }

    private List<String> sentIdempotencyKeys() {
        List<String> keys = new ArrayList<>();
        try {
            for (int i = 0; i < server.getRequestCount(); i++) {
                keys.add(server.takeRequest().getHeaders().get(SeatsioClient.IDEMPOTENCY_KEY_HEADER));
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return keys;
    }

    private static Dispatcher interruptingTheClientAndReplying(Thread client, MockResponse reply) {
        return new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                client.interrupt();
                return reply;
            }
        };
    }

    private static MockResponse closeConnectionWithoutAnswering() {
        return new MockResponse.Builder().onResponseStart(new SocketEffect.CloseSocket()).build();
    }

    private static MockResponse status(int code) {
        return new MockResponse.Builder().code(code).build();
    }

    private static MockResponse json(int code, String body) {
        return new MockResponse.Builder().code(code).setHeader("Content-Type", "application/json").body(body).build();
    }

    private static MockResponse error(int code, String errorCode) {
        return json(code, "{\"errors\": [{\"code\": \"" + errorCode + "\", \"message\": \"" + errorCode + "\"}], \"requestId\": \"aRequestId\"}");
    }
}
