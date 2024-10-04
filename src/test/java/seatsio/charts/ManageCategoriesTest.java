package seatsio.charts;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.SeatsioException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("unchecked")
public class ManageCategoriesTest extends SeatsioClientTest {

    @Test
    public void addCategory() {
        Chart chart = client.charts.create("aChart", "SIMPLE", null);

        client.charts.addCategory(chart.key,  new Category(CategoryKey.of(1L), "Category 1", "#aaaaaa", true));

        Chart retrievedChart = client.charts.retrieve(chart.key);
        Map<?, ?> drawing = client.charts.retrievePublishedVersion(retrievedChart.key);
        assertThat(categories(drawing)).containsExactly(
                Map.of(
                        "key", 1.0,
                        "label", "Category 1",
                        "color", "#aaaaaa",
                        "accessible", true
                )
        );
    }

    @Test
    public void removeCategory() {
        List<Category> categories = List.of(
                new Category(CategoryKey.of(1L), "Category 1", "#aaaaaa"),
                new Category(CategoryKey.of("cat2"), "Category 2", "#bbbbbb", true)
        );
        Chart chart = client.charts.create("aChart", "SIMPLE", categories);

        client.charts.removeCategory(chart.key, CategoryKey.of(1L));

        Chart retrievedChart = client.charts.retrieve(chart.key);
        Map<?, ?> drawing = client.charts.retrievePublishedVersion(retrievedChart.key);
        assertThat(categories(drawing)).containsExactly(
                Map.of(
                        "key", "cat2",
                        "label", "Category 2",
                        "color", "#bbbbbb",
                        "accessible", true
                )
        );
    }

    @Test
    public void listCategories() {
        List<Category> categories = List.of(
                new Category(CategoryKey.of(1L), "Category 1", "#aaaaaa"),
                new Category(CategoryKey.of("cat2"), "Category 2", "#bbbbbb", true)
        );
        Chart chart = client.charts.create("aChart", "SIMPLE", categories);

        final List<Category> categoryList = client.charts.listCategories(chart.key);
        assertThat(categoryList).isEqualTo(categories);
    }

    @Test
    public void listCategories_unknownChart() {
        assertThrows(SeatsioException.class, () -> client.charts.listCategories("unknownChart"));
    }

    @Test
    public void updateCategory() {
        List<Category> categories = List.of(
                new Category(CategoryKey.of(1L), "Category 1", "#aaaaaa"),
                new Category(CategoryKey.of("cat2"), "Category 2", "#bbbbbb", true)
        );
        Chart chart = client.charts.create("aChart", "SIMPLE", categories);

        client.charts.updateCategory(chart.key, CategoryKey.of("cat2"),
                new CategoryUpdateParams("New label", "#cccccc", false));

        Chart retrievedChart = client.charts.retrieve(chart.key);
        Map<?, ?> drawing = client.charts.retrievePublishedVersion(retrievedChart.key);
        assertThat(categories(drawing)).containsExactly(
                Map.of(
                        "key", 1.0,
                        "label", "Category 1",
                        "color", "#aaaaaa",
                        "accessible", false
                ),
                Map.of(
                        "key", "cat2",
                        "label", "New label",
                        "color", "#cccccc",
                        "accessible", false
                )
        );
    }

    @Test
    public void updateCategory_doNotChangeAnything() {
        List<Category> categories = List.of(
                new Category(CategoryKey.of(1L), "Category 1", "#aaaaaa"),
                new Category(CategoryKey.of("cat2"), "Category 2", "#bbbbbb", true)
        );
        Chart chart = client.charts.create("aChart", "SIMPLE", categories);

        client.charts.updateCategory(chart.key, CategoryKey.of("cat2"),
                new CategoryUpdateParams(null, null, null));

        Chart retrievedChart = client.charts.retrieve(chart.key);
        Map<?, ?> drawing = client.charts.retrievePublishedVersion(retrievedChart.key);
        assertThat(categories(drawing)).containsExactly(
                Map.of(
                        "key", 1.0,
                        "label", "Category 1",
                        "color", "#aaaaaa",
                        "accessible", false
                ),
                Map.of(
                        "key", "cat2",
                        "label", "Category 2",
                        "color", "#bbbbbb",
                        "accessible", true
                )
        );
    }

    @Test
    public void updateCategory_unknownChart() {
        SeatsioException e = assertThrows(SeatsioException.class, () -> client.charts.updateCategory("unknownChart", CategoryKey.of("cat2"),
                new CategoryUpdateParams("New label", "#cccccc", false)));
        assertThat(e.getMessage()).startsWith("Chart not found: unknownChart was not found in workspace");
    }

    @Test
    public void updateCategory_unknownCategory() {
        List<Category> categories = List.of(
                new Category(CategoryKey.of(1L), "Category 1", "#aaaaaa"),
                new Category(CategoryKey.of("cat2"), "Category 2", "#bbbbbb", true)
        );
        Chart chart = client.charts.create("aChart", "SIMPLE", categories);

        SeatsioException e = assertThrows(SeatsioException.class, () -> client.charts.updateCategory(chart.key, CategoryKey.of(3L),
                new CategoryUpdateParams("New label", "#cccccc", false)));
        assertThat(e.getMessage()).isEqualTo("category not found");
    }

    @Test
    public void nullAccessibleShouldBeInterpretedAsFalse() {
        String chartKey = createTestChartWithNullCategoryAccessible();

        List<Category> categories = client.charts.listCategories(chartKey);

        List<Category> expected = List.of(
                new Category(CategoryKey.of(9L), "Cat1", "#87A9CD", false),
                new Category(CategoryKey.of(10L), "Cat2", "#5E42ED", true),
                new Category(CategoryKey.of("string11"), "Cat3", "#5E42BB", false)
        );
        assertThat(categories).isEqualTo(expected);
    }

    private List<Map<?, ?>> categories(Map<?, ?> drawing) {
        Map<?, ?> categoriesMap = (Map<?, ?>) drawing.get("categories");
        return (List<Map<?, ?>>) categoriesMap.get("list");
    }
}
