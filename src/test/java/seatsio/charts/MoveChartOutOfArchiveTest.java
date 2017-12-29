package seatsio.charts;

import org.junit.Test;
import seatsio.SeatsioClientTest;

import static org.assertj.core.api.Assertions.assertThat;

public class MoveChartOutOfArchiveTest extends SeatsioClientTest {

    @Test
    public void test() {
        Chart chart = client.charts().create();
        client.charts().moveToArchive(chart.key);

        client.charts().moveOutOfArchive(chart.key);

        Chart retrievedChart = client.charts().retrieve(chart.key);
        assertThat(retrievedChart.archived).isFalse();
    }

}
