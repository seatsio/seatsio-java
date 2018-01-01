package seatsio.events;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import seatsio.SeatsioClientTest;
import seatsio.holdTokens.HoldToken;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static seatsio.events.ObjectStatus.BOOKED;
import static seatsio.events.ObjectStatus.FREE;

public class ChangeObjectStatusTest extends SeatsioClientTest {

    @Test
    public void test() {
        String chartKey = createTestChart();
        Event event = client.events().create(chartKey);

        client.events().changeObjectStatus(event.key, newArrayList("A-1", "A-2"), "foo");

        assertThat(client.events().getObjectStatus(event.key, "A-1").status).isEqualTo("foo");
        assertThat(client.events().getObjectStatus(event.key, "A-2").status).isEqualTo("foo");
        assertThat(client.events().getObjectStatus(event.key, "A-3").status).isEqualTo(FREE);
    }

    @Test
    public void holdToken() {
        String chartKey = createTestChart();
        Event event = client.events().create(chartKey);
        HoldToken holdToken = client.holdTokens().create();
        client.events().hold(event.key, newArrayList("A-1", "A-2"), holdToken.holdToken);

        client.events().changeObjectStatus(event.key, newArrayList("A-1", "A-2"), "foo", holdToken.holdToken);

        ObjectStatus status1 = client.events().getObjectStatus(event.key, "A-1");
        assertThat(status1.status).isEqualTo("foo");
        assertThat(status1.holdToken).isNull();

        ObjectStatus status2 = client.events().getObjectStatus(event.key, "A-2");
        assertThat(status2.status).isEqualTo("foo");
        assertThat(status2.holdToken).isNull();
    }

    @Test
    public void orderId() {
        String chartKey = createTestChart();
        Event event = client.events().create(chartKey);

        client.events().changeObjectStatus(event.key, newArrayList("A-1", "A-2"), "foo", null, "order1");

        assertThat(client.events().getObjectStatus(event.key, "A-1").orderId).isEqualTo("order1");
        assertThat(client.events().getObjectStatus(event.key, "A-2").orderId).isEqualTo("order1");
    }

    @Test
    public void ticketType() {
        String chartKey = createTestChart();
        Event event = client.events().create(chartKey);
        SeatsioObject object1 = new SeatsioObject("A-1", "T1");
        SeatsioObject object2 = new SeatsioObject("A-2", "T2");

        client.events().changeObjectStatus(event.key, newArrayList(object1, object2), "foo");

        ObjectStatus status1 = client.events().getObjectStatus(event.key, "A-1");
        assertThat(status1.status).isEqualTo("foo");
        assertThat(status1.ticketType).isEqualTo("T1");

        ObjectStatus status2 = client.events().getObjectStatus(event.key, "A-2");
        assertThat(status2.status).isEqualTo("foo");
        assertThat(status2.ticketType).isEqualTo("T2");
    }

    @Test
    public void quantity() {
        String chartKey = createTestChart();
        Event event = client.events().create(chartKey);
        SeatsioObject object1 = new SeatsioObject("GA1", 5);
        SeatsioObject object2 = new SeatsioObject("GA2", 10);

        client.events().changeObjectStatus(event.key, newArrayList(object1, object2), "foo");

        ObjectStatus status1 = client.events().getObjectStatus(event.key, "GA1");
        assertThat(status1.quantity).isEqualTo(5);

        ObjectStatus status2 = client.events().getObjectStatus(event.key, "GA2");
        assertThat(status2.quantity).isEqualTo(10);
    }

    @Test
    public void extraData() {
        String chartKey = createTestChart();
        Event event = client.events().create(chartKey);
        SeatsioObject object1 = new SeatsioObject("A-1", ImmutableMap.of("foo", "bar"));
        SeatsioObject object2 = new SeatsioObject("A-2", ImmutableMap.of("foo", "baz"));

        client.events().changeObjectStatus(event.key, newArrayList(object1, object2), "foo");

        ObjectStatus status1 = client.events().getObjectStatus(event.key, "A-1");
        assertThat(status1.extraData).isEqualTo(ImmutableMap.of("foo", "bar"));

        ObjectStatus status2 = client.events().getObjectStatus(event.key, "A-2");
        assertThat(status2.extraData).isEqualTo(ImmutableMap.of("foo", "baz"));
    }
}
