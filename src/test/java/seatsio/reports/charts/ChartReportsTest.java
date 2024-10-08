package seatsio.reports.charts;

public class ChartReportsTest extends AbstractChartReportsTest {

    @Override
    public String createTestChartForReport() {
        return createTestChart();
    }

    @Override
    public String createTestChartWithSectionsForReport() {
        return createTestChartWithSections();
    }

    @Override
    public String createTestChartWithZonesForReport() {
        return createTestChartWithZones();
    }

    @Override
    public String createTestChartWithTablesForReport() {
        return createTestChartWithTables();
    }

    @Override
    public String createTestChartWithFloorsForReport() {
        return createTestChartWithFloors();
    }

    @Override
    public ChartReportOptions updateOptions() {
        return ChartReportOptions.NONE;
    }

    @Override
    public ChartReportOptions updateOptions(final ChartReportOptions options) {
        return options;
    }
}
