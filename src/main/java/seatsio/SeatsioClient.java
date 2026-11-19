package seatsio;

import seatsio.charts.Charts;
import seatsio.eventLog.EventLog;
import seatsio.events.Events;
import seatsio.holdTokens.HoldTokens;
import seatsio.reports.charts.ChartReports;
import seatsio.reports.events.EventReports;
import seatsio.reports.usage.UsageReports;
import seatsio.seasons.Seasons;
import seatsio.ticketBuyers.TicketBuyers;
import seatsio.util.UnirestWrapper;
import seatsio.workspaces.Workspaces;

public class SeatsioClient {

    public static final String IDEMPOTENCY_KEY_HEADER = "Idempotency-Key";

    private final String baseUrl;
    private final UnirestWrapper unirest;

    public final Workspaces workspaces;
    public final HoldTokens holdTokens;
    public final Charts charts;
    public final Events events;
    public final Seasons seasons;
    public final EventReports eventReports;
    public final ChartReports chartReports;
    public final UsageReports usageReports;
    public final EventLog eventLog;
    public final TicketBuyers ticketBuyers;

    public SeatsioClient(String secretKey, String workspaceKey, String baseUrl) {
        this(baseUrl, new UnirestWrapper(secretKey, workspaceKey));
    }

    private SeatsioClient(String baseUrl, UnirestWrapper unirest) {
        this.baseUrl = baseUrl;
        this.unirest = unirest;
        this.workspaces = new Workspaces(baseUrl, unirest);
        this.holdTokens = new HoldTokens(baseUrl, unirest);
        this.charts = new Charts(baseUrl, unirest);
        this.events = new Events(baseUrl, unirest);
        this.seasons = new Seasons(baseUrl, unirest, this);
        this.eventReports = new EventReports(baseUrl, unirest);
        this.chartReports = new ChartReports(baseUrl, unirest);
        this.usageReports = new UsageReports(baseUrl, unirest);
        this.eventLog = new EventLog(baseUrl, unirest);
        this.ticketBuyers = new TicketBuyers(baseUrl, unirest);
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

    public SeatsioClient retryNonIdempotentRequests(boolean retryNonIdempotentRequests) {
        unirest.retryNonIdempotentRequests(retryNonIdempotentRequests);
        return this;
    }

    /**
     * Returns a client that sends the given Idempotency-Key with every POST, PUT and DELETE request.
     * Use it to retry an operation whose outcome is unknown: take the key from SeatsioException.idempotencyKey and
     * make the same call again on the returned client. Use the returned client for that one operation only.
     *
     * @throws IllegalArgumentException if the key is not a v4 UUID
     */
    public SeatsioClient withIdempotencyKey(String idempotencyKey) {
        return new SeatsioClient(baseUrl, unirest.withIdempotencyKey(idempotencyKey));
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void reinitializeHttpConnectionPool() {
        this.unirest.reinitializeHttpConnectionPool();
    }
}
