package seatsio.charts;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateChartTest extends SeatsioClientTest {

    @Test
    public void defaultParameters() {
        Chart chart = client.charts.create();

        assertThat(chart.id).isNotZero();
        assertThat(chart.key).isNotBlank();
        assertThat(chart.status).isEqualTo("NOT_USED");
        assertThat(chart.name).isEqualTo("Untitled chart");
        assertThat(chart.publishedVersionThumbnailUrl).isNotBlank();
        assertThat(chart.draftVersionThumbnailUrl).isNull();
        assertThat(chart.events).isNull();
        assertThat(chart.tags).isEmpty();
        assertThat(chart.archived).isFalse();

        Map<?, ?> drawing = client.charts.retrievePublishedVersion(chart.key);
        assertThat(drawing.get("venueType")).isEqualTo("MIXED");
        assertThat(categories(drawing)).isEmpty();
    }

    @Test
    public void name() {
        Chart chart = client.charts.create("aChart");

        assertThat(chart.name).isEqualTo("aChart");
        Map<?, ?> drawing = client.charts.retrievePublishedVersion(chart.key);
        assertThat(drawing.get("venueType")).isEqualTo("MIXED");
        assertThat(categories(drawing)).isEmpty();
    }

    @Test
    public void venueType() {
        Chart chart = client.charts.create(null, "BOOTHS");

        assertThat(chart.name).isEqualTo("Untitled chart");
        Map<?, ?> drawing = client.charts.retrievePublishedVersion(chart.key);
        assertThat(drawing.get("venueType")).isEqualTo("BOOTHS");
        assertThat(categories(drawing)).isEmpty();
    }

    @Test
    public void categories() {
        List<Category> categories = Lists.newArrayList(
                new Category(CategoryKey.of(1L), "Category 1", "#aaaaaa"),
                new Category(CategoryKey.of("anotherCat"), "Category 2", "#bbbbbb", true)
        );
        Chart chart = client.charts.create(null, null, categories);

        assertThat(chart.name).isEqualTo("Untitled chart");
        Map<?, ?> drawing = client.charts.retrievePublishedVersion(chart.key);
        assertThat(drawing.get("venueType")).isEqualTo("MIXED");
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
