package seatsio.subaccounts;

import org.junit.Test;
import seatsio.SeatsioClientTest;
import seatsio.charts.Chart;

import static org.assertj.core.api.Assertions.assertThat;

public class CopyChartToParentTest extends SeatsioClientTest {

    @Test
    public void test() {
        Subaccount subaccount = client.subaccounts.create();
        Chart chart = seatsioClient(subaccount.secretKey).charts.create("aChart");

        Chart copiedChart = client.subaccounts.copyChartToParent(subaccount.id, chart.key);

        assertThat(copiedChart.name).isEqualTo("aChart");
        Chart retrievedChart = client.charts.retrieve(copiedChart.key);
        assertThat(retrievedChart.name).isEqualTo("aChart");
    }
}
