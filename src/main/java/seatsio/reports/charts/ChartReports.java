package seatsio.reports.charts;

import com.google.gson.reflect.TypeToken;
import seatsio.reports.Reports;

import java.util.List;
import java.util.Map;

public class ChartReports extends Reports {


    public ChartReports(String secretKey, String baseUrl) {
        super(secretKey, baseUrl, "charts");
    }

    public Map<String, List<ChartReportItem>> byLabel(String chartKey) {
        return fetchReport("byLabel", chartKey);
    }

    public Map<String, List<ChartReportItem>> byCategoryKey(String chartKey) {
        return fetchReport("byCategoryKey", chartKey);
    }

    public Map<String, List<ChartReportItem>> byCategoryLabel(String chartKey) {
        return fetchReport("byCategoryLabel", chartKey);
    }

    protected TypeToken<Map<String, List<ChartReportItem>>> getTypeToken() {
        return new TypeToken<Map<String, List<ChartReportItem>>>() {
        };
    }

}
