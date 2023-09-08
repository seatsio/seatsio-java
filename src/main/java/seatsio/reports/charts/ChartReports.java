package seatsio.reports.charts;

import com.google.gson.reflect.TypeToken;
import seatsio.charts.ChartObjectInfo;
import seatsio.reports.Reports;
import seatsio.util.UnirestWrapper;

import java.util.*;

import static seatsio.reports.charts.ChartReportOptions.options;

public class ChartReports extends Reports {

    public ChartReports(String baseUrl, UnirestWrapper unirest) {
        super(baseUrl, "charts", unirest);
    }

    /**
     * @deprecated Prefer {@link ChartReports#byLabel(String, ChartReportOptions)} instead
     */
    @Deprecated(since = "81.1.0", forRemoval = true)
    public Map<String, List<ChartObjectInfo>> byLabel(String chartKey, ChartReportBookWholeTablesMode bookWholeTablesMode) {
        return fetchChartReport("byLabel", chartKey, options().bookWholeTablesMode(bookWholeTablesMode));
    }

    public Map<String, List<ChartObjectInfo>> byLabel(String chartKey) {
        return byLabel(chartKey, ChartReportOptions.NONE);
    }

    public Map<String, List<ChartObjectInfo>> byLabel(String chartKey, ChartReportOptions options) {
        return fetchChartReport("byLabel", chartKey, options);
    }

    /**
     * @deprecated Prefer {@link ChartReports#byObjectType(String, ChartReportOptions)} instead
     */
    @Deprecated(since = "81.1.0", forRemoval = true)
    public Map<String, List<ChartObjectInfo>> byObjectType(String chartKey, ChartReportBookWholeTablesMode bookWholeTablesMode) {
        return fetchChartReport("byObjectType", chartKey, options().bookWholeTablesMode(bookWholeTablesMode));
    }

    public Map<String, List<ChartObjectInfo>> byObjectType(String chartKey) {
        return byObjectType(chartKey, ChartReportOptions.NONE);
    }

    public Map<String, List<ChartObjectInfo>> byObjectType(String chartKey, ChartReportOptions options) {
        return fetchChartReport("byObjectType", chartKey, options);
    }

    /**
     * @deprecated Prefer {@link ChartReports#summaryByObjectType(String, ChartReportOptions)} instead
     */
    @Deprecated(since = "81.1.0", forRemoval = true)
    public Map<String, ChartReportSummaryItem> summaryByObjectType(String eventKey, ChartReportBookWholeTablesMode bookWholeTablesMode) {
        return fetchSummaryReport("byObjectType", eventKey, toQueryParams(bookWholeTablesMode, null));
    }

    public Map<String, ChartReportSummaryItem> summaryByObjectType(String eventKey) {
        return summaryByObjectType(eventKey, ChartReportOptions.NONE);
    }

    public Map<String, ChartReportSummaryItem> summaryByObjectType(String eventKey, ChartReportOptions options) {
        return fetchSummaryReport("byObjectType", eventKey, toQueryParams(options.bookWholeTablesMode(), options.version()));
    }

    /**
     * @deprecated Prefer {@link ChartReports#byCategoryKey(String, ChartReportOptions)} instead
     */
    @Deprecated(since = "81.1.0", forRemoval = true)
    public Map<String, List<ChartObjectInfo>> byCategoryKey(String chartKey, ChartReportBookWholeTablesMode bookWholeTablesMode) {
        return fetchChartReport("byCategoryKey", chartKey, options().bookWholeTablesMode(bookWholeTablesMode));
    }

    public Map<String, List<ChartObjectInfo>> byCategoryKey(String chartKey) {
        return byCategoryKey(chartKey, ChartReportOptions.NONE);
    }

    public Map<String, List<ChartObjectInfo>> byCategoryKey(String chartKey, ChartReportOptions options) {
        return fetchChartReport("byCategoryKey", chartKey, options);
    }

    /**
     * @deprecated Prefer {@link ChartReports#summaryByCategoryKey(String, ChartReportOptions)} instead
     */
    @Deprecated(since = "81.1.0", forRemoval = true)
    public Map<String, ChartReportSummaryItem> summaryByCategoryKey(String eventKey, ChartReportBookWholeTablesMode bookWholeTablesMode) {
        return fetchSummaryReport("byCategoryKey", eventKey, toQueryParams(bookWholeTablesMode, null));
    }

    public Map<String, ChartReportSummaryItem> summaryByCategoryKey(String eventKey) {
        return summaryByCategoryKey(eventKey, ChartReportOptions.NONE);
    }

