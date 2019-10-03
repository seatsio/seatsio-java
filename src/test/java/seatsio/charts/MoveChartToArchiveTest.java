package seatsio.charts;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import static org.assertj.core.api.Assertions.assertThat;

public class MoveChartToArchiveTest extends SeatsioClientTest {

    @Test
    public void test() {
        Chart chart = client.charts.create();

        client.charts.moveToArchive(chart.key);

        Chart retrievedChart = client.charts.retrieve(chart.key);
        assertThat(retrievedChart.archived).isTrue();
    }

}
