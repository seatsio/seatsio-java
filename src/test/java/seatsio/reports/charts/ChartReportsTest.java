package seatsio.reports.charts;

import org.junit.Test;
import seatsio.SeatsioClientTest;
import seatsio.events.Labels;
import seatsio.reports.events.EventReportItem;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ChartReportsTest extends SeatsioClientTest {

    @Test
    public void reportItemProperties() {
        String chartKey = createTestChart();

        Map<String, List<ChartReportItem>> report = client.chartReports.byLabel(chartKey);

        ChartReportItem reportItem = report.get("A-1").get(0);
        assertThat(reportItem.label).isEqualTo("A-1");
        assertThat(reportItem.labels).isEqualTo(new Labels("1", "seat", "A", "row"));
        assertThat(reportItem.categoryLabel).isEqualTo("Cat1");
        assertThat(reportItem.categoryKey).isEqualTo(9);
        assertThat(reportItem.section).isNull();
        assertThat(reportItem.entrance).isNull();
        assertThat(reportItem.capacity).isNull();
        assertThat(reportItem.objectType).isEqualTo("seat");
    }

    @Test
    public void reportItemPropertiesForGA() {
        String chartKey = createTestChart();

        Map<String, List<ChartReportItem>> report = client.chartReports.byLabel(chartKey);

        ChartReportItem reportItem = report.get("GA1").get(0);
        assertThat(reportItem.capacity).isEqualTo(100);
        assertThat(reportItem.objectType).isEqualTo("generalAdmission");
    }


    @Test
    public void byLabel() {
        String chartKey = createTestChart();

        Map<String, List<ChartReportItem>> report = client.chartReports.byLabel(chartKey);

        assertThat(report.get("A-1")).hasSize(1);
        assertThat(report.get("A-2")).hasSize(1);
    }

}
