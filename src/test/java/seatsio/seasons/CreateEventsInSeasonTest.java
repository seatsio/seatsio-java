package seatsio.seasons;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

public class CreateEventsInSeasonTest extends SeatsioClientTest {

    @Test
    public void eventKeys() {
        String chartKey = createTestChart();
        Season season = client.seasons.create(chartKey);

        Season updatedSeason = client.seasons.createEvents(season.key, newArrayList("event1", "event2"));

        assertThat(updatedSeason.events)
                .extracting(s -> s.key)
                .containsExactly("event1", "event2");
    }

    @Test
    public void numberOfEvents() {
        String chartKey = createTestChart();
        Season season = client.seasons.create(chartKey);

        Season updatedSeason = client.seasons.createEvents(season.key, 2);

        assertThat(updatedSeason.events).hasSize(2);
    }
}
