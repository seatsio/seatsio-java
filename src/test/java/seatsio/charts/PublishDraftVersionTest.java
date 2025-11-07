package seatsio.charts;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import static org.assertj.core.api.Assertions.assertThat;

public class PublishDraftVersionTest extends SeatsioClientTest {

    @Test
    public void test() {
        Chart chart = client.charts.create("oldname");
        client.events.create(chart.key());
        client.charts.update(chart.key(), "newname");

        client.charts.publishDraftVersion(chart.key());

        Chart retrievedChart = client.charts.retrieve(chart.key());
        assertThat(retrievedChart.name()).isEqualTo("newname");
        assertThat(retrievedChart.status()).isEqualTo("PUBLISHED");
    }

}
