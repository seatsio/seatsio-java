package seatsio;

import seatsio.charts.Charts;
import seatsio.events.Events;
import seatsio.holdTokens.HoldTokens;
import seatsio.reports.charts.ChartReports;
import seatsio.reports.events.EventReports;
import seatsio.reports.usage.UsageReports;
import seatsio.seasons.Seasons;
import seatsio.subaccounts.Subaccounts;
import seatsio.util.UnirestWrapper;
import seatsio.workspaces.Workspaces;

public class SeatsioClient {

    private final String baseUrl;
    private final UnirestWrapper unirest;

    public final Subaccounts subaccounts;
    public final Workspaces workspaces;
    public final HoldTokens holdTokens;
    public final Charts charts;
    public final Events events;
    public final Seasons seasons;
    public final EventReports eventReports;
    public final ChartReports chartReports;
    public final UsageReports usageReports;


    public SeatsioClient(String secretKey, String workspaceKey, String baseUrl) {
        this.baseUrl = baseUrl;
        this.unirest = new UnirestWrapper(secretKey, workspaceKey);
        this.subaccounts = new Subaccounts(baseUrl, unirest);
        this.workspaces = new Workspaces(baseUrl, unirest);
        this.holdTokens = new HoldTokens(baseUrl, unirest);
        this.charts = new Charts(baseUrl, unirest);
        this.events = new Events(baseUrl, unirest);
        this.seasons = new Seasons(baseUrl, unirest, this);
        this.eventReports = new EventReports(baseUrl, unirest);
        this.chartReports = new ChartReports(baseUrl, unirest);
        this.usageReports = new UsageReports(baseUrl, unirest);
    }

    public SeatsioClient(Region region, String secretKey, String workspaceKey) {
        this(secretKey, workspaceKey, region.getUrl());
    }

    public SeatsioClient(Region region, String secretKey) {
        this(region, secretKey, null);
    }

    public SeatsioClient maxRetries(int maxRetries) {
        unirest.maxRetries(maxRetries);
        return this;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

}
