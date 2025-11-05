package seatsio.seasons;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.charts.Chart;
import seatsio.events.Event;
import seatsio.events.TableBookingConfig;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static seatsio.events.TableBookingMode.BY_SEAT;
import static seatsio.events.TableBookingMode.BY_TABLE;

public class UpdateSeasonTest extends SeatsioClientTest {

    @Test
    public void canUpdateSeasonName() {
        Chart chart = client.charts.create();
        Season season = client.seasons.create(chart.key, new CreateSeasonParams().name("Original name"));

        client.seasons.update(season.key, new UpdateSeasonParams().name("New name"));

        Season retrievedSeason = client.seasons.retrieve(season.key);
        assertThat(retrievedSeason.name).isEqualTo("New name");
    }

    @Test
    public void updateSeasonEventKey() {
        Chart chart = client.charts.create();
        Season season = client.seasons.create(chart.key, new CreateSeasonParams().key("someKey"));

        client.seasons.update(season.key, new UpdateSeasonParams().key("someNewKey"));

        Season retrievedSeason = client.seasons.retrieve("someNewKey");
        assertThat(retrievedSeason.key).isEqualTo("someNewKey");
        assertThat(retrievedSeason.id).isEqualTo(season.id);
    }

    @Test
    public void updateTableBookingConfig() {
        String chartKey = createTestChartWithTables();
        Season season = client.seasons.create(chartKey);

        TableBookingConfig newTableBookingConfig = TableBookingConfig.custom(Map.of("T1", BY_TABLE, "T2", BY_SEAT));
        client.seasons.update(season.key, new UpdateSeasonParams().tableBookingConfig(newTableBookingConfig));

        Event retrievedEvent = client.seasons.retrieve(season.key);
        assertThat(retrievedEvent.tableBookingConfig).isEqualTo(newTableBookingConfig);
    }


}
