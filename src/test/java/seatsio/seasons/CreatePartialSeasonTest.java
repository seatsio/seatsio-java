package seatsio.seasons;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.charts.Chart;
import seatsio.events.Event;
import seatsio.events.TableBookingConfig;

import java.time.Instant;

import static com.google.common.collect.Lists.newArrayList;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;

public class CreatePartialSeasonTest extends SeatsioClientTest {

    @Test
    public void keyCanBePassedIn() {
        Chart chart = client.charts.create();
        Season topLevelSeason = client.seasons.create(chart.key, "aTopLevelSeason", newArrayList(), null, null);

        Season partialSeason = client.seasons.createPartialSeason(topLevelSeason.key, "aPartialSeason", newArrayList());

        assertThat(partialSeason.key).isEqualTo("aPartialSeason");
    }

    @Test
    public void eventKeysCanBePassedIn() {
        Chart chart = client.charts.create();
        Season topLevelSeason = client.seasons.create(chart.key, "aTopLevelSeason", newArrayList("event1", "event2", "event3"), null, null);

        Season season = client.seasons.createPartialSeason(topLevelSeason.key, "aPartialSeason", newArrayList("event1", "event3"));

        assertThat(season.events).extracting(event -> event.key).containsExactly("event1", "event3");
    }
}
