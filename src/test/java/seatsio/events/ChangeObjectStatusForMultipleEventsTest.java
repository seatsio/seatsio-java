package seatsio.events;

import org.junit.Test;
import seatsio.SeatsioClientTest;
import seatsio.holdTokens.HoldToken;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static seatsio.events.ObjectStatus.BOOKED;
import static seatsio.events.ObjectStatus.FREE;
import static seatsio.events.ObjectStatus.HELD;

public class ChangeObjectStatusForMultipleEventsTest extends SeatsioClientTest {

    @Test
    public void test() {
        String chartKey = createTestChart();
        Event event1 = client.events().create(chartKey);
        Event event2 = client.events().create(chartKey);

        client.events().changeObjectStatus(asList(event1.key, event2.key), newArrayList("A-1", "A-2"), "foo", null, null);

        assertThat(client.events().getObjectStatus(event1.key, "A-1").status).isEqualTo("foo");
        assertThat(client.events().getObjectStatus(event1.key, "A-2").status).isEqualTo("foo");
        assertThat(client.events().getObjectStatus(event2.key, "A-1").status).isEqualTo("foo");
        assertThat(client.events().getObjectStatus(event2.key, "A-2").status).isEqualTo("foo");
    }

    @Test
    public void book() {
        String chartKey = createTestChart();
        Event event1 = client.events().create(chartKey);
        Event event2 = client.events().create(chartKey);

        client.events().book(asList(event1.key, event2.key), newArrayList("A-1", "A-2"), null, null);

        assertThat(client.events().getObjectStatus(event1.key, "A-1").status).isEqualTo(BOOKED);
        assertThat(client.events().getObjectStatus(event1.key, "A-2").status).isEqualTo(BOOKED);
        assertThat(client.events().getObjectStatus(event2.key, "A-1").status).isEqualTo(BOOKED);
        assertThat(client.events().getObjectStatus(event2.key, "A-2").status).isEqualTo(BOOKED);
    }

    @Test
    public void hold() {
        String chartKey = createTestChart();
        Event event1 = client.events().create(chartKey);
        Event event2 = client.events().create(chartKey);
        HoldToken token = client.holdTokens().create();

        client.events().hold(asList(event1.key, event2.key), newArrayList("A-1", "A-2"), token.holdToken, null);

        assertThat(client.events().getObjectStatus(event1.key, "A-1").status).isEqualTo(HELD);
        assertThat(client.events().getObjectStatus(event1.key, "A-2").status).isEqualTo(HELD);
        assertThat(client.events().getObjectStatus(event2.key, "A-1").status).isEqualTo(HELD);
        assertThat(client.events().getObjectStatus(event2.key, "A-2").status).isEqualTo(HELD);
    }

    @Test
    public void release() {
        String chartKey = createTestChart();
        Event event1 = client.events().create(chartKey);
        Event event2 = client.events().create(chartKey);
        client.events().book(asList(event1.key, event2.key), newArrayList("A-1", "A-2"), null, null);

        client.events().release(asList(event1.key, event2.key), newArrayList("A-1", "A-2"), null, null);

        assertThat(client.events().getObjectStatus(event1.key, "A-1").status).isEqualTo(FREE);
        assertThat(client.events().getObjectStatus(event1.key, "A-2").status).isEqualTo(FREE);
        assertThat(client.events().getObjectStatus(event2.key, "A-1").status).isEqualTo(FREE);
        assertThat(client.events().getObjectStatus(event2.key, "A-2").status).isEqualTo(FREE);
    }

}
