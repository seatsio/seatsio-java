package seatsio.charts;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.SeatsioException;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("unchecked")
public class ManageCategoriesTest extends SeatsioClientTest {

    @Test
    public void addCategory() {
        Chart chart = client.charts.create("aChart", "BOOTHS", null);

        client.charts.addCategory(chart.key,  new Category(CategoryKey.of(1L), "Category 1", "#aaaaaa", true));

        Chart retrievedChart = client.charts.retrieve(chart.key);
        Map<?, ?> drawing = client.charts.retrievePublishedVersion(retrievedChart.key);
        assertThat(categories(drawing)).containsExactly(
                ImmutableMap.of(
                        "key", 1.0,
                        "label", "Category 1",
                        "color", "#aaaaaa",
                        "accessible", true
                )
        );
    }

    @Test
    public void removeCategory() {
        List<Category> categories = newArrayList(
                new Category(CategoryKey.of(1L), "Category 1", "#aaaaaa"),
                new Category(CategoryKey.of("cat2"), "Category 2", "#bbbbbb", true)
        );
        Chart chart = client.charts.create("aChart", "BOOTHS", categories);

        client.charts.removeCategory(chart.key, CategoryKey.of(1L));

        Chart retrievedChart = client.charts.retrieve(chart.key);
        Map<?, ?> drawing = client.charts.retrievePublishedVersion(retrievedChart.key);
        assertThat(categories(drawing)).containsExactly(
                ImmutableMap.of(
                        "key", "cat2",
                        "label", "Category 2",
                        "color", "#bbbbbb",
                        "accessible", true
                )
        );
    }

    @Test
    public void listCategories() {
        List<Category> categories = newArrayList(
                new Category(CategoryKey.of(1L), "Category 1", "#aaaaaa"),
                new Category(CategoryKey.of("cat2"), "Category 2", "#bbbbbb", true)
        );
        Chart chart = client.charts.create("aChart", "BOOTHS", categories);

        final List<Category> categoryList = client.charts.listCategories(chart.key);
        assertThat(categoryList).isEqualTo(categories);
    }

    @Test
    public void listCategories_unknownChart() {
        assertThrows(SeatsioException.class, () -> client.charts.listCategories("unknownChart"));
    }

    private List<Map<?, ?>> categories(Map<?, ?> drawing) {
        Map<?, ?> categoriesMap = (Map<?, ?>) drawing.get("categories");
        return (List<Map<?, ?>>) categoriesMap.get("list");
    }

}
