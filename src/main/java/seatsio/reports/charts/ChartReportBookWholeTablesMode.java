package seatsio.reports.charts;

public enum ChartReportBookWholeTablesMode {

    CHART("chart"), TRUE("true"), FALSE("false");

    private String queryParam;

    ChartReportBookWholeTablesMode(String queryParam) {
        this.queryParam = queryParam;
    }

    public String queryParam() {
        return queryParam;
    }
}
