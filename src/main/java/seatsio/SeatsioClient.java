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

    public SeatsioClient(String secretKey, String workspaceKey, String baseUrl) {
        this.baseUrl = baseUrl;
        this.accounts = new Accounts(secretKey, workspaceKey, baseUrl);
        this.subaccounts = new Subaccounts(secretKey, workspaceKey, baseUrl);
        this.holdTokens = new HoldTokens(secretKey, workspaceKey, baseUrl);
        this.charts = new Charts(secretKey, workspaceKey, baseUrl);
        this.events = new Events(secretKey, workspaceKey, baseUrl);
        this.eventReports = new EventReports(secretKey, workspaceKey, baseUrl);
        this.chartReports = new ChartReports(secretKey, workspaceKey, baseUrl);
        this.usageReports = new UsageReports(secretKey, workspaceKey, baseUrl);
    }

    public SeatsioClient(String secretKey, String workspaceKey) {
        this(secretKey, workspaceKey, "https://api.seatsio.net");
    }

    public SeatsioClient(String secretKey) {
        this(secretKey, null);
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}
