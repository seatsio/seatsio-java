package seatsio.events;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

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
}
