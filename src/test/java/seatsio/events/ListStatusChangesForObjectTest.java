package seatsio.events;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static seatsio.events.ListStatusChangesTest.waitForStatusChanges;

public class ListStatusChangesForObjectTest extends SeatsioClientTest {

    @Test
    public void test() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.changeObjectStatus(newArrayList(
                new StatusChangeRequest(event.key, newArrayList("A-1"), "s1"),
                new StatusChangeRequest(event.key, newArrayList("A-1"), "s2"),
                new StatusChangeRequest(event.key, newArrayList("A-1"), "s3"),
                new StatusChangeRequest(event.key, newArrayList("A-1"), "s4")
        ));

        List<StatusChange> statusChanges = waitForStatusChanges(() -> client.events.statusChangesForObject(event.key, "A-1").all());
        assertThat(statusChanges)
                .extracting(statusChange -> statusChange.status)
                .containsExactly("s4", "s3", "s2", "s1");
    }
}
