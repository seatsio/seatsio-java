package seatsio.events;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.charts.Category;
import seatsio.charts.CategoryKey;
import seatsio.charts.Chart;
import seatsio.charts.SocialDistancingRuleset;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
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
        assertThat(event.categories).containsExactly(
                new Category(9L, "Cat1", "#87A9CD", false),
                new Category(10L, "Cat2", "#5E42ED", false),
                new Category("string11", "Cat3", "#5E42BB", false)
        );
    }

    @Test
    public void createEventWithEventCreationParams() {
        String chartKey = createTestChart();
        EventCreationParams params = new EventCreationParams("anEvent");

        Event event = client.events.create(chartKey, params);

        assertThat(event.key).isEqualTo("anEvent");
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

        Event event = client.events.create(chartKey, null, null, "ruleset1", null, null);

        assertThat(event.socialDistancingRulesetKey).isEqualTo("ruleset1");
    }

    @Test
    public void objectCategoriesCanBePassedIn() {
        String chartKey = createTestChart();
        Map<String, CategoryKey> objectCategories = ImmutableMap.of("A-1", CategoryKey.of(10L));

        Event event = client.events.create(chartKey, null, null, null, objectCategories, null);

        assertThat(event.objectCategories).containsOnly(entry("A-1", CategoryKey.of(10L)));
    }

    @Test
    public void categoriesCanBePassedIn() {
        String chartKey = createTestChart();
        Category eventCategory = new Category("eventCategory", "event-level category", "#AAABBB");
        List<Category> categories = newArrayList(
                eventCategory
        );
        EventCreationParams params = new EventCreationParams(null, categories);

        Event event = client.events.create(chartKey, params);

        int numberOfCategoriesOnChart = 3; // see sampleChart.json
        assertThat(event.categories)
                .hasSize(numberOfCategoriesOnChart + categories.size())
                .contains(eventCategory);
    }

}
