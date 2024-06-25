package seatsio.reports.charts;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.charts.ChartObjectInfo;
import seatsio.events.IDs;
import seatsio.events.Labels;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static seatsio.reports.charts.ChartReportBookWholeTablesMode.*;
import static seatsio.reports.charts.ChartReportOptions.options;

public abstract class AbstractChartReportsTest extends SeatsioClientTest {

    @Test
    public void reportItemProperties() {
        String chartKey = createTestChartForReport();

        Map<String, List<ChartObjectInfo>> report = client.chartReports.byLabel(chartKey, updateOptions());

        ChartObjectInfo reportItem = report.get("A-1").get(0);
        assertThat(reportItem.label).isEqualTo("A-1");
        assertThat(reportItem.labels).isEqualTo(new Labels("1", "seat", "A", "row"));
        assertThat(reportItem.ids).isEqualTo(new IDs("1", "A", null));
        assertThat(reportItem.categoryLabel).isEqualTo("Cat1");
        assertThat(reportItem.categoryKey).isEqualTo("9");
        assertThat(reportItem.section).isNull();
        assertThat(reportItem.entrance).isNull();
        assertThat(reportItem.capacity).isNull();
        assertThat(reportItem.objectType).isEqualTo("seat");
        assertThat(reportItem.leftNeighbour).isNull();
        assertThat(reportItem.rightNeighbour).isEqualTo("A-2");
        assertThat(reportItem.distanceToFocalPoint).isNotNull();
        assertThat(reportItem.isAccessible).isNotNull();
        assertThat(reportItem.isCompanionSeat).isNotNull();
        assertThat(reportItem.hasRestrictedView).isNotNull();
    }

    @Test
    public void reportItemPropertiesForGA() {
        String chartKey = createTestChartForReport();

        Map<String, List<ChartObjectInfo>> report = client.chartReports.byLabel(chartKey, updateOptions());

        ChartObjectInfo reportItem = report.get("GA1").get(0);
        assertThat(reportItem.capacity).isEqualTo(100);
        assertThat(reportItem.objectType).isEqualTo("generalAdmission");
        assertThat(reportItem.bookAsAWhole).isEqualTo(false);
    }

    @Test
    public void reportItemPropertiesForTable() {
        String chartKey = createTestChartWithTablesForReport();

        Map<String, List<ChartObjectInfo>> report = client.chartReports.byLabel(chartKey, updateOptions(options().bookWholeTablesMode(TRUE)));

        ChartObjectInfo reportItem = report.get("T1").get(0);
        assertThat(reportItem.bookAsAWhole).isEqualTo(false);
        assertThat(reportItem.numSeats).isEqualTo(6);
    }

    @Test
    public void byLabel() {
        String chartKey = createTestChartForReport();

        Map<String, List<ChartObjectInfo>> report = client.chartReports.byLabel(chartKey, updateOptions());

        assertThat(report.get("A-1")).hasSize(1);
        assertThat(report.get("A-2")).hasSize(1);
    }

    @Test
    public void byObjectType() {
        String chartKey = createTestChartForReport();

        Map<String, List<ChartObjectInfo>> report = client.chartReports.byObjectType(chartKey, updateOptions());

        assertThat(report.get("seat")).hasSize(32);
        assertThat(report.get("generalAdmission")).hasSize(2);
    }

    @Test
    public void byLabel_bookWholeTablesNull() {
        String chartKey = createTestChartWithTablesForReport();

        Map<String, List<ChartObjectInfo>> report = client.chartReports.byLabel(chartKey, updateOptions());

        assertThat(report.keySet()).containsExactlyInAnyOrder("T1-1", "T1-2", "T1-3", "T1-4", "T1-5", "T1-6", "T2-1", "T2-2", "T2-3", "T2-4", "T2-5", "T2-6", "T1", "T2");
    }

    @Test
    public void byLabel_bookWholeTablesChart() {
        String chartKey = createTestChartWithTablesForReport();

        Map<String, List<ChartObjectInfo>> report = client.chartReports.byLabel(chartKey, updateOptions(options().bookWholeTablesMode(CHART)));

        assertThat(report.keySet()).containsExactlyInAnyOrder("T1-1", "T1-2", "T1-3", "T1-4", "T1-5", "T1-6", "T2");
    }

    @Test
    public void byLabel_bookWholeTablesTrue() {
        String chartKey = createTestChartWithTablesForReport();

        Map<String, List<ChartObjectInfo>> report = client.chartReports.byLabel(chartKey, updateOptions(options().bookWholeTablesMode(TRUE)));

        assertThat(report.keySet()).containsExactlyInAnyOrder("T1", "T2");
    }

    @Test
    public void byLabel_bookWholeTablesFalse() {
        String chartKey = createTestChartWithTablesForReport();

        Map<String, List<ChartObjectInfo>> report = client.chartReports.byLabel(chartKey, updateOptions(options().bookWholeTablesMode(FALSE)));

        assertThat(report.keySet()).containsExactlyInAnyOrder("T1-1", "T1-2", "T1-3", "T1-4", "T1-5", "T1-6", "T2-1", "T2-2", "T2-3", "T2-4", "T2-5", "T2-6");
    }

    @Test
    public void byCategoryKey() {
        String chartKey = createTestChartForReport();

        Map<String, List<ChartObjectInfo>> report = client.chartReports.byCategoryKey(chartKey, updateOptions());

        assertThat(report.get("9")).hasSize(17);
        assertThat(report.get("10")).hasSize(17);
    }

    @Test
    public void byCategoryLabel() {
        String chartKey = createTestChartForReport();

        Map<String, List<ChartObjectInfo>> report = client.chartReports.byCategoryLabel(chartKey, updateOptions());

        assertThat(report.get("Cat1")).hasSize(17);
        assertThat(report.get("Cat2")).hasSize(17);
    }

    @Test
    public void bySection() {
        String chartKey = createTestChartWithSectionsForReport();

        Map<String, List<ChartObjectInfo>> report = client.chartReports.bySection(chartKey, updateOptions());

        assertThat(report.get("Section A")).hasSize(36);
        assertThat(report.get("Section B")).hasSize(35);
    }

    public abstract String createTestChartForReport();

    public abstract String createTestChartWithSectionsForReport();

    public abstract String createTestChartWithTablesForReport();

    public abstract ChartReportOptions updateOptions();

    public abstract ChartReportOptions updateOptions(ChartReportOptions options);
}
