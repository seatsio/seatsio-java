package seatsio.charts;

import org.junit.Test;
import seatsio.SeatsioClientTest;

import static org.assertj.core.api.Assertions.assertThat;

public class DiscardDraftVersionTest extends SeatsioClientTest {

    @Test
    public void test() {
        Chart chart = client.charts().create("oldname");
        client.events().create(chart.key);
        client.charts().update(chart.key, "newname");

        client.charts().discardDraftVersion(chart.key);

        Chart retrievedChart = client.charts().retrieve(chart.key);
        assertThat(retrievedChart.name).isEqualTo("oldname");
        assertThat(retrievedChart.status).isEqualTo("PUBLISHED");
    }

}
