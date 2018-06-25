package seatsio.events;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import seatsio.SeatsioClientTest;
import seatsio.holdTokens.HoldToken;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static seatsio.events.ObjectStatus.BOOKED;
import static seatsio.events.ObjectStatus.FREE;

public class BookObjectsTest extends SeatsioClientTest {

    @Test
    public void test() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        ChangeObjectStatusResult result = client.events.book(event.key, newArrayList("A-1", "A-2"));

        assertThat(client.events.retrieveObjectStatus(event.key, "A-1").status).isEqualTo(BOOKED);
        assertThat(client.events.retrieveObjectStatus(event.key, "A-2").status).isEqualTo(BOOKED);
        assertThat(client.events.retrieveObjectStatus(event.key, "A-3").status).isEqualTo(FREE);
        assertThat(result.labels).isEqualTo(ImmutableMap.of(
                "A-1", new Labels("1", "seat", "A", "row"),
                "A-2", new Labels("2", "seat", "A", "row")
        ));
    }

    @Test
    public void sections() {
        String chartKey = createTestChartWithSections();
        Event event = client.events.create(chartKey);

        ChangeObjectStatusResult result = client.events.book(event.key, newArrayList("Section A-A-1", "Section A-A-2"));

        assertThat(client.events.retrieveObjectStatus(event.key, "Section A-A-1").status).isEqualTo(BOOKED);
        assertThat(client.events.retrieveObjectStatus(event.key, "Section A-A-2").status).isEqualTo(BOOKED);
        assertThat(client.events.retrieveObjectStatus(event.key, "Section A-A-3").status).isEqualTo(FREE);
        assertThat(result.labels).isEqualTo(ImmutableMap.of(
                "Section A-A-1", new Labels("1", "seat", "A", "row", "Section A", "Entrance 1"),
                "Section A-A-2", new Labels("2", "seat", "A", "row", "Section A", "Entrance 1")
        ));
    }

    @Test
    public void holdToken() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        HoldToken holdToken = client.holdTokens.create();
        client.events.hold(event.key, newArrayList("A-1", "A-2"), holdToken.holdToken);

        client.events.book(event.key, newArrayList("A-1", "A-2"), holdToken.holdToken);

        ObjectStatus status1 = client.events.retrieveObjectStatus(event.key, "A-1");
        assertThat(status1.status).isEqualTo(BOOKED);
        assertThat(status1.holdToken).isNull();

        ObjectStatus status2 = client.events.retrieveObjectStatus(event.key, "A-2");
        assertThat(status2.status).isEqualTo(BOOKED);
        assertThat(status2.holdToken).isNull();
    }

    @Test
    public void orderId() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        client.events.book(event.key, newArrayList("A-1", "A-2"), null, "order1");

        assertThat(client.events.retrieveObjectStatus(event.key, "A-1").orderId).isEqualTo("order1");
        assertThat(client.events.retrieveObjectStatus(event.key, "A-2").orderId).isEqualTo("order1");
    }
}
