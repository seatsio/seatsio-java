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

    private final String baseUrl;

    public final Accounts accounts;
    public final Subaccounts subaccounts;
    public final HoldTokens holdTokens;
    public final Charts charts;
    public final Events events;
    public final EventReports eventReports;
    public final ChartReports chartReports;
    public final UsageReports usageReports;

    public SeatsioClient(String secretKey, Long accountId, String baseUrl) {
        this.baseUrl = baseUrl;
        this.accounts = new Accounts(secretKey, accountId, baseUrl);
        this.subaccounts = new Subaccounts(secretKey, accountId, baseUrl);
        this.holdTokens = new HoldTokens(secretKey, accountId, baseUrl);
        this.charts = new Charts(secretKey, accountId, baseUrl);
        this.events = new Events(secretKey, accountId, baseUrl);
        this.eventReports = new EventReports(secretKey, accountId, baseUrl);
        this.chartReports = new ChartReports(secretKey, accountId, baseUrl);
        this.usageReports = new UsageReports(secretKey, accountId, baseUrl);
    }

    public SeatsioClient(String secretKey, String baseUrl) {
        this(secretKey, null, baseUrl);
    }

    public SeatsioClient(String secretKey, Long accountId) {
        this(secretKey, "https://api.seatsio.net");
    }

    public SeatsioClient(String secretKey) {
        this(secretKey, (Long) null);
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}
