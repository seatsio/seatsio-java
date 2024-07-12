package seatsio.reports.charts;

import static seatsio.reports.charts.ChartReportOptions.options;

public class ChartDraftReportsTest extends AbstractChartReportsTest {

    @Override
    public String createTestChartForReport() {
        String chartKey = createTestChart();
        createDraft(chartKey);
        return chartKey;
    }

    @Override
    public String createTestChartWithSectionsForReport() {
        String chartKey = createTestChartWithSections();
        createDraft(chartKey);
        return chartKey;
    }

    @Override
    public String createTestChartWithZonesForReport() {
        String chartKey = createTestChartWithZones();
        createDraft(chartKey);
        return chartKey;
    }

    @Override
    public String createTestChartWithTablesForReport() {
        String chartKey = createTestChartWithTables();
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
    public ChartReportOptions updateOptions(ChartReportOptions options) {
        return options.version(ChartReportVersion.DRAFT);
    }
}
