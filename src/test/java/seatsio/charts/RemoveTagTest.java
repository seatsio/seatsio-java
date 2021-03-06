package seatsio.charts;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import static org.assertj.core.api.Assertions.assertThat;

public class RemoveTagTest extends SeatsioClientTest {

    @Test
    public void test() {
        Chart chart = client.charts.create();
        client.charts.addTag(chart.key, "tag1");
        client.charts.addTag(chart.key, "tag2");

        client.charts.removeTag(chart.key, "tag2");

        Chart retrievedChart = client.charts.retrieve(chart.key);
        assertThat(retrievedChart.tags).containsOnly("tag1");
    }

}
