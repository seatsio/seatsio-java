package seatsio.events;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.SeatsioException;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class ChangeObjectStatusInBatchTest extends SeatsioClientTest {

    @Test
    public void test() {
        String chartKey1 = createTestChart();
        String chartKey2 = createTestChart();
        Event event1 = client.events.create(chartKey1);
        Event event2 = client.events.create(chartKey2);

        List<ChangeObjectStatusResult> result = client.events.changeObjectStatus(List.of(
                new StatusChangeRequest(event1.key, List.of("A-1"), "lolzor"),
                new StatusChangeRequest(event2.key, List.of("A-2"), "lolzor")
        ));

        assertThat(result.get(0).objects.get("A-1").status).isEqualTo("lolzor");
        assertThat(client.events.retrieveObjectInfo(event1.key, "A-1").status).isEqualTo("lolzor");

        assertThat(result.get(1).objects.get("A-2").status).isEqualTo("lolzor");
        assertThat(client.events.retrieveObjectInfo(event2.key, "A-2").status).isEqualTo("lolzor");
    }

    @Test
    public void channelKeys() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey, new CreateEventParams().withChannels(List.of(
                new Channel("channelKey1", "channel 1", "#FFFF99", 1, Set.of("A-1"))
        )));

        List<ChangeObjectStatusResult> result = client.events.changeObjectStatus(List.of(
                new StatusChangeRequest(event.key, List.of("A-1"), "lolzor", null, null, null, null, Set.of("channelKey1"))
        ));

        assertThat(result.get(0).objects.get("A-1").status).isEqualTo("lolzor");
    }

    @Test
    public void ignoreChannels() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey, new CreateEventParams().withChannels(List.of(
                new Channel("channelKey1", "channel 1", "#FFFF99", 1, Set.of("A-1"))
        )));

        List<ChangeObjectStatusResult> result = client.events.changeObjectStatus(List.of(
                new StatusChangeRequest(event.key, List.of("A-1"), "lolzor", null, null, null, true, null)
        ));

        assertThat(result.get(0).objects.get("A-1").status).isEqualTo("lolzor");
    }

    @Test
    public void allowedPreviousStatus() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        try {
            client.events.changeObjectStatus(List.of(
                    new StatusChangeRequest(event.key, List.of("A-1"), "lolzor", null, null, null, true, null, Set.of("MustBeThisStatus"), null)
            ));
            fail("expected exception");
        } catch (SeatsioException e) {
            assertThat(e.errors).hasSize(1);
            assertThat(e.errors.get(0).getCode()).isEqualTo("ILLEGAL_STATUS_CHANGE");
        }
    }

    @Test
    public void rejectedPreviousStatus() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        try {
            client.events.changeObjectStatus(List.of(
                    new StatusChangeRequest(event.key, List.of("A-1"), "lolzor", null, null, null, true, null, null, Set.of("free"))
            ));
            fail("expected exception");
        } catch (SeatsioException e) {
            assertThat(e.errors).hasSize(1);
            assertThat(e.errors.get(0).getCode()).isEqualTo("ILLEGAL_STATUS_CHANGE");
        }
    }
}
