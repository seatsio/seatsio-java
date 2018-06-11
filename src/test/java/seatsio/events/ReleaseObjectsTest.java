package seatsio.events;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import seatsio.SeatsioClientTest;
import seatsio.holdTokens.HoldToken;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static seatsio.events.ObjectStatus.FREE;

public class ReleaseObjectsTest extends SeatsioClientTest {

    @Test
    public void test() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.book(event.key, newArrayList("A-1", "A-2"));

        ChangeObjectStatusResult result = client.events.release(event.key, newArrayList("A-1", "A-2"));

        assertThat(client.events.retrieveObjectStatus(event.key, "A-1").status).isEqualTo(FREE);
        assertThat(client.events.retrieveObjectStatus(event.key, "A-2").status).isEqualTo(FREE);
        assertThat(result.labels).isEqualTo(ImmutableMap.of(
                "A-1", new Labels("1", "seat", "A", "row", null),
                "A-2", new Labels("2", "seat", "A", "row", null)
        ));
    }

    @Test
    public void holdToken() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        HoldToken holdToken = client.holdTokens.create();
        client.events.hold(event.key, newArrayList("A-1", "A-2"), holdToken.holdToken, null);

        client.events.release(event.key, newArrayList("A-1", "A-2"), holdToken.holdToken);

        ObjectStatus status1 = client.events.retrieveObjectStatus(event.key, "A-1");
        assertThat(status1.status).isEqualTo(FREE);
        assertThat(status1.holdToken).isNull();

        ObjectStatus status2 = client.events.retrieveObjectStatus(event.key, "A-2");
        assertThat(status2.status).isEqualTo(FREE);
        assertThat(status2.holdToken).isNull();
    }

    @Test
    public void orderId() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        client.events.release(event.key, newArrayList("A-1", "A-2"), null, "order1");

        assertThat(client.events.retrieveObjectStatus(event.key, "A-1").orderId).isEqualTo("order1");
        assertThat(client.events.retrieveObjectStatus(event.key, "A-2").orderId).isEqualTo("order1");
    }
}
