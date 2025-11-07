package seatsio.events;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.charts.Category;
import seatsio.charts.CategoryKey;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

        assertThat(event.id()).isNotZero();
        assertThat(event.key()).isNotNull();
        assertThat(event.chartKey()).isEqualTo(chartKey);
        assertThat(event.tableBookingConfig()).isEqualTo(TableBookingConfig.inherit());
        assertThat(event.supportsBestAvailable()).isTrue();
        assertThat(event.forSaleConfig()).isNull();
        Instant now = Instant.now();
        assertThat(event.createdOn()).isBetween(now.minus(1, MINUTES), now.plus(1, MINUTES));
        assertThat(event.updatedOn()).isNull();
        assertThat(event.categories()).containsExactly(
                new Category(9L, "Cat1", "#87A9CD", false),
                new Category(10L, "Cat2", "#5E42ED", false),
                new Category("string11", "Cat3", "#5E42BB", false)
        );
    }

    @Test
    public void eventKeyCanBePassedIn() {
        String chartKey = createTestChart();

        Event event = client.events.create(chartKey, new CreateEventParams().withKey("anEvent"));

        assertThat(event.key()).isEqualTo("anEvent");
    }

    @Test
    public void tableBookingConfigCustomCanBePassedIn() {
        String chartKey = createTestChartWithTables();

        TableBookingConfig tableBookingConfig = TableBookingConfig.custom(Map.of("T1", BY_TABLE, "T2", BY_SEAT));
        Event event = client.events.create(chartKey, new CreateEventParams().withTableBookingConfig(tableBookingConfig));

        assertThat(event.key()).isNotNull();
        assertThat(event.tableBookingConfig()).isEqualTo(tableBookingConfig);
    }

    @Test
    public void tableBookingConfigInheritCanBePassedIn() {
        String chartKey = createTestChartWithTables();

        TableBookingConfig inherit = TableBookingConfig.inherit();
        Event event = client.events.create(chartKey, new CreateEventParams().withTableBookingConfig(inherit));

        assertThat(event.key()).isNotNull();
        assertThat(event.tableBookingConfig()).isEqualTo(inherit);
    }

    @Test
    public void objectCategoriesCanBePassedIn() {
        String chartKey = createTestChart();
        Map<String, CategoryKey> objectCategories = Map.of("A-1", CategoryKey.of(10L));

        Event event = client.events.create(chartKey, new CreateEventParams().withObjectCategories(objectCategories));

        assertThat(event.objectCategories()).containsOnly(entry("A-1", CategoryKey.of(10L)));
    }

    @Test
    public void categoriesCanBePassedIn() {
        String chartKey = createTestChart();
        Category eventCategory = new Category("eventCategory", "event-level category", "#AAABBB");
        List<Category> categories = List.of(
                eventCategory
        );

        Event event = client.events.create(chartKey, new CreateEventParams().withCategories(categories));

        int numberOfCategoriesOnChart = 3; // see sampleChart.json
        assertThat(event.categories())
                .hasSize(numberOfCategoriesOnChart + categories.size())
                .contains(eventCategory);
    }

    @Test
    public void channelsCanBePassedIn() {
        String chartKey = createTestChart();
        List<Channel> channels = List.of(
                new Channel("channelKey1", "channel 1", "#FFFF99", 1, Set.of("A-1")),
                new Channel("channelKey2", "channel 2", "#FFFF99", 2, Set.of("A-2"))
        );

        Event event = client.events.create(chartKey, new CreateEventParams().withChannels(channels));

        assertThat(event.channels()).containsExactly(
                new Channel("channelKey1", "channel 1", "#FFFF99", 1, Set.of("A-1")),
                new Channel("channelKey2", "channel 2", "#FFFF99", 2, Set.of("A-2"))
        );
    }

    @Test
    public void nameCanBePassedIn() {
        String chartKey = createTestChart();

        Event event = client.events.create(chartKey, new CreateEventParams().withName("My event"));

        assertThat(event.name()).isEqualTo("My event");
    }

    @Test
    public void dateCanBePassedIn() {
        String chartKey = createTestChart();

        Event event = client.events.create(chartKey, new CreateEventParams().withDate(LocalDate.of(2022, 10, 1)));

        assertThat(event.date()).isEqualTo(LocalDate.of(2022, 10, 1));
    }

    @Test
    public void forSaleConfigCanBePassedIn() {
        String chartKey = createTestChart();
        ForSaleConfigParams params = new ForSaleConfigParams(false, List.of("A-1"), Map.of("GA1", 5), List.of("Cat1"));

        Event event = client.events.create(chartKey, new CreateEventParams().withForSaleConfigParams(params));

        ForSaleConfig forSaleConfig = new ForSaleConfig(params.forSale, params.objects, params.areaPlaces, params.categories);
        assertThat(event.forSaleConfig()).isEqualTo(forSaleConfig);
    }
}
