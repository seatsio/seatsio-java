package seatsio.charts;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

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

    private List<Map<?, ?>> categories(Map<?, ?> drawing) {
        Map<?, ?> categoriesMap = (Map<?, ?>) drawing.get("categories");
        return (List<Map<?, ?>>) categoriesMap.get("list");
    }

}
