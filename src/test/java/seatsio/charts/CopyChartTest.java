package seatsio.charts;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class CopyChartTest extends SeatsioClientTest {

    @Test
    public void test() {
        Chart chart = client.charts.create("my chart", "BOOTHS");

        Chart copiedChart = client.charts.copy(chart.key);

        assertThat(copiedChart.name).isEqualTo("my chart (copy)");
        Map<?, ?> drawing = client.charts.retrievePublishedVersion(copiedChart.key);
        assertThat(drawing.get("venueType")).isEqualTo("BOOTHS");
    }

}
