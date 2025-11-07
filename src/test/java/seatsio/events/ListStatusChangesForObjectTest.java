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
                new StatusChangeRequest.Builder().withEventKey(event.key()).withObjects(List.of("A-1")).withStatus("s1").build(),
                new StatusChangeRequest.Builder().withEventKey(event.key()).withObjects(List.of("A-1")).withStatus("s2").build(),
                new StatusChangeRequest.Builder().withEventKey(event.key()).withObjects(List.of("A-1")).withStatus("s3").build(),
                new StatusChangeRequest.Builder().withEventKey(event.key()).withObjects(List.of("A-1")).withStatus("s4").build()
        ));
        waitForStatusChanges(client, event, 4);

        Stream<StatusChange> statusChanges = client.events.statusChangesForObject(event.key(), "A-1").all();

        assertThat(statusChanges)
                .extracting(StatusChange::status)
                .containsExactly("s4", "s3", "s2", "s1");
    }
}
