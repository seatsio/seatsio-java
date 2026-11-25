package seatsio.events;

import kong.unirest.HttpRequestWithBody;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import mockwebserver3.Dispatcher;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import mockwebserver3.RecordedRequest;
import mockwebserver3.SocketEffect;
import org.junit.jupiter.api.Test;
import seatsio.SeatsioClient;
import seatsio.SeatsioClientTest;
import seatsio.SeatsioException;
import seatsio.util.IdempotencyKeyStatus;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class IdempotencyTest extends SeatsioClientTest {

    @Test
    public void sameKeyReplaysTheResponse() {
        useTestCompanyWithIdempotencyEnabled();
        String chartKey = createTestChart();
        SeatsioClient clientWithKey = client.withIdempotencyKey(UUID.randomUUID());

        Event first = clientWithKey.events.create(chartKey);
        Event second = clientWithKey.events.create(chartKey);

        assertThat(second.id()).isEqualTo(first.id());
        assertThat(second.key()).isEqualTo(first.key());
    }

    @Test
    public void sameKeyWithADifferentRequestIsRejected() {
        useTestCompanyWithIdempotencyEnabled();
        String chartKey = createTestChart();
        SeatsioClient clientWithKey = client.withIdempotencyKey(UUID.randomUUID());
        clientWithKey.events.create(chartKey);

        SeatsioException e = assertThrows(SeatsioException.class, () -> clientWithKey.events.create(chartKey, new CreateEventParams().withKey("anotherEvent")));

        assertThat(e.errors.get(0).getCode()).isEqualTo("IDEMPOTENCY_KEY_REUSED");
    }

    @Test
    public void keyIsIgnoredWhenIdempotencyIsNotEnabledForTheCompany() {
        String chartKey = createTestChart();
        SeatsioClient clientWithKey = client.withIdempotencyKey(UUID.randomUUID());

        Event first = clientWithKey.events.create(chartKey);
        Event second = clientWithKey.events.create(chartKey);

        assertThat(second.id()).isNotEqualTo(first.id());
    }

    @Test
    public void suppliedKeyIsAvailableOnAFailedRequest() {
        useTestCompanyWithIdempotencyEnabled();
        UUID key = UUID.randomUUID();

        SeatsioException e = assertThrows(SeatsioException.class, () -> client.withIdempotencyKey(key).events.create("unknownChart"));

        assertThat(e.idempotencyKey).isEqualTo(key);
    }

    @Test
    public void generatedKeyIsAvailableOnAFailedRequest() {
        useTestCompanyWithIdempotencyEnabled();

        SeatsioException e = assertThrows(SeatsioException.class, () -> client.events.create("unknownChart"));

        assertThat(e.idempotencyKey.version()).isEqualTo(4);
    }

    @Test
    public void retryAfterALostResponseCreatesOneEvent() throws Exception {
        useTestCompanyWithIdempotencyEnabled();
        String chartKey = createTestChart();
        try (MockWebServer proxy = new MockWebServer()) {
            proxy.setDispatcher(forwardingButDroppingTheResponseOfRequest(2));
            proxy.start();
            String proxyUrl = proxy.url("/").toString();
            SeatsioClient clientViaProxy = new SeatsioClient(user.secretKey, null, proxyUrl.substring(0, proxyUrl.length() - 1));

            Event first = clientViaProxy.events.create(chartKey); // the client learns that the route honors idempotency keys
            Event second = clientViaProxy.events.create(chartKey); // response lost, retried with the same key, replayed

            assertThat(proxy.getRequestCount()).isEqualTo(3);
            assertThat(client.events.listAll()).extracting(Event::id).containsExactlyInAnyOrder(first.id(), second.id());
        }
    }

    private static Dispatcher forwardingButDroppingTheResponseOfRequest(int requestToDrop) {
        AtomicInteger requests = new AtomicInteger();
        return new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                HttpResponse<String> response = forward(request);
                if (requests.incrementAndGet() == requestToDrop) {
                    return new MockResponse.Builder().onResponseStart(new SocketEffect.CloseSocket()).build();
                }
                MockResponse.Builder reply = new MockResponse.Builder().code(response.getStatus()).body(response.getBody());
                for (String header : List.of("Content-Type", IdempotencyKeyStatus.HEADER)) {
                    String value = response.getHeaders().getFirst(header);
                    if (!value.isEmpty()) {
                        reply.setHeader(header, value);
                    }
                }
                return reply.build();
            }
        };
    }

    private static HttpResponse<String> forward(RecordedRequest request) {
        HttpRequestWithBody forwarded = Unirest.request(request.getMethod(), apiUrl() + request.getTarget());
        for (String name : request.getHeaders().names()) {
            if (!name.equalsIgnoreCase("host") && !name.equalsIgnoreCase("content-length")) {
                request.getHeaders().values(name).forEach(value -> forwarded.header(name, value));
            }
        }
        return forwarded.body(request.getBody() == null ? "" : request.getBody().utf8()).asString();
    }
}
