package seatsio.seasons;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.charts.Chart;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CreatePartialSeasonTest extends SeatsioClientTest {

    @Test
    public void keyCanBePassedIn() {
        Chart chart = client.charts.create();
        Season topLevelSeason = client.seasons.create(chart.key, new CreateSeasonParams().key("aTopLevelSeason"));

        Season partialSeason = client.seasons.createPartialSeason(topLevelSeason.key, "aPartialSeason", null, null);

        assertThat(partialSeason.key).isEqualTo("aPartialSeason");
        assertThat(partialSeason.isPartialSeason).isTrue();
        assertThat(partialSeason.topLevelSeasonKey).isEqualTo(topLevelSeason.key);
    }

    @Test
    public void nameCanBePassedIn() {
        Chart chart = client.charts.create();
        Season topLevelSeason = client.seasons.create(chart.key, new CreateSeasonParams().key("aTopLevelSeason"));

        Season partialSeason = client.seasons.createPartialSeason(topLevelSeason.key, null, "aPartialSeason", null);

        assertThat(partialSeason.name).isEqualTo("aPartialSeason");
        assertThat(partialSeason.isPartialSeason).isTrue();
        assertThat(partialSeason.topLevelSeasonKey).isEqualTo(topLevelSeason.key);
    }

    @Test
    public void eventKeysCanBePassedIn() {
        Chart chart = client.charts.create();
        CreateSeasonParams seasonParams = new CreateSeasonParams()
                .key("aTopLevelSeason")
                .eventKeys(List.of("event1", "event2", "event3"));
        Season topLevelSeason = client.seasons.create(chart.key, seasonParams);

        Season season = client.seasons.createPartialSeason(topLevelSeason.key, null, null, List.of("event1", "event3"));

        assertThat(season.events).extracting(event -> event.key).containsExactly("event1", "event3");
        assertThat(season.events.get(0).partialSeasonKeysForEvent).containsExactly(season.key);
    }
}
