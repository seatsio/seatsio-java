package seatsio.reports.charts;

public enum ChartReportVersion {
    PUBLISHED("published"),
    DRAFT("draft");

    private final String queryParam;

    ChartReportVersion(final String queryParam) {
        this.queryParam = queryParam;
    }

    public String queryParam() {
        return queryParam;
    }
}
