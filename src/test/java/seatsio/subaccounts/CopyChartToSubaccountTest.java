package seatsio.subaccounts;

import org.junit.Test;
import seatsio.SeatsioClientTest;
import seatsio.charts.Chart;

import static org.assertj.core.api.Assertions.assertThat;

public class CopyChartToSubaccountTest extends SeatsioClientTest {

    @Test
    public void test() {
        Subaccount fromSubaccount = client.subaccounts.create();
        Chart chart = seatsioClient(fromSubaccount.secretKey).charts.create("aChart");
        Subaccount toSubaccount = client.subaccounts.create();

        Chart copiedChart = client.subaccounts.copyChartToSubaccount(fromSubaccount.id, toSubaccount.id, chart.key);

        assertThat(copiedChart.name).isEqualTo("aChart");
        Chart retrievedChart = seatsioClient(toSubaccount.secretKey).charts.retrieve(copiedChart.key);
        assertThat(retrievedChart.name).isEqualTo("aChart");
    }
}
