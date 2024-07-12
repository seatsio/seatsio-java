package seatsio.reports.charts;

import static seatsio.reports.charts.ChartReportOptions.options;

public class ChartDraftReportsSummaryTest extends AbstractChartReportsSummaryTest {

    @Override
    public String createTestChartForReport() {
        final String chartKey = createTestChart();
        createDraft(chartKey);
        return chartKey;
    }

    @Override
    public String createTestChartWithTablesForReport() {
        final String chartKey = createTestChartWithTables();
        createDraft(chartKey);
        return chartKey;
    }

    @Override
    public String createTestChartWithZonesForReport() {
        final String chartKey = createTestChartWithZones();
        createDraft(chartKey);
        return chartKey;
    }

    private void createDraft(String chartKey) {
        client.events.create(chartKey);
        client.charts.update(chartKey, "foo");
    }

    @Override
    public ChartReportOptions updateOptions() {
        return updateOptions(options());
    }

    @Override
    public ChartReportOptions updateOptions(final ChartReportOptions options) {
        return options.version(ChartReportVersion.DRAFT);
    }
}
