package seatsio.charts;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

public class UpdateChartTest extends SeatsioClientTest {

    @Test
    public void name() {
        List<Category> categories = newArrayList(
                new Category(CategoryKey.of(1L), "Category 1", "#aaaaaa")
        );

        Chart chart = client.charts.create(null, "BOOTHS", categories);

        client.charts.update(chart.key, "aChart");

        Chart retrievedChart = client.charts.retrieve(chart.key);
        assertThat(retrievedChart.name).isEqualTo("aChart");
        Map<?, ?> drawing = client.charts.retrievePublishedVersion(retrievedChart.key);
        assertThat(drawing.get("venueType")).isEqualTo("BOOTHS");
        assertThat(categories(drawing)).containsExactly(
                ImmutableMap.of("key", 1.0, "label", "Category 1", "color", "#aaaaaa", "accessible", false)
        );
    }

    @Test
    public void categories() {
        Chart chart = client.charts.create("aChart", "BOOTHS", null);
        List<Category> categories = newArrayList(
                new Category(CategoryKey.of(1L), "Category 1", "#aaaaaa"),
                new Category(CategoryKey.of("anotherCat"), "Category 2", "#bbbbbb", true)
        );

        client.charts.update(chart.key, null, categories);

        Chart retrievedChart = client.charts.retrieve(chart.key);
        assertThat(retrievedChart.name).isEqualTo("aChart");
        Map<?, ?> drawing = client.charts.retrievePublishedVersion(retrievedChart.key);
        assertThat(drawing.get("venueType")).isEqualTo("BOOTHS");
        assertThat(categories(drawing)).containsExactly(
                ImmutableMap.of("key", 1.0, "label", "Category 1", "color", "#aaaaaa", "accessible", false),
                ImmutableMap.of("key", "anotherCat", "label", "Category 2", "color", "#bbbbbb", "accessible", true)
        );
    }

    private List<Map<?, ?>> categories(Map<?, ?> drawing) {
        Map<?, ?> categoriesMap = (Map<?, ?>) drawing.get("categories");
        return (List<Map<?, ?>>) categoriesMap.get("list");
    }
}
