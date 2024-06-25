package seatsio.events;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.SeatsioException;
import seatsio.holdTokens.HoldToken;

import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class ChangeObjectStatusForMultipleEventsTest extends SeatsioClientTest {

    @Test
    public void test() {
        String chartKey = createTestChart();
        Event event1 = client.events.create(chartKey);
        Event event2 = client.events.create(chartKey);

        client.events.changeObjectStatus(asList(event1.key, event2.key), List.of("A-1", "A-2"), "foo", null, null, null, null, null);

        assertThat(client.events.retrieveObjectInfo(event1.key, "A-1").status).isEqualTo("foo");
        assertThat(client.events.retrieveObjectInfo(event1.key, "A-2").status).isEqualTo("foo");
        assertThat(client.events.retrieveObjectInfo(event2.key, "A-1").status).isEqualTo("foo");
        assertThat(client.events.retrieveObjectInfo(event2.key, "A-2").status).isEqualTo("foo");
    }

    @Test
    public void testConvenienceVersion() {
        String chartKey = createTestChart();
        Event event1 = client.events.create(chartKey);
        Event event2 = client.events.create(chartKey);

        client.events.changeObjectStatus(asList(event1.key, event2.key), List.of("A-1", "A-2"), "foo");

        assertThat(client.events.retrieveObjectInfo(event1.key, "A-1").status).isEqualTo("foo");
        assertThat(client.events.retrieveObjectInfo(event1.key, "A-2").status).isEqualTo("foo");
        assertThat(client.events.retrieveObjectInfo(event2.key, "A-1").status).isEqualTo("foo");
        assertThat(client.events.retrieveObjectInfo(event2.key, "A-2").status).isEqualTo("foo");
    }

    @Test
    public void book() {
        String chartKey = createTestChart();
        Event event1 = client.events.create(chartKey);
        Event event2 = client.events.create(chartKey);

        client.events.book(asList(event1.key, event2.key), List.of("A-1", "A-2"), null, null, null, null, null);

        assertThat(client.events.retrieveObjectInfo(event1.key, "A-1").status).isEqualTo(EventObjectInfo.BOOKED);
        assertThat(client.events.retrieveObjectInfo(event1.key, "A-2").status).isEqualTo(EventObjectInfo.BOOKED);
        assertThat(client.events.retrieveObjectInfo(event2.key, "A-1").status).isEqualTo(EventObjectInfo.BOOKED);
        assertThat(client.events.retrieveObjectInfo(event2.key, "A-2").status).isEqualTo(EventObjectInfo.BOOKED);
    }

    @Test
    public void hold() {
        String chartKey = createTestChart();
        Event event1 = client.events.create(chartKey);
        Event event2 = client.events.create(chartKey);
        HoldToken token = client.holdTokens.create();

        client.events.hold(asList(event1.key, event2.key), List.of("A-1", "A-2"), token.holdToken, null, null, null, null);

        assertThat(client.events.retrieveObjectInfo(event1.key, "A-1").status).isEqualTo(EventObjectInfo.HELD);
        assertThat(client.events.retrieveObjectInfo(event1.key, "A-2").status).isEqualTo(EventObjectInfo.HELD);
        assertThat(client.events.retrieveObjectInfo(event2.key, "A-1").status).isEqualTo(EventObjectInfo.HELD);
        assertThat(client.events.retrieveObjectInfo(event2.key, "A-2").status).isEqualTo(EventObjectInfo.HELD);
    }

    @Test
    public void release() {
        String chartKey = createTestChart();
        Event event1 = client.events.create(chartKey);
        Event event2 = client.events.create(chartKey);
        client.events.book(asList(event1.key, event2.key), List.of("A-1", "A-2"), null, null, null, null, null);

        client.events.release(asList(event1.key, event2.key), List.of("A-1", "A-2"), null, null, null, null, null);

        assertThat(client.events.retrieveObjectInfo(event1.key, "A-1").status).isEqualTo(EventObjectInfo.FREE);
        assertThat(client.events.retrieveObjectInfo(event1.key, "A-2").status).isEqualTo(EventObjectInfo.FREE);
        assertThat(client.events.retrieveObjectInfo(event2.key, "A-1").status).isEqualTo(EventObjectInfo.FREE);
        assertThat(client.events.retrieveObjectInfo(event2.key, "A-2").status).isEqualTo(EventObjectInfo.FREE);
    }

    @Test
    public void allowedPreviousStatuses() {
        String chartKey = createTestChart();
        Event event1 = client.events.create(chartKey);
        Event event2 = client.events.create(chartKey);

        try {
            client.events.changeObjectStatus(asList(event1.key, event2.key), List.of("A-1"), EventObjectInfo.BOOKED, null, null, null, null, null, Set.of("MustBeThisStatus"), null);
            fail("expected exception");
        } catch (SeatsioException e) {
            assertThat(e.errors).hasSize(1);
            assertThat(e.errors.get(0).getCode()).isEqualTo("ILLEGAL_STATUS_CHANGE");
        }
    }

    @Test
    public void rejectedPreviousStatuses() {
        String chartKey = createTestChart();
        Event event1 = client.events.create(chartKey);
        Event event2 = client.events.create(chartKey);

        try {
            client.events.changeObjectStatus(asList(event1.key, event2.key), List.of("A-1"), EventObjectInfo.BOOKED, null, null, null, null, null, null, Set.of("free"));
            fail("expected exception");
        } catch (SeatsioException e) {
            assertThat(e.errors).hasSize(1);
            assertThat(e.errors.get(0).getCode()).isEqualTo("ILLEGAL_STATUS_CHANGE");
        }
    }

}
