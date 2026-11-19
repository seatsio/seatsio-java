package seatsio.events;

import kong.unirest.HttpRequestWithBody;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.Test;
import seatsio.FakeServer;
import seatsio.FakeServer.Reply;
import seatsio.SeatsioClient;
import seatsio.SeatsioClientTest;
import seatsio.SeatsioException;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class IdempotencyTest extends SeatsioClientTest {

    @Test
    public void sameKeyReplaysTheResponse() {
        useTestCompanyWithIdempotencyEnabled();
        String chartKey = createTestChart();
        SeatsioClient clientWithKey = client.withIdempotencyKey(UUID.randomUUID().toString());

        Event first = clientWithKey.events.create(chartKey);
        Event second = clientWithKey.events.create(chartKey);

        assertThat(second.id()).isEqualTo(first.id());
        assertThat(second.key()).isEqualTo(first.key());
    }

    @Test
    public void sameKeyWithADifferentRequestIsRejected() {
        useTestCompanyWithIdempotencyEnabled();
        String chartKey = createTestChart();
        SeatsioClient clientWithKey = client.withIdempotencyKey(UUID.randomUUID().toString());
        clientWithKey.events.create(chartKey);

        SeatsioException e = assertThrows(SeatsioException.class, () -> clientWithKey.events.create(chartKey, new CreateEventParams().withKey("anotherEvent")));

        assertThat(e.errors.get(0).getCode()).isEqualTo("IDEMPOTENCY_KEY_REUSED");
    }

    @Test
    public void keyIsIgnoredWhenIdempotencyIsNotEnabledForTheCompany() {
        String chartKey = createTestChart();
        SeatsioClient clientWithKey = client.withIdempotencyKey(UUID.randomUUID().toString());

        Event first = clientWithKey.events.create(chartKey);
        Event second = clientWithKey.events.create(chartKey);

        assertThat(second.id()).isNotEqualTo(first.id());
    }

    @Test
    public void suppliedKeyIsAvailableOnAFailedRequest() {
        useTestCompanyWithIdempotencyEnabled();
        String key = UUID.randomUUID().toString();

        SeatsioException e = assertThrows(SeatsioException.class, () -> client.withIdempotencyKey(key).events.create("unknownChart"));

        assertThat(e.idempotencyKey).isEqualTo(key);
    }

    @Test
    public void generatedKeyIsAvailableOnAFailedRequest() {
        useTestCompanyWithIdempotencyEnabled();

        SeatsioException e = assertThrows(SeatsioException.class, () -> client.events.create("unknownChart"));

        assertThat(UUID.fromString(e.idempotencyKey).version()).isEqualTo(4);
    }

    @Test
    public void retryAfterALostResponseCreatesOneEvent() throws Exception {
        useTestCompanyWithIdempotencyEnabled();
        String chartKey = createTestChart();
        try (FakeServer proxy = proxyThatForwardsButDropsTheFirstResponse()) {
            SeatsioClient clientViaProxy = new SeatsioClient(user.secretKey, null, proxy.url("")).retryNonIdempotentRequests(true);

            Event event = clientViaProxy.events.create(chartKey);

            assertThat(client.events.listAll()).extracting(Event::id).containsExactly(event.id());
        }
    }

    private static FakeServer proxyThatForwardsButDropsTheFirstResponse() {
        AtomicInteger requests = new AtomicInteger();
        return FakeServer.replying(Reply.computedBy(request -> {
            HttpResponse<String> response = forward(request);
            if (requests.incrementAndGet() == 1) {
                return Reply.closeConnectionWithoutAnswering();
            }
            return Reply.of(response.getStatus(), response.getHeaders().getFirst("Content-Type"), response.getBody());
        }));
    }

    private static HttpResponse<String> forward(FakeServer.Request request) {
        HttpRequestWithBody forwarded = Unirest.request(request.method(), apiUrl() + request.path());
        request.headers().forEach((name, values) -> {
            if (!name.equals("host") && !name.equals("content-length")) {
                values.forEach(value -> forwarded.header(name, value));
            }
        });
        return forwarded.body(request.body()).asString();
    }
}
