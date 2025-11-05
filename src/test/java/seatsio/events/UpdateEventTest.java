package seatsio.events;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.charts.Category;
import seatsio.charts.CategoryKey;
import seatsio.charts.Chart;
import seatsio.seasons.CreateSeasonParams;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static seatsio.events.TableBookingMode.BY_SEAT;
import static seatsio.events.TableBookingMode.BY_TABLE;

public class UpdateEventTest extends SeatsioClientTest {

    @Test
    public void updateEventKey() {
        Chart chart = client.charts.create();
        Event event = client.events.create(chart.key);

        client.events.update(event.key, new UpdateEventParams().withKey("newKey"));

        Event retrievedEvent = client.events.retrieve("newKey");
        assertThat(retrievedEvent.key).isEqualTo("newKey");
        assertThat(retrievedEvent.id).isEqualTo(event.id);
    }

    @Test
    public void updateTableBookingConfig() {
        String chartKey = createTestChartWithTables();
        Event event = client.events.create(chartKey);

        TableBookingConfig newTableBookingConfig = TableBookingConfig.custom(Map.of("T1", BY_TABLE, "T2", BY_SEAT));
        client.events.update(event.key, new UpdateEventParams().withTableBookingConfig(newTableBookingConfig));

        Event retrievedEvent = client.events.retrieve(event.key);
        assertThat(retrievedEvent.tableBookingConfig).isEqualTo(newTableBookingConfig);
    }

    @Test
    public void updateObjectCategories() {
        String chartKey = createTestChart();
        Map<String, CategoryKey> objectCategories = Map.of(
                "A-1", CategoryKey.of(9L)
        );
        Event event = client.events.create(chartKey, new CreateEventParams().withObjectCategories(objectCategories));

        Map<String, CategoryKey> newObjectCategories = Map.of(
                "A-2", CategoryKey.of(10L)
        );
        client.events.update(event.key, new UpdateEventParams().withObjectCategories(newObjectCategories));

        Event retrievedEvent = client.events.retrieve(event.key);
        assertThat(retrievedEvent.objectCategories).isEqualTo(newObjectCategories);
    }

    @Test
    public void removeObjectCategories() {
        String chartKey = createTestChart();
        Map<String, CategoryKey> objectCategories = Map.of(
                "A-1", CategoryKey.of(9L)
        );
        Event event = client.events.create(chartKey, new CreateEventParams().withObjectCategories(objectCategories));

        client.events.removeObjectCategories(event.key);

        Event retrievedEvent = client.events.retrieve(event.key);
        assertThat(retrievedEvent.objectCategories).isNull();
    }

    @Test
    public void updateCategories() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        Category eventCategory = new Category("eventCategory", "event-level category", "#AAABBB");
        List<Category> categories = List.of(
                eventCategory
        );

        client.events.update(event.key, new UpdateEventParams().withCategories(categories));

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
        List<Category> categories = List.of(eventCategory);

        Event event = client.events.create(chartKey, new CreateEventParams().withCategories(categories));

        client.events.removeCategories(event.key);

        Event retrievedEvent = client.events.retrieve(event.key);
        int numberOfCategoriesOnChart = 3; // see sampleChart.json
        assertThat(retrievedEvent.categories)
                .hasSize(numberOfCategoriesOnChart)
                .doesNotContain(eventCategory);
    }

    @Test
    public void updateName() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey, new CreateEventParams().withName("My event"));

        client.events.update(event.key, new UpdateEventParams().withName("Another event"));

        Event retrievedEvent = client.events.retrieve(event.key);
        assertThat(retrievedEvent.name).isEqualTo("Another event");
    }

    @Test
    public void updateDate() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey, new CreateEventParams().withDate(LocalDate.of(2022, 1, 5)));

        client.events.update(event.key, new UpdateEventParams().withDate(LocalDate.of(2022, 1, 6)));

        Event retrievedEvent = client.events.retrieve(event.key);
        assertThat(retrievedEvent.date).isEqualTo(LocalDate.of(2022, 1, 6));
    }

    @Test
    public void updateIsInThePast() {
        String chartKey = createTestChart();
        client.seasons.create(chartKey, new CreateSeasonParams().eventKeys(List.of("event1")));
        Event event = client.events.retrieve("event1");
        assertThat(event.isInThePast).isFalse();

        client.events.update("event1", new UpdateEventParams().withIsInThePast(true));

        Event retrievedEvent = client.events.retrieve(event.key);
        assertThat(retrievedEvent.isInThePast).isTrue();
    }
}
