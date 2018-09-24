package seatsio;

import seatsio.accounts.subaccounts.Accounts;
import seatsio.charts.Charts;
import seatsio.eventreports.EventReports;
import seatsio.events.Events;
import seatsio.holdTokens.HoldTokens;
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

    public SeatsioClient(String secretKey, String baseUrl) {
        this.secretKey = secretKey;
        this.baseUrl = baseUrl;
        this.accounts = new Accounts(secretKey, baseUrl);
        this.subaccounts = new Subaccounts(secretKey, baseUrl);
        this.holdTokens = new HoldTokens(secretKey, baseUrl);
        this.charts = new Charts(secretKey, baseUrl);
        this.events = new Events(secretKey, baseUrl);
        this.eventReports = new EventReports(secretKey, baseUrl);
    }

    public SeatsioClient(String secretKey) {
        this(secretKey, "https://api.seatsio.net");
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}
