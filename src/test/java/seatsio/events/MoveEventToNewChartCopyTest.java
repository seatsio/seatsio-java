package seatsio.events;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MoveEventToNewChartCopyTest extends SeatsioClientTest {

    @Test
    public void testEventIsMovedToNewChart() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        Event movedEvent = client.events.moveEventToNewChartCopy(event.key());
        assertThat(movedEvent.key()).isNotNull();
        assertThat(movedEvent.chartKey()).isNotEqualTo(chartKey);
    }

    @Test
    public void testMultipleEventsAreMovedToOneNewChart() {
        String chartKey = createTestChart();
        Event event1 = client.events.create(chartKey);
        Event event2 = client.events.create(chartKey);

        List<Event> movedEvents = client.events.moveEventsToNewChartCopy(List.of(event2.key(), event1.key()));

        assertThat(movedEvents).extracting(Event::key).containsExactly(event2.key(), event1.key());
        assertThat(movedEvents.get(0).chartKey()).isNotEqualTo(chartKey);
        assertThat(movedEvents.get(1).chartKey()).isEqualTo(movedEvents.get(0).chartKey());
    }
}
