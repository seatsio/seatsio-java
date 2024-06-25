package seatsio.events;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static seatsio.events.ListStatusChangesTest.waitForStatusChanges;

public class ListStatusChangesForObjectTest extends SeatsioClientTest {

    @Test
    public void test() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.changeObjectStatus(List.of(
                new StatusChangeRequest(event.key, List.of("A-1"), "s1"),
                new StatusChangeRequest(event.key, List.of("A-1"), "s2"),
                new StatusChangeRequest(event.key, List.of("A-1"), "s3"),
                new StatusChangeRequest(event.key, List.of("A-1"), "s4")
        ));
        waitForStatusChanges(client, event, 4);

        Stream<StatusChange> statusChanges = client.events.statusChangesForObject(event.key, "A-1").all();

        assertThat(statusChanges)
                .extracting(statusChange -> statusChange.status)
                .containsExactly("s4", "s3", "s2", "s1");
    }
}
