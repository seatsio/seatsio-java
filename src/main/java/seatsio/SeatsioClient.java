package seatsio;

import seatsio.charts.Charts;
import seatsio.events.Events;
import seatsio.holdTokens.HoldTokens;
import seatsio.reports.charts.ChartReports;
import seatsio.reports.events.EventReports;
import seatsio.reports.usage.UsageReports;
import seatsio.subaccounts.Subaccounts;
import seatsio.workspaces.Workspaces;

public class SeatsioClient {

    private final String baseUrl;

    public final Subaccounts subaccounts;
    public final Workspaces workspaces;
    public final HoldTokens holdTokens;
    public final Charts charts;
    public final Events events;
    public final EventReports eventReports;
    public final ChartReports chartReports;
    public final UsageReports usageReports;

    public SeatsioClient(String secretKey, String workspaceKey, String baseUrl) {
        this.baseUrl = baseUrl;
        this.subaccounts = new Subaccounts(secretKey, workspaceKey, baseUrl);
        this.workspaces = new Workspaces(secretKey, baseUrl);
        this.holdTokens = new HoldTokens(secretKey, workspaceKey, baseUrl);
        this.charts = new Charts(secretKey, workspaceKey, baseUrl);
        this.events = new Events(secretKey, workspaceKey, baseUrl);
        this.eventReports = new EventReports(secretKey, workspaceKey, baseUrl);
        this.chartReports = new ChartReports(secretKey, workspaceKey, baseUrl);
        this.usageReports = new UsageReports(secretKey, workspaceKey, baseUrl);
    }

    public SeatsioClient(Region region, String secretKey, String workspaceKey) {
        this(secretKey, workspaceKey, region.getUrl());
    }

    public SeatsioClient(Region region, String secretKey) {
        this(region, secretKey, null);
    }

    public String getBaseUrl() {
        return baseUrl;
    }

}
