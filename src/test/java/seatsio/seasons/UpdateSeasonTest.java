package seatsio.seasons;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.charts.Category;
import seatsio.charts.CategoryKey;
import seatsio.charts.Chart;
import seatsio.events.TableBookingConfig;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static seatsio.events.TableBookingMode.BY_SEAT;
import static seatsio.events.TableBookingMode.BY_TABLE;

public class UpdateSeasonTest extends SeatsioClientTest {

    @Test
    public void canUpdateSeasonName() {
        Chart chart = client.charts.create();
        Season season = client.seasons.create(chart.key(), new CreateSeasonParams().name("Original name"));

        client.seasons.update(season.key(), new UpdateSeasonParams().name("New name"));

        Season retrievedSeason = client.seasons.retrieve(season.key());
        assertThat(retrievedSeason.name()).isEqualTo("New name");
    }

    @Test
    public void updateSeasonEventKey() {
        Chart chart = client.charts.create();
        Season season = client.seasons.create(chart.key(), new CreateSeasonParams().key("someKey"));

        client.seasons.update(season.key(), new UpdateSeasonParams().key("someNewKey"));

        Season retrievedSeason = client.seasons.retrieve("someNewKey");
        assertThat(retrievedSeason.key()).isEqualTo("someNewKey");
        assertThat(retrievedSeason.id()).isEqualTo(season.id());
    }

    @Test
    public void updateTableBookingConfig() {
        String chartKey = createTestChartWithTables();
        Season season = client.seasons.create(chartKey);

        TableBookingConfig newTableBookingConfig = TableBookingConfig.custom(Map.of("T1", BY_TABLE, "T2", BY_SEAT));
        client.seasons.update(season.key(), new UpdateSeasonParams().tableBookingConfig(newTableBookingConfig));

        Season retrievedSeason = client.seasons.retrieve(season.key());
        assertThat(retrievedSeason.tableBookingConfig()).isEqualTo(newTableBookingConfig);
    }

    @Test
    public void updateObjectCategories() {
        String chartKey = createTestChart();
        Map<String, CategoryKey> objectCategories = Map.of(
                "A-1", CategoryKey.of(9L)
        );
        Season season = client.seasons.create(chartKey, new CreateSeasonParams().withObjectCategories(objectCategories));

        Map<String, CategoryKey> newObjectCategories = Map.of(
                "A-2", CategoryKey.of(10L)
        );
        client.seasons.update(season.key(), new UpdateSeasonParams().withObjectCategories(newObjectCategories));

        Season retrievedSeason = client.seasons.retrieve(season.key());
        Assertions.assertThat(retrievedSeason.objectCategories()).isEqualTo(newObjectCategories);
    }

    @Test
    public void updateCategories() {
        String chartKey = createTestChart();
        Season season = client.seasons.create(chartKey);

        Category category = new Category("eventCategory", "event-level category", "#AAABBB");
        List<Category> categories = List.of(
                category
        );

        client.seasons.update(season.key(), new UpdateSeasonParams().withCategories(categories));

        Season retrievedSeason = client.seasons.retrieve(season.key());
        int numberOfCategoriesOnChart = 3; // see sampleChart.json
        Assertions.assertThat(retrievedSeason.categories())
                .hasSize(numberOfCategoriesOnChart + categories.size())
                .contains(category);
    }

    @Test
    public void updateForSalePropagated() {
        String chartKey = createTestChartWithTables();
        Season season = client.seasons.create(chartKey, new CreateSeasonParams().forSalePropagated(false));

        client.seasons.update(season.key(), new UpdateSeasonParams().forSalePropagated(true));

        Season retrievedSeason = client.seasons.retrieve(season.key());
        assertThat(retrievedSeason.forSalePropagated).isTrue();
    }


}
