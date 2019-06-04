package seatsio;

import seatsio.accounts.subaccounts.Accounts;
import seatsio.charts.Charts;
import seatsio.events.Events;
import seatsio.holdTokens.HoldTokens;
import seatsio.reports.charts.ChartReports;
import seatsio.reports.events.EventReports;
import seatsio.reports.usage.UsageReports;
import seatsio.subaccounts.Subaccounts;

public class SeatsioClient {

    private final String secretKey;
    private final String baseUrl;

    public final Accounts accounts;
    public final Subaccounts subaccounts;
    public final HoldTokens holdTokens;
    public final Charts charts;
    public final Events events;
    public final EventReports eventReports;
    public final ChartReports chartReports;
    public final UsageReports usageReports;

    public SeatsioClient(String secretKey, String baseUrl) {
        this.secretKey = secretKey;
        this.baseUrl = baseUrl;
        this.accounts = new Accounts(secretKey, baseUrl);
        this.subaccounts = new Subaccounts(secretKey, baseUrl);
        this.holdTokens = new HoldTokens(secretKey, baseUrl);
        this.charts = new Charts(secretKey, baseUrl);
        this.events = new Events(secretKey, baseUrl);
        this.eventReports = new EventReports(secretKey, baseUrl);
        this.chartReports = new ChartReports(secretKey, baseUrl);
        this.usageReports = new UsageReports(secretKey, baseUrl);
    }

    public SeatsioClient(String secretKey) {
        this(secretKey, "https://api.seatsio.net");
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}
