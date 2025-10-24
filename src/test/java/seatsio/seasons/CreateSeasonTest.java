package seatsio.seasons;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.charts.Chart;
import seatsio.events.Channel;
import seatsio.events.ForSaleConfig;
import seatsio.events.ForSaleConfigParams;
import seatsio.events.TableBookingConfig;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        assertThat(season.key).isNotNull();
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
    public void nameCanBePassedIn() {
        Chart chart = client.charts.create();

        Season season = client.seasons.create(chart.key, new SeasonParams().name("aSeason"));

        assertThat(season.name).isEqualTo("aSeason");
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

        Season season = client.seasons.create(chart.key, new SeasonParams().key("aSeason").eventKeys(List.of("event1", "event2")));

        assertThat(season.events).extracting(event -> event.key).containsExactly("event1", "event2");
    }

    @Test
    public void tableBookingConfigCustomCanBePassedIn() {
        String chartKey = createTestChartWithTables();

        Season season = client.seasons.create(chartKey, new SeasonParams().tableBookingConfig(TableBookingConfig.custom(Map.of("T1", BY_TABLE, "T2", BY_SEAT))));

        assertThat(season.key).isNotNull();
        assertThat(season.tableBookingConfig).isEqualTo(TableBookingConfig.custom(Map.of("T1", BY_TABLE, "T2", BY_SEAT)));
    }

    @Test
    public void tableBookingConfigInheritCanBePassedIn() {
        String chartKey = createTestChartWithTables();

        Season season = client.seasons.create(chartKey, new SeasonParams().tableBookingConfig(TableBookingConfig.inherit()));

        assertThat(season.key).isNotNull();
        assertThat(season.tableBookingConfig).isEqualTo(TableBookingConfig.inherit());
    }

    @Test
    public void channelsCanBePassedIn() {
        String chartKey = createTestChart();
        List<Channel> channels = List.of(
                new Channel("channelKey1", "channel 1", "#FFFF99", 1, Set.of("A-1")),
                new Channel("channelKey2", "channel 2", "#FFFF99", 2, Set.of("A-2"))
        );

        Season season = client.seasons.create(chartKey, new SeasonParams().channels(channels));

        assertThat(season.key).isNotNull();
        assertThat(season.channels).isEqualTo(channels);
    }

    @Test
    public void forSaleConfigCanBePassedIn() {
        String chartKey = createTestChart();
        ForSaleConfigParams params = new ForSaleConfigParams(false, List.of("A-1"), Map.of("GA1", 5), List.of("Cat1"));

        Season season = client.seasons.create(chartKey, new SeasonParams().forSaleConfigParams(params));

        ForSaleConfig forSaleConfig = new ForSaleConfig();
        forSaleConfig.forSale = params.forSale;
        forSaleConfig.objects = params.objects;
        forSaleConfig.areaPlaces = params.areaPlaces;
        forSaleConfig.categories = params.categories;
        assertThat(season.forSaleConfig).isEqualTo(forSaleConfig);
    }

    @Test
    public void forSalePropagatedCanBePassedIn() {
        String chartKey = createTestChart();
        ForSaleConfigParams params = new ForSaleConfigParams(false, List.of("A-1"), null, null);

        Season season = client.seasons.create(chartKey, new SeasonParams().forSalePropagated(false).forSaleConfigParams(params).eventKeys(List.of("event1")));

        assertThat(season.forSalePropagated).isFalse();
        assertThat(client.events.retrieve("event1").season.forSaleConfig).isNull();
    }
}
