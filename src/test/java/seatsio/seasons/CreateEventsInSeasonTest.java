package seatsio.seasons;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.events.Event;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateEventsInSeasonTest extends SeatsioClientTest {

    @Test
    public void eventKeys() {
        String chartKey = createTestChart();
        Season season = client.seasons.create(chartKey);

        List<Event> events = client.seasons.createEvents(season.key(), List.of("event1", "event2"));

        assertThat(events)
                .extracting(Event::key)
                .containsExactly("event2", "event1");
    }

    @Test
    public void numberOfEvents() {
        String chartKey = createTestChart();
        Season season = client.seasons.create(chartKey);

        List<Event> events = client.seasons.createEvents(season.key(), 2);

        assertThat(events).hasSize(2);
    }
}
