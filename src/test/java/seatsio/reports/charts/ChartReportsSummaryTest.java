package seatsio.reports.charts;

public class ChartReportsSummaryTest extends AbstractChartReportsSummaryTest {

    @Override
    public String createTestChartForReport() {
        return createTestChart();
    }

    @Override
    public String createTestChartWithTablesForReport() {
        return createTestChartWithTables();
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
