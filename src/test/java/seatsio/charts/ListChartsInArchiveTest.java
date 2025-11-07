package seatsio.charts;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ListChartsInArchiveTest extends SeatsioClientTest {

    @Test
    public void test() {
        Chart chart1 = client.charts.create();
        client.charts.moveToArchive(chart1.key());

        Chart chart2 = client.charts.create();

        Chart chart3 = client.charts.create();
        client.charts.moveToArchive(chart3.key());

        Stream<Chart> charts = client.charts.archive.all();

        assertThat(charts)
                .extracting(chart -> chart.key())
                .containsExactly(chart3.key(), chart1.key());
    }

}
