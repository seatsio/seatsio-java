package seatsio.util;

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
import seatsio.SeatsioClient;
import seatsio.SeatsioException;
import seatsio.events.UpdateEventParams;
import seatsio.exceptions.RateLimitExceededException;

import java.io.IOException;
import java.net.ServerSocket;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class IdempotentRetryTest {

    private static final int APACHE_DEFAULT_ATTEMPTS = 1 + 3;
    private static final String BOOK = "/events/{key}/actions/book";

    private final MockWebServer server = new MockWebServer();
    private final FakeClock clock = new FakeClock();

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

    // guide test 1
    @ParameterizedTest
    @ValueSource(strings = {"POST", "DELETE"})
    public void aLostResponseIsRetriedWithTheSameKeyOnARouteThatHonorsKeys(String method) {
        UnirestWrapper wrapper = wrapper();
        replyWith(keyStatus(200, "executed"), lostResponse(), keyStatus(json(200, "{\"replayed\": true}"), "replayed"));

        wrapper.stringResponse(request(method));
        String response = wrapper.stringResponse(request(method));

        assertThat(response).isEqualTo("{\"replayed\": true}");
        List<String> keys = sentIdempotencyKeys();
        assertThat(keys).hasSize(3).doesNotContainNull();
        assertThat(keys.get(2)).isEqualTo(keys.get(1)).isNotEqualTo(keys.get(0));
    }

    // guide test 2
    @Test
    public void aLostResponseIsNotRetriedOnARouteThatIgnoresKeys() {
        UnirestWrapper wrapper = wrapper();
        replyWith(keyStatus(200, "ignored"), lostResponse());

        wrapper.stringResponse(request("POST"));
        SeatsioException e = assertThrows(SeatsioException.class, () -> wrapper.stringResponse(request("POST")));

        assertThat(server.getRequestCount()).isEqualTo(2);
        assertThat(e.outcomeIsUnknown()).isTrue();
        assertThat(e.isSafeToRetryWithSameKey()).isFalse();
        assertThat(e.idempotencyKey).isEqualTo(sentIdempotencyKeys().get(1));
    }

    // guide test 3
    @Test
    public void aLostResponseIsNotRetriedOnARouteThatNeverSentAStatus() {
        UnirestWrapper wrapper = wrapper();
        replyWith(status(200), lostResponse());

        wrapper.stringResponse(request("POST"));
        assertThrows(SeatsioException.class, () -> wrapper.stringResponse(request("POST")));

        assertThat(server.getRequestCount()).isEqualTo(2);
    }

    // guide test 4
    @ParameterizedTest
    @ValueSource(strings = {"POST", "PUT", "DELETE", "PATCH"})
    public void theFirstRequestToARouteIsNotRetriedAfterALostResponse(String method) {
        replyWith(lostResponse(), status(200));

        SeatsioException e = assertThrows(SeatsioException.class, () -> wrapper().stringResponse(request(method)));

        assertThat(server.getRequestCount()).isEqualTo(1);
        assertThat(e.outcomeIsUnknown()).isTrue();
        assertThat(e.isSafeToRetryWithSameKey()).isFalse();
        assertThat(e.idempotencyKey).isEqualTo(sentIdempotencyKeys().get(0));
    }

    // guide test 5
    @Test
    public void aRequestThatWasNeverSentIsRetriedWithTheSameKey() throws Exception {
        int port = freePort();
        try (MockWebServer late = new MockWebServer()) {
            late.enqueue(status(200));
            clock.onSleep = () -> start(late, port);
            HttpRequest<?> request = Unirest.post("http://localhost:" + port + "/").body("{}");

            wrapper().stringResponse(request);

            assertThat(clock.sleeps).isEqualTo(1);
            assertThat(late.getRequestCount()).isEqualTo(1);
            assertThat(late.takeRequest().getHeaders().get(SeatsioClient.IDEMPOTENCY_KEY_HEADER))
                    .isEqualTo(request.getHeaders().getFirst(SeatsioClient.IDEMPOTENCY_KEY_HEADER))
                    .isNotEmpty();
        }
    }

    // guide test 6
    @Test
    public void routesAreRememberedSeparately() {
        UnirestWrapper wrapper = wrapper();
        replyWith(keyStatus(200, "executed"), lostResponse());

        wrapper.stringResponse(Unirest.post(baseUrl() + "/a").body("{}"));
        assertThrows(SeatsioException.class, () -> wrapper.stringResponse(Unirest.post(baseUrl() + "/b").body("{}")));

        assertThat(server.getRequestCount()).isEqualTo(2);
    }

    @Test
    public void methodsAreRememberedSeparately() {
        UnirestWrapper wrapper = wrapper();
        replyWith(keyStatus(200, "executed"), lostResponse());

        wrapper.stringResponse(request("POST"));
        assertThrows(SeatsioException.class, () -> wrapper.stringResponse(request("DELETE")));

        assertThat(server.getRequestCount()).isEqualTo(2);
    }

    // guide test 7
    @ParameterizedTest
    @ValueSource(ints = {200, 400, 500})
    public void theLatestKnownStatusOfARouteWins(int ignoredStatus) {
        UnirestWrapper wrapper = wrapper();
        replyWith(keyStatus(200, "executed"), keyStatus(ignoredStatus, "ignored"), lostResponse());

        wrapper.stringResponse(request("POST"));
        ignoreFailure(() -> wrapper.stringResponse(request("POST")));
        assertThrows(SeatsioException.class, () -> wrapper.stringResponse(request("POST")));

        assertThat(server.getRequestCount()).isEqualTo(3);
    }

    // guide test 8
    @ParameterizedTest
    @ValueSource(ints = {401, 429})
    public void aResponseWithoutAStatusLeavesTheMemoryAlone(int status) {
        UnirestWrapper wrapper = wrapper();
        replyWith(keyStatus(200, "executed"), error(status, "SOME_ERROR"), lostResponse(), keyStatus(200, "replayed"));

        wrapper.stringResponse(request("POST"));
        wrapper.maxRetries(0);
        assertThrows(SeatsioException.class, () -> wrapper.stringResponse(request("POST")));
        wrapper.maxRetries(1);
        wrapper.stringResponse(request("POST"));

        assertThat(server.getRequestCount()).isEqualTo(4);
    }

    // guide test 9
    @Test
    public void concretePathsOfTheSameTemplateShareTheMemory() {
        UnirestWrapper wrapper = wrapper();
        replyWith(keyStatus(200, "executed"), lostResponse(), keyStatus(200, "replayed"));

        wrapper.stringResponse(book("event1"));
        wrapper.stringResponse(book("event2"));

        assertThat(server.getRequestCount()).isEqualTo(3);
    }

    // guide test 10
    @Test
    public void copiesWithAKeyShareTheMemory() {
        UnirestWrapper wrapper = wrapper();
        replyWith(keyStatus(200, "executed"), lostResponse(), keyStatus(200, "replayed"));

        wrapper.stringResponse(request("POST"));
        wrapper.withIdempotencyKey(UUID.randomUUID().toString()).stringResponse(request("POST"));

        assertThat(server.getRequestCount()).isEqualTo(3);
    }

    @Test
    public void otherWrappersDontShareTheMemory() {
        replyWith(keyStatus(200, "executed"), lostResponse());

        new UnirestWrapper("aSecretKey", null, clock).stringResponse(request("POST"));
        assertThrows(SeatsioException.class, () -> new UnirestWrapper("anotherSecretKey", null, clock).stringResponse(request("POST")));

        assertThat(server.getRequestCount()).isEqualTo(2);
    }

    // guide test 11
    @Test
    public void aRetrySendsTheSameRequest() throws InterruptedException {
        UnirestWrapper wrapper = new UnirestWrapper("secretKey", "aWorkspaceKey", clock);
        replyWith(keyStatus(200, "executed"), lostResponse(), keyStatus(200, "replayed"));

        wrapper.stringResponse(book("event1"));
        wrapper.stringResponse(Unirest.post(baseUrl() + BOOK).routeParam("key", "event2").queryString("expand", "x").body("{\"objects\": [\"A-1\"]}"));

        server.takeRequest();
        RecordedRequest lost = server.takeRequest();
        RecordedRequest retry = server.takeRequest();
        assertThat(retry.getMethod()).isEqualTo(lost.getMethod()).isEqualTo("POST");
        assertThat(retry.getTarget()).isEqualTo(lost.getTarget()).isEqualTo("/events/event2/actions/book?expand=x");
        assertThat(retry.getBody()).isEqualTo(lost.getBody());
        assertThat(retry.getBody().utf8()).isEqualTo("{\"objects\": [\"A-1\"]}");
        for (String header : List.of(SeatsioClient.IDEMPOTENCY_KEY_HEADER, "X-Workspace-Key", "Authorization")) {
            assertThat(retry.getHeaders().values(header)).hasSize(1).isEqualTo(lost.getHeaders().values(header));
        }
    }

    // guide test 12
    @Test
    public void anUnknownStatusLeavesTheMemoryAlone() {
        UnirestWrapper wrapper = wrapper();
        replyWith(keyStatus(200, "executed"), keyStatus(200, "maybe"), lostResponse(), keyStatus(200, "replayed"));

        wrapper.stringResponse(request("POST"));
        wrapper.stringResponse(request("POST"));
        wrapper.stringResponse(request("POST"));

        assertThat(server.getRequestCount()).isEqualTo(4);
    }

    @Test
    public void anUnknownStatusDoesNotMarkARouteAsHonoringKeys() {
        UnirestWrapper wrapper = wrapper();
        replyWith(keyStatus(200, "maybe"), lostResponse());

        wrapper.stringResponse(request("POST"));
        assertThrows(SeatsioException.class, () -> wrapper.stringResponse(request("POST")));

        assertThat(server.getRequestCount()).isEqualTo(2);
    }

    @Test
    public void theStatusIsCaseInsensitive() {
        UnirestWrapper wrapper = wrapper();
        replyWith(keyStatus(200, "Executed"), lostResponse(), keyStatus(200, "replayed"));

        wrapper.stringResponse(request("POST"));
        wrapper.stringResponse(request("POST"));

        assertThat(server.getRequestCount()).isEqualTo(3);
    }

    // guide test 13
    @Test
    public void inUseMarksARouteAsHonoringKeys() {
        UnirestWrapper wrapper = wrapper();
        replyWith(keyStatus(error(409, "IDEMPOTENCY_KEY_IN_USE"), "in-use"), lostResponse(), keyStatus(200, "replayed"));

        wrapper.maxRetries(0);
        SeatsioException e = assertThrows(SeatsioException.class, () -> wrapper.stringResponse(request("POST")));
        wrapper.maxRetries(1);
        wrapper.stringResponse(request("POST"));

        assertThat(server.getRequestCount()).isEqualTo(3);
        assertThat(e.idempotencyKeyHonored).isTrue();
        assertThat(e.replayed).isFalse();
    }

    @Test
    public void reusedMarksARouteAsHonoringKeys() {
        UnirestWrapper wrapper = wrapper();
        replyWith(keyStatus(error(422, "IDEMPOTENCY_KEY_REUSED"), "reused"), lostResponse(), keyStatus(200, "replayed"));

        SeatsioException e = assertThrows(SeatsioException.class, () -> wrapper.stringResponse(request("POST")));
        wrapper.stringResponse(request("POST"));

        assertThat(server.getRequestCount()).isEqualTo(3);
        assertThat(e.idempotencyKeyHonored).isTrue();
        assertThat(e.replayed).isFalse();
        assertThat(e.outcomeIsUnknown()).isFalse();
        assertThat(e.isSafeToRetryWithSameKey()).isFalse();
    }

    @Test
    public void replayedMarksARouteAsHonoringKeys() {
        UnirestWrapper wrapper = wrapper();
        replyWith(keyStatus(200, "replayed"), lostResponse(), keyStatus(200, "replayed"));

        wrapper.stringResponse(request("POST"));
        wrapper.stringResponse(request("POST"));

        assertThat(server.getRequestCount()).isEqualTo(3);
    }

    @Test
    public void aReplayedErrorIsReportedAsReplayed() {
        replyWith(keyStatus(error(400, "SOME_ERROR"), "replayed"));

        SeatsioException e = assertThrows(SeatsioException.class, () -> wrapper().stringResponse(request("POST")));

        assertThat(e.replayed).isTrue();
        assertThat(e.idempotencyKeyHonored).isTrue();
        assertThat(e.outcomeIsUnknown()).isFalse();
        assertThat(e.isSafeToRetryWithSameKey()).isFalse();
    }

    @Test
    public void anErrorWithoutAStatusSaysNothingAboutTheKey() {
        replyWith(error(400, "SOME_ERROR"));

        SeatsioException e = assertThrows(SeatsioException.class, () -> wrapper().stringResponse(request("POST")));

        assertThat(e.idempotencyKeyHonored).isNull();
        assertThat(e.replayed).isFalse();
    }

    @Test
    public void reusedIsNotRetried() {
        replyWith(keyStatus(error(422, "IDEMPOTENCY_KEY_REUSED"), "reused"), status(200));

        assertThrows(SeatsioException.class, () -> wrapper().stringResponse(request("POST")));

        assertThat(server.getRequestCount()).isEqualTo(1);
    }

    // guide test 14
    @Test
    public void lostResponsesCountAgainstMaxRetries() {
        UnirestWrapper wrapper = wrapper();
        replyWith(keyStatus(200, "executed"));
        replyWith(3, lostResponse());

        wrapper.stringResponse(request("POST"));
        SeatsioException e = assertThrows(SeatsioException.class, () -> wrapper.maxRetries(2).stringResponse(request("POST")));

        assertThat(server.getRequestCount()).isEqualTo(1 + 3);
        assertThat(e.outcomeIsUnknown()).isTrue();
        assertThat(e.isSafeToRetryWithSameKey()).isTrue();
        assertThat(sentIdempotencyKeys().subList(1, 4)).containsOnly(e.idempotencyKey);
    }

    @Test
    public void allKindsOfFailureShareOneRetryCount() throws Exception {
        int port = freePort();
        try (MockWebServer late = new MockWebServer()) {
            late.enqueue(error(429, "RATE_LIMIT_EXCEEDED"));
            late.enqueue(keyStatus(error(409, "IDEMPOTENCY_KEY_IN_USE"), "in-use"));
            late.enqueue(lostResponse());
            late.enqueue(status(200));
            clock.onSleep = () -> {
                if (clock.sleeps == 1) {
                    start(late, port);
                }
            };
            HttpRequest<?> request = Unirest.post("http://localhost:" + port + BOOK).routeParam("key", "event1").body("{}");

            SeatsioException e = assertThrows(SeatsioException.class, () -> wrapper().maxRetries(3).stringResponse(request));

            assertThat(clock.sleeps).isEqualTo(3);
            assertThat(late.getRequestCount()).isEqualTo(3);
            assertThat(e.getCause()).isNotNull();
            assertThat(e.outcomeIsUnknown()).isTrue();
            assertThat(e.isSafeToRetryWithSameKey()).isTrue();
        }
    }

    // guide test 15
    @Test
    public void setDefaultWorkspaceIsRetriedAfterALostResponse() {
        SeatsioClient client = new SeatsioClient("secretKey", null, baseUrl());
        replyWith(keyStatus(204, "executed"), lostResponse(), keyStatus(204, "replayed"));

        client.workspaces.setDefault("workspace1");
        client.workspaces.setDefault("workspace2");

        List<String> keys = sentIdempotencyKeys();
        assertThat(keys).hasSize(3);
        assertThat(keys.get(2)).isEqualTo(keys.get(1));
    }

    // guide test 16
    @Test
    public void aRequestThatWasNeverSentIsSafeToRetryWithTheSameKey() {
        String url = server.url("/").toString();
        server.close();

        SeatsioException e = assertThrows(SeatsioException.class, () -> wrapper().maxRetries(0).stringResponse(Unirest.post(url).body("{}")));

        assertThat(e.outcomeIsUnknown()).isFalse();
        assertThat(e.isSafeToRetryWithSameKey()).isTrue();
        assertThat(e.idempotencyKey).isNotNull();
    }

    @Test
    public void aLostResponseIsSafeToRetryWithTheSameKeyOnARouteThatHonorsKeys() {
        UnirestWrapper wrapper = wrapper();
        replyWith(keyStatus(200, "executed"), lostResponse());

        wrapper.stringResponse(request("POST"));
        SeatsioException e = assertThrows(SeatsioException.class, () -> wrapper.maxRetries(0).stringResponse(request("POST")));

        assertThat(e.outcomeIsUnknown()).isTrue();
        assertThat(e.isSafeToRetryWithSameKey()).isTrue();
    }

    // guide test 17
    @Test
    public void aRequestThatWasNeverSentIsNotRetriedOnceTheKeyMayHaveExpired() {
        replyWith(error(429, "RATE_LIMIT_EXCEEDED").newBuilder().setHeader("Connection", "close").build());
        clock.onSleep = () -> {
            server.close();
            clock.now += Duration.ofMinutes(6).toMillis();
        };

        SeatsioException e = assertThrows(SeatsioException.class, () -> wrapper().stringResponse(request("POST")));

        assertThat(server.getRequestCount()).isEqualTo(1);
        assertThat(clock.sleeps).isEqualTo(1);
        assertThat(e.getCause()).isNotNull();
        assertThat(e.outcomeIsUnknown()).isFalse();
    }

    @Test
    public void aRequestThatWasNeverSentIsRetriedWhateverTheTimeIfNoAttemptWasSent() {
        String url = server.url("/").toString();
        server.close();
        clock.onSleep = () -> clock.now += Duration.ofMinutes(10).toMillis();

        assertThrows(SeatsioException.class, () -> wrapper().maxRetries(3).stringResponse(Unirest.post(url).body("{}")));

        assertThat(clock.sleeps).isEqualTo(3);
    }

    @Test
    public void lostResponsesAreNotRetriedOnceTheKeyMayHaveExpired() {
        UnirestWrapper wrapper = wrapper();
        replyWith(keyStatus(200, "executed"));
        replyWith(4, lostResponse());
        clock.onSleep = () -> clock.now += Duration.ofMinutes(2).toMillis();

        wrapper.stringResponse(request("POST"));
        SeatsioException e = assertThrows(SeatsioException.class, () -> wrapper.stringResponse(request("POST")));

        assertThat(server.getRequestCount()).isEqualTo(1 + 4);
        assertThat(e.outcomeIsUnknown()).isTrue();
        assertThat(e.isSafeToRetryWithSameKey()).isFalse();
    }

    @Test
    public void retriesOn429StopOnceTheKeyMayHaveExpired() {
        replyWith(2, error(429, "RATE_LIMIT_EXCEEDED"));
        clock.onSleep = () -> clock.now += Duration.ofMinutes(6).toMillis();

        assertThrows(RateLimitExceededException.class, () -> wrapper().stringResponse(request("POST")));

        assertThat(server.getRequestCount()).isEqualTo(2);
    }

    @Test
    public void getsAreRetriedByTheHttpClientOnlyNotOnTopOfIt() {
        replyWith(APACHE_DEFAULT_ATTEMPTS, lostResponse());

        SeatsioException e = assertThrows(SeatsioException.class, () -> wrapper().stringResponse(request("GET")));

        assertThat(server.getRequestCount()).isEqualTo(APACHE_DEFAULT_ATTEMPTS);
        assertThat(e.idempotencyKey).isNull();
        assertThat(e.isSafeToRetryWithSameKey()).isFalse();
    }

    @Test
    public void requestConstructionErrorsAreNotRetried() {
        assertThrows(SeatsioException.class, () -> wrapper().stringResponse(Unirest.post("unknownProtocol:///x").body("{}")));

        assertThat(clock.sleeps).isZero();
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
    public void doesNotRetryALostResponseWhenTheThreadIsInterrupted() {
        UnirestWrapper wrapper = wrapper();
        replyWith(keyStatus(200, "executed"));
        wrapper.stringResponse(request("POST"));
        server.setDispatcher(interruptingTheClientAndReplying(Thread.currentThread(), lostResponse()));

        assertThrows(SeatsioException.class, () -> wrapper.stringResponse(request("POST")));

        assertThat(server.getRequestCount()).isEqualTo(2);
        assertThat(Thread.interrupted()).isTrue();
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
        assertThrows(RateLimitExceededException.class, () -> copy.events.update("anEvent", new UpdateEventParams().withKey("anotherEvent")));

        assertThat(server.getRequestCount()).isEqualTo(3);
    }

    @ParameterizedTest
    @ValueSource(ints = {500, 502, 503})
    public void outcomeIsUnknownAfterA5xx(int status) {
        replyWith(error(status, "SOME_ERROR"));

        SeatsioException e = assertThrows(SeatsioException.class, () -> wrapper().stringResponse(request("POST")));

        assertThat(e.outcomeIsUnknown()).isTrue();
        assertThat(e.isSafeToRetryWithSameKey()).isFalse();
    }

    @Test
    public void a5xxIsSafeToRetryWithTheSameKeyWhenTheKeyWasHonored() {
        replyWith(keyStatus(error(500, "SOME_ERROR"), "executed"));

        SeatsioException e = assertThrows(SeatsioException.class, () -> wrapper().stringResponse(request("POST")));

        assertThat(e.idempotencyKeyHonored).isTrue();
        assertThat(e.isSafeToRetryWithSameKey()).isTrue();
    }

    @Test
    public void a5xxWithoutAStatusIsSafeToRetryWithTheSameKeyOnARouteThatHonorsKeys() {
        UnirestWrapper wrapper = wrapper();
        replyWith(keyStatus(200, "executed"), error(502, "BAD_GATEWAY"));

        wrapper.stringResponse(request("POST"));
        SeatsioException e = assertThrows(SeatsioException.class, () -> wrapper.stringResponse(request("POST")));

        assertThat(e.idempotencyKeyHonored).isNull();
        assertThat(e.isSafeToRetryWithSameKey()).isTrue();
    }

    @Test
    public void a5xxIsNotSafeToRetryWithTheSameKeyWhenTheKeyWasIgnored() {
        replyWith(keyStatus(error(500, "SOME_ERROR"), "ignored"));

        SeatsioException e = assertThrows(SeatsioException.class, () -> wrapper().stringResponse(request("POST")));

        assertThat(e.idempotencyKeyHonored).isFalse();
        assertThat(e.isSafeToRetryWithSameKey()).isFalse();
    }

    @Test
    public void outcomeIsUnknownWhenTheKeyIsStillInUseAfterAllRetries() {
        replyWith(keyStatus(error(409, "IDEMPOTENCY_KEY_IN_USE"), "in-use"));

        SeatsioException e = assertThrows(SeatsioException.class, () -> wrapper().maxRetries(0).stringResponse(request("POST")));

        assertThat(e.outcomeIsUnknown()).isTrue();
        assertThat(e.isSafeToRetryWithSameKey()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 404, 409, 422})
    public void outcomeIsKnownAfterA4xx(int status) {
        replyWith(keyStatus(error(status, "SOME_ERROR"), "executed"));

        SeatsioException e = assertThrows(SeatsioException.class, () -> wrapper().stringResponse(request("POST")));

        assertThat(e.outcomeIsUnknown()).isFalse();
        assertThat(e.isSafeToRetryWithSameKey()).isFalse();
    }

    @Test
    public void anOperationWithUnknownOutcomeCanBeRetriedWithTheKeyOfTheException() {
        replyWith(keyStatus(error(500, "SOME_ERROR"), "executed"), json(200, "{}"));
        SeatsioClient client = new SeatsioClient("secretKey", null, baseUrl());
        SeatsioException e = assertThrows(SeatsioException.class,
                () -> client.events.update("anEvent", new UpdateEventParams().withKey("anotherEvent")));

        assertThat(e.isSafeToRetryWithSameKey()).isTrue();
        client.withIdempotencyKey(e.idempotencyKey).events.update("anEvent", new UpdateEventParams().withKey("anotherEvent"));

        assertThat(sentIdempotencyKeys()).containsExactly(e.idempotencyKey, e.idempotencyKey);
    }

    private UnirestWrapper wrapper() {
        return new UnirestWrapper("secretKey", null, clock);
    }

    private HttpRequest<?> request(String method) {
        String url = server.url("/").toString();
        if (method.equals("GET")) {
            return Unirest.get(url);
        }
        return Unirest.request(method, url).body("{}");
    }

    private HttpRequest<?> book(String eventKey) {
        return Unirest.post(baseUrl() + BOOK).routeParam("key", eventKey).body("{}");
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

    private static void ignoreFailure(Runnable call) {
        try {
            call.run();
        } catch (SeatsioException e) {
            // expected for some statuses
        }
    }

    private static int freePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    private static void start(MockWebServer server, int port) {
        try {
            server.start(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    /**
     * The client sends the request, then the connection closes before the response starts.
     */
    private static MockResponse lostResponse() {
        return new MockResponse.Builder().onResponseStart(new SocketEffect.CloseSocket()).build();
    }

    private static MockResponse status(int code) {
        return new MockResponse.Builder().code(code).build();
    }

    private static MockResponse keyStatus(int code, String keyStatus) {
        return keyStatus(status(code), keyStatus);
    }

    private static MockResponse keyStatus(MockResponse response, String keyStatus) {
        return response.newBuilder().setHeader(IdempotencyKeyStatus.HEADER, keyStatus).build();
    }

    private static MockResponse json(int code, String body) {
        return new MockResponse.Builder().code(code).setHeader("Content-Type", "application/json").body(body).build();
    }

    private static MockResponse error(int code, String errorCode) {
        return json(code, "{\"errors\": [{\"code\": \"" + errorCode + "\", \"message\": \"" + errorCode + "\"}], \"requestId\": \"aRequestId\"}");
    }

    private static class FakeClock implements RetryClock {
        long now;
        int sleeps;
        Runnable onSleep = () -> {
        };

        @Override
        public long millis() {
            return now;
        }

        @Override
        public void sleep(long millis) throws InterruptedException {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            sleeps++;
            now += millis;
            onSleep.run();
        }
    }
}
