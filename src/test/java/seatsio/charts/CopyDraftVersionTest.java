package seatsio.charts;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class CopyDraftVersionTest extends SeatsioClientTest {

    @Test
    public void test() {
        Chart chart = client.charts.create("oldname");
        client.events.create(chart.key);
        client.charts.update(chart.key, "newname");

        Chart copiedChart = client.charts.copyDraftVersion(chart.key);

        assertThat(copiedChart.name).isEqualTo("newname (copy)");
    }

}
