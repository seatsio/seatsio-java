package seatsio.events;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.charts.Chart;
import seatsio.charts.SocialDistancingRuleset;

import java.util.Map;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;
import static seatsio.events.TableBookingMode.BY_SEAT;
import static seatsio.events.TableBookingMode.BY_TABLE;

public class UpdateEventTest extends SeatsioClientTest {

    @Test
    public void updateChartKey() {
        Chart chart1 = client.charts.create();
        Event event = client.events.create(chart1.key);
        Chart chart2 = client.charts.create();

        client.events.update(event.key, chart2.key, null);

        Event retrievedEvent = client.events.retrieve(event.key);
        assertThat(retrievedEvent.key).isEqualTo(event.key);
        assertThat(retrievedEvent.chartKey).isEqualTo(chart2.key);
        assertThat(retrievedEvent.updatedOn).isBetween(now().minus(1, MINUTES), now().plus(1, MINUTES));
    }

    @Test
    public void updateEventKey() {
        Chart chart = client.charts.create();
        Event event = client.events.create(chart.key);

        client.events.update(event.key, null, "newKey");

        Event retrievedEvent = client.events.retrieve("newKey");
        assertThat(retrievedEvent.key).isEqualTo("newKey");
        assertThat(retrievedEvent.id).isEqualTo(event.id);
    }

    @Test
    public void updateTableBookingConfig() {
        String chartKey = createTestChartWithTables();
        Event event = client.events.create(chartKey);

        client.events.update(event.key, null, null, TableBookingConfig.custom(ImmutableMap.of("T1", BY_TABLE, "T2", BY_SEAT)));

        Event retrievedEvent = client.events.retrieve(event.key);
        assertThat(retrievedEvent.tableBookingConfig).isEqualTo(TableBookingConfig.custom(ImmutableMap.of("T1", BY_TABLE, "T2", BY_SEAT)));
    }

    @Test
    public void updateSocialDistancingRulesetKey() {
        String chartKey = createTestChartWithTables();
        Map<String, SocialDistancingRuleset> rulesets = ImmutableMap.of(
                "ruleset1", SocialDistancingRuleset.ruleBased("My first ruleset").build(),
                "ruleset2", SocialDistancingRuleset.ruleBased("My second ruleset").build()
        );
        client.charts.saveSocialDistancingRulesets(chartKey, rulesets);
        Event event = client.events.create(chartKey, null, null, "ruleset1");

        client.events.update(event.key, null, null, null, "ruleset2");

        Event retrievedEvent = client.events.retrieve(event.key);
        assertThat(retrievedEvent.socialDistancingRulesetKey).isEqualTo("ruleset2");
    }

    @Test
    public void removeSocialDistancingRulesetKey() {
        String chartKey = createTestChartWithTables();
        Map<String, SocialDistancingRuleset> rulesets = ImmutableMap.of(
                "ruleset1", SocialDistancingRuleset.ruleBased("My first ruleset").build(),
                "ruleset2", SocialDistancingRuleset.ruleBased("My second ruleset").build()
        );
        client.charts.saveSocialDistancingRulesets(chartKey, rulesets);
        Event event = client.events.create(chartKey, null, null, "ruleset1");

        client.events.update(event.key, null, null, null, "");

        Event retrievedEvent = client.events.retrieve(event.key);
        assertThat(retrievedEvent.socialDistancingRulesetKey).isNull();
    }
}
