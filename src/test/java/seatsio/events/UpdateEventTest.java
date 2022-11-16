package seatsio.events;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.charts.Category;
import seatsio.charts.CategoryKey;
import seatsio.charts.Chart;
import seatsio.charts.SocialDistancingRuleset;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static seatsio.events.EventCreationParamsBuilder.anEvent;
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
        Event event = client.events.create(chartKey, anEvent().withSocialDistancingRulesetKey("ruleset1"));

        client.events.update(event.key, null, null, null, "ruleset2", null);

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
        Event event = client.events.create(chartKey, anEvent().withSocialDistancingRulesetKey("ruleset1"));

        client.events.update(event.key, null, null, null, "", null);

        Event retrievedEvent = client.events.retrieve(event.key);
        assertThat(retrievedEvent.socialDistancingRulesetKey).isNull();
    }

    @Test
    public void updateObjectCategories() {
        String chartKey = createTestChart();
        Map<String, CategoryKey> objectCategories = ImmutableMap.of(
                "A-1", CategoryKey.of(9L)
        );
        Event event = client.events.create(chartKey, anEvent().withObjectCategories(objectCategories));

        Map<String, CategoryKey> newObjectCategories = ImmutableMap.of(
                "A-2", CategoryKey.of(10L)
        );

        client.events.update(event.key, null, null, null, null, newObjectCategories);

        Event retrievedEvent = client.events.retrieve(event.key);
        assertThat(retrievedEvent.objectCategories).containsOnly(entry("A-2", CategoryKey.of(10L)));
    }

    @Test
    public void removeObjectCategories() {
        String chartKey = createTestChart();
        Map<String, CategoryKey> objectCategories = ImmutableMap.of(
                "A-1", CategoryKey.of(9L)
        );
        Event event = client.events.create(chartKey, anEvent().withObjectCategories(objectCategories));

        client.events.update(event.key, null, null, null, null, Maps.newHashMap());

        Event retrievedEvent = client.events.retrieve(event.key);
        assertThat(retrievedEvent.objectCategories).isNull();
    }

    @Test
    public void updateCategories() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        Category eventCategory = new Category("eventCategory", "event-level category", "#AAABBB");
        List<Category> categories = newArrayList(
                eventCategory
        );

        client.events.update(event.key, null, null, null, null, null, categories);

        Event retrievedEvent = client.events.retrieve(event.key);
        int numberOfCategoriesOnChart = 3; // see sampleChart.json
        assertThat(retrievedEvent.categories)
                .hasSize(numberOfCategoriesOnChart + categories.size())
                .contains(eventCategory);
    }

    @Test
    public void removeCategories() {
        String chartKey = createTestChart();
        Category eventCategory = new Category("eventCategory", "event-level category", "#AAABBB");
        List<Category> categories = newArrayList(eventCategory);

        Event event = client.events.create(chartKey, anEvent().withCategories(categories));

        client.events.update(event.key, null, null, null, null, null, Lists.newArrayList());

        Event retrievedEvent = client.events.retrieve(event.key);
        int numberOfCategoriesOnChart = 3; // see sampleChart.json
        assertThat(retrievedEvent.categories)
                .hasSize(numberOfCategoriesOnChart)
                .doesNotContain(eventCategory);
    }

}
