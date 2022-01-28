package seatsio.reports.charts;

import com.google.common.collect.ImmutableMap;
import com.google.gson.reflect.TypeToken;
import seatsio.charts.ChartObjectInfo;
import seatsio.reports.Reports;
import seatsio.util.UnirestWrapper;

import java.util.List;
import java.util.Map;

public class ChartReports extends Reports {

    public ChartReports(String baseUrl, UnirestWrapper unirest) {
        super(baseUrl, "charts", unirest);
    }

    public Map<String, List<ChartObjectInfo>> byLabel(String chartKey, ChartReportBookWholeTablesMode bookWholeTablesMode) {
        return fetchChartReport("byLabel", chartKey, bookWholeTablesMode);
    }

    public Map<String, List<ChartObjectInfo>> byObjectType(String chartKey, ChartReportBookWholeTablesMode bookWholeTablesMode) {
        return fetchChartReport("byObjectType", chartKey, bookWholeTablesMode);
    }

    public Map<String, ChartReportSummaryItem> summaryByObjectType(String eventKey) {
        return fetchSummaryReport("byObjectType", eventKey);
    }

    public Map<String, List<ChartObjectInfo>> byCategoryKey(String chartKey, ChartReportBookWholeTablesMode bookWholeTablesMode) {
        return fetchChartReport("byCategoryKey", chartKey, bookWholeTablesMode);
    }

    public Map<String, ChartReportSummaryItem> summaryByCategoryKey(String eventKey) {
        return fetchSummaryReport("byCategoryKey", eventKey);
    }

    public Map<String, List<ChartObjectInfo>> byCategoryLabel(String chartKey, ChartReportBookWholeTablesMode bookWholeTablesMode) {
        return fetchChartReport("byCategoryLabel", chartKey, bookWholeTablesMode);
    }

    public Map<String, ChartReportSummaryItem> summaryByCategoryLabel(String eventKey) {
        return fetchSummaryReport("byCategoryLabel", eventKey);
    }

    public Map<String, List<ChartObjectInfo>> bySection(String chartKey, ChartReportBookWholeTablesMode bookWholeTablesMode) {
        return fetchChartReport("bySection", chartKey, bookWholeTablesMode);
    }

    public Map<String, ChartReportSummaryItem> summaryBySection(String eventKey) {
        return fetchSummaryReport("bySection", eventKey);
    }

    private Map<String, List<ChartObjectInfo>> fetchChartReport(String reportType, String chartKey, ChartReportBookWholeTablesMode bookWholeTablesMode) {
        Map<String, Object> queryParams = bookWholeTablesMode == null ? null : ImmutableMap.of("bookWholeTables", bookWholeTablesMode.queryParam());
        return fetchReport(reportType, chartKey, queryParams);
    }

    protected TypeToken<Map<String, List<ChartObjectInfo>>> getTypeToken() {
        return new TypeToken<Map<String, List<ChartObjectInfo>>>() {
        };
    }

    @Override
    protected TypeToken<Map<String, ChartReportSummaryItem>> getSummaryTypeToken() {
        return new TypeToken<Map<String, ChartReportSummaryItem>>() {
        };
    }
}