    public Map<String, ChartReportSummaryItem> summaryByCategoryKey(String eventKey, ChartReportOptions options) {
        return fetchSummaryReport("byCategoryKey", eventKey, toQueryParams(options.bookWholeTablesMode(), options.version()));
    }

    /**
     * @deprecated Prefer {@link ChartReports#byCategoryLabel(String, ChartReportOptions)} instead
     */
    @Deprecated(since = "81.1.0", forRemoval = true)
    public Map<String, List<ChartObjectInfo>> byCategoryLabel(String chartKey, ChartReportBookWholeTablesMode bookWholeTablesMode) {
        return fetchChartReport("byCategoryLabel", chartKey, options().bookWholeTablesMode(bookWholeTablesMode));
    }

    public Map<String, List<ChartObjectInfo>> byCategoryLabel(String chartKey) {
        return byCategoryLabel(chartKey, ChartReportOptions.NONE);
    }

    public Map<String, List<ChartObjectInfo>> byCategoryLabel(String chartKey, ChartReportOptions options) {
        return fetchChartReport("byCategoryLabel", chartKey, options);
    }

    /**
     * @deprecated Prefer {@link ChartReports#summaryByCategoryLabel(String, ChartReportOptions)} instead
     */
    @Deprecated(since = "81.1.0", forRemoval = true)
    public Map<String, ChartReportSummaryItem> summaryByCategoryLabel(String eventKey, ChartReportBookWholeTablesMode bookWholeTablesMode) {
        return fetchSummaryReport("byCategoryLabel", eventKey, toQueryParams(bookWholeTablesMode, null));
    }

    public Map<String, ChartReportSummaryItem> summaryByCategoryLabel(String eventKey) {
        return summaryByCategoryLabel(eventKey, ChartReportOptions.NONE);
    }

    public Map<String, ChartReportSummaryItem> summaryByCategoryLabel(String eventKey, ChartReportOptions options) {
        return fetchSummaryReport("byCategoryLabel", eventKey, toQueryParams(options.bookWholeTablesMode(), options.version()));
    }

    /**
     * @deprecated Prefer {@link ChartReports#bySection(String, ChartReportOptions)} instead
     */
    @Deprecated(since = "81.1.0", forRemoval = true)
    public Map<String, List<ChartObjectInfo>> bySection(String chartKey, ChartReportBookWholeTablesMode bookWholeTablesMode) {
        return fetchChartReport("bySection", chartKey, options().bookWholeTablesMode(bookWholeTablesMode));
    }

    public Map<String, List<ChartObjectInfo>> bySection(String chartKey) {
        return bySection(chartKey, ChartReportOptions.NONE);
    }

    public Map<String, List<ChartObjectInfo>> bySection(String chartKey, ChartReportOptions options) {
        return fetchChartReport("bySection", chartKey, options);
    }

    /**
     * @deprecated Prefer {@link ChartReports#summaryBySection(String, ChartReportOptions)} instead
     */
    @Deprecated(since = "81.1.0", forRemoval = true)
    public Map<String, ChartReportSummaryItem> summaryBySection(String eventKey, ChartReportBookWholeTablesMode bookWholeTablesMode) {
        return fetchSummaryReport("bySection", eventKey, toQueryParams(bookWholeTablesMode, null));
    }

    public Map<String, ChartReportSummaryItem> summaryBySection(String eventKey) {
        return summaryBySection(eventKey, ChartReportOptions.NONE);
    }

    public Map<String, ChartReportSummaryItem> summaryBySection(String eventKey, ChartReportOptions options) {
        return fetchSummaryReport("bySection", eventKey, toQueryParams(options.bookWholeTablesMode(), options.version()));
    }

    private Map<String, List<ChartObjectInfo>> fetchChartReport(String reportType, String chartKey, ChartReportOptions options) {
        Map<String, Object> queryParams = toQueryParams(options.bookWholeTablesMode(), options.version());
        return fetchReport(reportType, chartKey, queryParams);
    }

    private Map<String, Object> toQueryParams(ChartReportBookWholeTablesMode bookWholeTablesMode,
                                              ChartReportVersion version) {
        final Map<String, Object> params = new HashMap<>(2);
        if (bookWholeTablesMode != null) {
            params.put("bookWholeTables", bookWholeTablesMode.queryParam());
        }
        if (version != null) {
            params.put("version", version.queryParam());
        }
        return params.isEmpty() ? null : params;
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
