package seatsio.events;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class ListStatusChangesForObjectTest extends SeatsioClientTest {

    @Test
    public void test() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.changeObjectStatus(event.key, asList("A-1"), "s1");
        client.events.changeObjectStatus(event.key, asList("A-1"), "s2");
        client.events.changeObjectStatus(event.key, asList("A-1"), "s3");
        client.events.changeObjectStatus(event.key, asList("A-1"), "s4");

        Stream<StatusChange> statusChanges = client.events.statusChangesForObject(event.key, "A-1").all();

        assertThat(statusChanges)
                .extracting(statusChange -> statusChange.status)
                .containsExactly("s4", "s3", "s2", "s1");
    }

}
