package seatsio.events;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.charts.CategoryKey;
import seatsio.charts.Chart;
import seatsio.charts.SocialDistancingRuleset;

import java.time.Instant;
import java.util.Map;

import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static seatsio.events.TableBookingMode.BY_SEAT;
import static seatsio.events.TableBookingMode.BY_TABLE;

public class CreateEventTest extends SeatsioClientTest {

    @Test
    public void chartKeyIsRequired() {
        String chartKey = createTestChart();

        Event event = client.events.create(chartKey);

        assertThat(event.id).isNotZero();
        assertThat(event.key).isNotNull();
        assertThat(event.chartKey).isEqualTo(chartKey);
        assertThat(event.tableBookingConfig).isEqualTo(TableBookingConfig.inherit());
        assertThat(event.supportsBestAvailable).isTrue();
        assertThat(event.forSaleConfig).isNull();
        Instant now = Instant.now();
        assertThat(event.createdOn).isBetween(now.minus(1, MINUTES), now.plus(1, MINUTES));
        assertThat(event.updatedOn).isNull();
    }

    @Test
    public void eventKeyCanBePassedIn() {
        Chart chart = client.charts.create();

        Event event = client.events.create(chart.key, "anEvent");

        assertThat(event.key).isEqualTo("anEvent");
    }

    @Test
    public void tableBookingConfigCustomCanBePassedIn() {
        String chartKey = createTestChartWithTables();

        Event event = client.events.create(chartKey, null, TableBookingConfig.custom(ImmutableMap.of("T1", BY_TABLE, "T2", BY_SEAT)));

        assertThat(event.key).isNotNull();
        assertThat(event.tableBookingConfig).isEqualTo(TableBookingConfig.custom(ImmutableMap.of("T1", BY_TABLE, "T2", BY_SEAT)));
    }

    @Test
    public void tableBookingConfigInheritCanBePassedIn() {
        String chartKey = createTestChartWithTables();

        Event event = client.events.create(chartKey, null, TableBookingConfig.inherit());

        assertThat(event.key).isNotNull();
        assertThat(event.tableBookingConfig).isEqualTo(TableBookingConfig.inherit());
    }

    @Test
    public void socialDistancingRulesetKeyCanBePassedIn() {
        String chartKey = createTestChartWithTables();
        Map<String, SocialDistancingRuleset> rulesets = ImmutableMap.of("ruleset1", SocialDistancingRuleset.ruleBased("My ruleset").build());
        client.charts.saveSocialDistancingRulesets(chartKey, rulesets);

        Event event = client.events.create(chartKey, null, null, "ruleset1", null);

        assertThat(event.socialDistancingRulesetKey).isEqualTo("ruleset1");
    }

    @Test
    public void objectCategoriesCanBePassedIn() {
        String chartKey = createTestChart();
        Map<String, CategoryKey> objectCategories = ImmutableMap.of("A-1", CategoryKey.of(10L));

        Event event = client.events.create(chartKey, null, null, null, objectCategories);

        assertThat(event.objectCategories).containsOnly(entry("A-1", CategoryKey.of(10L)));
    }
}
