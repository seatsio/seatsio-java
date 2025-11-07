package seatsio.reports.charts;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.charts.ChartObjectInfo;
import seatsio.events.Floor;
import seatsio.events.IDs;
import seatsio.events.LabelAndType;
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
        assertThat(reportItem.label()).isEqualTo("A-1");
        assertThat(reportItem.labels()).isEqualTo(new Labels(new LabelAndType("1", "seat"), new LabelAndType("A", "row"), null));
        assertThat(reportItem.ids()).isEqualTo(new IDs("1", "A", null));
        assertThat(reportItem.categoryLabel()).isEqualTo("Cat1");
        assertThat(reportItem.categoryKey()).isEqualTo("9");
        assertThat(reportItem.section()).isNull();
        assertThat(reportItem.entrance()).isNull();
        assertThat(reportItem.capacity()).isNull();
        assertThat(reportItem.objectType()).isEqualTo("seat");
        assertThat(reportItem.leftNeighbour()).isNull();
        assertThat(reportItem.rightNeighbour()).isEqualTo("A-2");
        assertThat(reportItem.distanceToFocalPoint()).isNotNull();
        assertThat(reportItem.isAccessible()).isNotNull();
        assertThat(reportItem.isCompanionSeat()).isNotNull();
        assertThat(reportItem.hasRestrictedView()).isNotNull();
        assertThat(reportItem.floor()).isNull();
    }

    @Test
    public void reportItemPropertiesForGA() {
        String chartKey = createTestChartForReport();

        Map<String, List<ChartObjectInfo>> report = client.chartReports.byLabel(chartKey, updateOptions());

        ChartObjectInfo reportItem = report.get("GA1").get(0);
        assertThat(reportItem.capacity()).isEqualTo(100);
        assertThat(reportItem.objectType()).isEqualTo("generalAdmission");
        assertThat(reportItem.bookAsAWhole()).isEqualTo(false);
    }

    @Test
    public void reportItemPropertiesForTable() {
        String chartKey = createTestChartWithTablesForReport();

        Map<String, List<ChartObjectInfo>> report = client.chartReports.byLabel(chartKey, updateOptions(options().bookWholeTablesMode(TRUE)));

        ChartObjectInfo reportItem = report.get("T1").get(0);
        assertThat(reportItem.bookAsAWhole()).isEqualTo(false);
        assertThat(reportItem.numSeats()).isEqualTo(6);
    }

    @Test
    public void byObjectType() {
        String chartKey = createTestChartForReport();

        Map<String, List<ChartObjectInfo>> report = client.chartReports.byObjectType(chartKey, updateOptions());

        assertThat(report.get("seat")).hasSize(32);
        assertThat(report.get("generalAdmission")).hasSize(2);
    }

    @Test
    public void byObjectType_withFloors() {
        String chartKey = createTestChartWithFloorsForReport();

        Map<String, List<ChartObjectInfo>> report = client.chartReports.byObjectType(chartKey, updateOptions());

        List<ChartObjectInfo> seats = report.get("seat");
        assertThat(seats).hasSize(4);
        assertThat(seats.get(0).floor()).isEqualTo(floor("1", "Floor 1"));
        assertThat(seats.get(1).floor()).isEqualTo(floor("1", "Floor 1"));
        assertThat(seats.get(2).floor()).isEqualTo(floor("2", "Floor 2"));
        assertThat(seats.get(3).floor()).isEqualTo(floor("2", "Floor 2"));
    }

    @Test
    public void byLabel() {
        String chartKey = createTestChartForReport();

        Map<String, List<ChartObjectInfo>> report = client.chartReports.byLabel(chartKey, updateOptions());

        assertThat(report.get("A-1")).hasSize(1);
        assertThat(report.get("A-2")).hasSize(1);
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
    public void byLabel_withFloors() {
        String chartKey = createTestChartWithFloorsForReport();

        Map<String, List<ChartObjectInfo>> report = client.chartReports.byLabel(chartKey, updateOptions());

        assertThat(report.get("S1-A-1").get(0).floor()).isEqualTo(floor("1", "Floor 1"));
        assertThat(report.get("S1-A-2").get(0).floor()).isEqualTo(floor("1", "Floor 1"));
        assertThat(report.get("S2-B-1").get(0).floor()).isEqualTo(floor("2", "Floor 2"));
        assertThat(report.get("S2-B-2").get(0).floor()).isEqualTo(floor("2", "Floor 2"));
    }

    @Test
    public void byCategoryKey() {
        String chartKey = createTestChartForReport();

        Map<String, List<ChartObjectInfo>> report = client.chartReports.byCategoryKey(chartKey, updateOptions());

        assertThat(report.get("9")).hasSize(17);
        assertThat(report.get("10")).hasSize(17);
    }

    @Test
    public void byCategoryKey_withFloors() {
        String chartKey = createTestChartWithFloorsForReport();

        Map<String, List<ChartObjectInfo>> report = client.chartReports.byCategoryKey(chartKey, updateOptions());

        List<ChartObjectInfo> fooCategory = report.get("1");
        assertThat(fooCategory).hasSize(2);
        assertThat(fooCategory.get(0).floor()).isEqualTo(floor("1", "Floor 1"));
        assertThat(fooCategory.get(1).floor()).isEqualTo(floor("1", "Floor 1"));

        List<ChartObjectInfo> barCategory = report.get("2");
        assertThat(barCategory).hasSize(2);
        assertThat(barCategory.get(0).floor()).isEqualTo(floor("2", "Floor 2"));
        assertThat(barCategory.get(1).floor()).isEqualTo(floor("2", "Floor 2"));
    }

    @Test
    public void byCategoryLabel() {
        String chartKey = createTestChartForReport();

        Map<String, List<ChartObjectInfo>> report = client.chartReports.byCategoryLabel(chartKey, updateOptions());

        assertThat(report.get("Cat1")).hasSize(17);
        assertThat(report.get("Cat2")).hasSize(17);
    }

    @Test
    public void byCategoryLabel_withFloors() {
        String chartKey = createTestChartWithFloorsForReport();

        Map<String, List<ChartObjectInfo>> report = client.chartReports.byCategoryLabel(chartKey, updateOptions());

        List<ChartObjectInfo> fooCategory = report.get("Foo");
        assertThat(fooCategory).hasSize(2);
        assertThat(fooCategory.get(0).floor()).isEqualTo(floor("1", "Floor 1"));
        assertThat(fooCategory.get(1).floor()).isEqualTo(floor("1", "Floor 1"));

        List<ChartObjectInfo> barCategory = report.get("Bar");
        assertThat(barCategory).hasSize(2);
        assertThat(barCategory.get(0).floor()).isEqualTo(floor("2", "Floor 2"));
        assertThat(barCategory.get(1).floor()).isEqualTo(floor("2", "Floor 2"));
    }

    @Test
    public void bySection() {
        String chartKey = createTestChartWithSectionsForReport();

        Map<String, List<ChartObjectInfo>> report = client.chartReports.bySection(chartKey, updateOptions());

        assertThat(report.get("Section A")).hasSize(36);
        assertThat(report.get("Section B")).hasSize(35);
    }

    @Test
    public void bySection_withFloors() {
        String chartKey = createTestChartWithFloorsForReport();

        Map<String, List<ChartObjectInfo>> report = client.chartReports.bySection(chartKey, updateOptions());

        List<ChartObjectInfo> section1 = report.get("S1");
        assertThat(section1).hasSize(2);
        assertThat(section1.get(0).floor()).isEqualTo(floor("1", "Floor 1"));
        assertThat(section1.get(0).floor()).isEqualTo(floor("1", "Floor 1"));

        List<ChartObjectInfo> section2 = report.get("S2");
        assertThat(section2).hasSize(2);
        assertThat(section2.get(1).floor()).isEqualTo(floor("2", "Floor 2"));
        assertThat(section2.get(1).floor()).isEqualTo(floor("2", "Floor 2"));
    }

    @Test
    public void byZone() {
        String chartKey = createTestChartWithZonesForReport();

        Map<String, List<ChartObjectInfo>> report = client.chartReports.byZone(chartKey, updateOptions());

        assertThat(report.get("midtrack")).hasSize(6032);
        assertThat(report.get("midtrack").get(0).zone()).isEqualTo("midtrack");
        assertThat(report.get("finishline")).hasSize(2865);
    }

    public abstract String createTestChartForReport();

    public abstract String createTestChartWithSectionsForReport();

    public abstract String createTestChartWithZonesForReport();

    public abstract String createTestChartWithTablesForReport();

    public abstract String createTestChartWithFloorsForReport();

    public abstract ChartReportOptions updateOptions();

    public abstract ChartReportOptions updateOptions(ChartReportOptions options);

    private static Floor floor(String name, String displayName){
        return new Floor(name, displayName);
    }
}
