package seatsio.reports.charts;

public class ChartReportOptions {

    public static final ChartReportOptions NONE = new ChartReportOptions();

    private ChartReportBookWholeTablesMode bookWholeTablesMode;
    private ChartReportVersion version;

    public static ChartReportOptions options() {
        return new ChartReportOptions();
    }

    public ChartReportOptions bookWholeTablesMode(ChartReportBookWholeTablesMode bookWholeTablesMode) {
        this.bookWholeTablesMode = bookWholeTablesMode;
        return this;
    }

    public ChartReportOptions version(ChartReportVersion version) {
        this.version = version;
        return this;
    }

    public ChartReportBookWholeTablesMode bookWholeTablesMode() {
        return bookWholeTablesMode;
    }

    public ChartReportVersion version() {
        return version;
    }
}
