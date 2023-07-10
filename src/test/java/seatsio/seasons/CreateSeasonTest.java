package seatsio.seasons;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.charts.Chart;
import seatsio.events.TableBookingConfig;

import java.time.Instant;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;
import static seatsio.events.TableBookingMode.BY_SEAT;
import static seatsio.events.TableBookingMode.BY_TABLE;

public class CreateSeasonTest extends SeatsioClientTest {

    @Test
    public void chartKeyIsRequired() {
        String chartKey = createTestChart();

        Season season = client.seasons.create(chartKey);

        assertThat(season.id).isNotZero();
        assertThat(season.isTopLevelSeason).isTrue();
        assertThat(season.topLevelSeasonKey).isNull();
        assertThat(season.key).isNotNull();
        assertThat(season.partialSeasonKeys).isEmpty();
        assertThat(season.id).isNotZero();
        assertThat(season.key).isEqualTo(season.key);
        assertThat(season.chartKey).isEqualTo(chartKey);
        assertThat(season.tableBookingConfig).isEqualTo(TableBookingConfig.inherit());
        assertThat(season.supportsBestAvailable).isTrue();
        assertThat(season.forSaleConfig).isNull();
        Instant now = Instant.now();
        assertThat(season.createdOn).isBetween(now.minus(1, MINUTES), now.plus(1, MINUTES));
        assertThat(season.updatedOn).isNull();

        assertThat(season.events).isEmpty();
    }

    @Test
    public void keyCanBePassedIn() {
        Chart chart = client.charts.create();

        Season season = client.seasons.create(chart.key, new SeasonParams().key("aSeason"));

        assertThat(season.key).isEqualTo("aSeason");
    }

    @Test
    public void numberOfEventsCanBePassedIn() {
        Chart chart = client.charts.create();

        Season season = client.seasons.create(chart.key, new SeasonParams().key("aSeason").numberOfEvents(2));

        assertThat(season.events).hasSize(2);
    }

    @Test
    public void eventKeysCanBePassedIn() {
        Chart chart = client.charts.create();

        Season season = client.seasons.create(chart.key, new SeasonParams().key("aSeason").eventKeys(newArrayList("event1", "event2")));

        assertThat(season.events).extracting(event -> event.key).containsExactly("event1", "event2");
    }

    @Test
    public void tableBookingConfigCustomCanBePassedIn() {
        String chartKey = createTestChartWithTables();

        Season season = client.seasons.create(chartKey, new SeasonParams().tableBookingConfig(TableBookingConfig.custom(ImmutableMap.of("T1", BY_TABLE, "T2", BY_SEAT))));

        assertThat(season.key).isNotNull();
        assertThat(season.tableBookingConfig).isEqualTo(TableBookingConfig.custom(ImmutableMap.of("T1", BY_TABLE, "T2", BY_SEAT)));
    }

    @Test
    public void tableBookingConfigInheritCanBePassedIn() {
        String chartKey = createTestChartWithTables();

        Season season = client.seasons.create(chartKey, new SeasonParams().tableBookingConfig(TableBookingConfig.inherit()));

        assertThat(season.key).isNotNull();
        assertThat(season.tableBookingConfig).isEqualTo(TableBookingConfig.inherit());
    }
}
