package seatsio.seasons;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.charts.Chart;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

public class CreatePartialSeasonTest extends SeatsioClientTest {

    @Test
    public void keyCanBePassedIn() {
        Chart chart = client.charts.create();
        Season topLevelSeason = client.seasons.create(chart.key, new SeasonParams().key("aTopLevelSeason"));

        Season partialSeason = client.seasons.createPartialSeason(topLevelSeason.key, "aPartialSeason", null);

        assertThat(partialSeason.key).isEqualTo("aPartialSeason");
        assertThat(partialSeason.isPartialSeason).isTrue();
        assertThat(partialSeason.topLevelSeasonKey).isEqualTo(topLevelSeason.key);
    }

    @Test
    public void eventKeysCanBePassedIn() {
        Chart chart = client.charts.create();
        SeasonParams seasonParams = new SeasonParams()
                .key("aTopLevelSeason")
                .eventKeys(newArrayList("event1", "event2", "event3"));
        Season topLevelSeason = client.seasons.create(chart.key, seasonParams);

        Season season = client.seasons.createPartialSeason(topLevelSeason.key, null, newArrayList("event1", "event3"));

        assertThat(season.events).extracting(event -> event.key).containsExactly("event1", "event3");
    }
}
