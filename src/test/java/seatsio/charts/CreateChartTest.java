package seatsio.charts;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import java.util.List;

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
    }

    @Test
    public void name() {
        Chart chart = client.charts.create("aChart");
        assertThat(chart.name).isEqualTo("aChart");
    }

    @Test
    public void venueType() {
        Chart chart = client.charts.create(null, "BOOTHS");

        assertThat(chart.name).isEqualTo("Untitled chart");
    }

    @Test
    public void categories() {
        List<Category> categories = Lists.newArrayList(
                new Category(1L, "Category 1", "#aaaaaa"),
                new Category("anotherCat", "Category 2", "#bbbbbb", true)
        );
        Chart chart = client.charts.create(null, null, categories);

        assertThat(chart.name).isEqualTo("Untitled chart");
    }

}
