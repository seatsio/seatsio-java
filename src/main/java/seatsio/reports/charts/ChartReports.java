package seatsio.reports.charts;

import com.google.common.collect.ImmutableMap;
import com.google.gson.reflect.TypeToken;
import seatsio.reports.Reports;

import java.util.List;
import java.util.Map;

public class ChartReports extends Reports {


    public ChartReports(String secretKey, String workspaceKey, String baseUrl) {
        super(secretKey, workspaceKey, baseUrl, "charts");
    }

    public Map<String, List<ChartReportItem>> byLabel(String chartKey, ChartReportBookWholeTablesMode bookWholeTablesMode) {
        return fetchChartReport("byLabel", chartKey, bookWholeTablesMode);
    }

    public Map<String, List<ChartReportItem>> byCategoryKey(String chartKey, ChartReportBookWholeTablesMode bookWholeTablesMode) {
        return fetchChartReport("byCategoryKey", chartKey, bookWholeTablesMode);
    }

    public Map<String, List<ChartReportItem>> byCategoryLabel(String chartKey, ChartReportBookWholeTablesMode bookWholeTablesMode) {
        return fetchChartReport("byCategoryLabel", chartKey, bookWholeTablesMode);
    }

    private Map<String, List<ChartReportItem>> fetchChartReport(String reportType, String chartKey, ChartReportBookWholeTablesMode bookWholeTablesMode) {
        Map<String, Object> queryParams = bookWholeTablesMode == null ? null : ImmutableMap.of("bookWholeTables", bookWholeTablesMode.queryParam());
        return fetchReport(reportType, chartKey, queryParams);
    }


    protected TypeToken<Map<String, List<ChartReportItem>>> getTypeToken() {
        return new TypeToken<Map<String, List<ChartReportItem>>>() {
        };
    }

}
