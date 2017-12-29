package seatsio.charts;

import org.junit.Test;
import seatsio.SeatsioClientTest;

import static org.assertj.core.api.Assertions.assertThat;

public class RetrieveChartTest extends SeatsioClientTest {

    @Test
    public void test() {
        Chart chart = client.charts().create();
        client.charts().addTag(chart.key, "tag1");
        client.charts().addTag(chart.key, "tag2");

        Chart retrievedChart = client.charts().retrieve(chart.key);

        assertThat(retrievedChart.id).isNotZero();
        assertThat(retrievedChart.key).isNotBlank();
        assertThat(retrievedChart.status).isEqualTo("NOT_USED");
        assertThat(retrievedChart.name).isEqualTo("Untitled chart");
        assertThat(retrievedChart.publishedVersionThumbnailUrl).isNotBlank();
        assertThat(chart.draftVersionThumbnailUrl).isNull();
        assertThat(retrievedChart.events).isNull();
        assertThat(retrievedChart.tags).containsOnly("tag1", "tag2");
        assertThat(retrievedChart.archived).isFalse();
    }
}
