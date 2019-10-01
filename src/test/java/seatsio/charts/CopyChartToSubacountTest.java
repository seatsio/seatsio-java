package seatsio.charts;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClient;
import seatsio.SeatsioClientTest;
import seatsio.subaccounts.Subaccount;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class CopyChartToSubacountTest extends SeatsioClientTest {

    @Test
    public void test() {
        Chart chart = client.charts.create("my chart", "BOOTHS");
        Subaccount subaccount = client.subaccounts.create();

        Chart copiedChart = client.charts.copyToSubacccount(chart.key, subaccount.id);

        SeatsioClient subaccountClient = new SeatsioClient(subaccount.secretKey, BASE_URL);
        assertThat(copiedChart.name).isEqualTo("my chart");
        Chart retrievedChart = subaccountClient.charts.retrieve(copiedChart.key);
        assertThat(retrievedChart.name).isEqualTo("my chart");
        Map<?, ?> drawing = subaccountClient.charts.retrievePublishedVersion(copiedChart.key);
        assertThat(drawing.get("venueType")).isEqualTo("BOOTHS");
    }

}
