package seatsio.seasons;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.charts.Chart;
import seatsio.charts.SocialDistancingRuleset;
import seatsio.events.Event;
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
        assertThat(season.key).isNotNull();
        assertThat(season.partialSeasonKeys).isEmpty();

        Event seasonEvent = season.seasonEvent;
        assertThat(seasonEvent.id).isNotZero();
        assertThat(seasonEvent.key).isEqualTo(season.key);
        assertThat(seasonEvent.chartKey).isEqualTo(chartKey);
        assertThat(seasonEvent.tableBookingConfig).isEqualTo(TableBookingConfig.inherit());
        assertThat(seasonEvent.supportsBestAvailable).isTrue();
        assertThat(seasonEvent.forSaleConfig).isNull();
        Instant now = Instant.now();
        assertThat(seasonEvent.createdOn).isBetween(now.minus(1, MINUTES), now.plus(1, MINUTES));
        assertThat(seasonEvent.updatedOn).isNull();

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
        Event seasonEvent = season.seasonEvent;
        assertThat(seasonEvent.tableBookingConfig).isEqualTo(TableBookingConfig.custom(ImmutableMap.of("T1", BY_TABLE, "T2", BY_SEAT)));
    }

    @Test
    public void tableBookingConfigInheritCanBePassedIn() {
        String chartKey = createTestChartWithTables();

        Season season = client.seasons.create(chartKey, new SeasonParams().tableBookingConfig(TableBookingConfig.inherit()));

        assertThat(season.key).isNotNull();
        Event seasonEvent = season.seasonEvent;
        assertThat(seasonEvent.tableBookingConfig).isEqualTo(TableBookingConfig.inherit());
    }

    @Test
    public void socialDistancingRulesetKeyCanBePassedIn() {
        String chartKey = createTestChartWithTables();
        Map<String, SocialDistancingRuleset> rulesets = ImmutableMap.of("ruleset1", SocialDistancingRuleset.ruleBased("My ruleset").build());
        client.charts.saveSocialDistancingRulesets(chartKey, rulesets);

        Season season = client.seasons.create(chartKey, new SeasonParams().socialDistancingRulesetKey("ruleset1"));

        Event seasonEvent = season.seasonEvent;
        assertThat(seasonEvent.socialDistancingRulesetKey).isEqualTo("ruleset1");
    }

}
