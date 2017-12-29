package seatsio;

import seatsio.charts.Charts;
import seatsio.events.Events;
import seatsio.holdTokens.HoldTokens;
import seatsio.subaccounts.Subaccounts;

public class SeatsioClient {

    private final String secretKey;
    private final String baseUrl;

    public SeatsioClient(String secretKey, String baseUrl) {
        this.secretKey = secretKey;
        this.baseUrl = baseUrl;
    }

    public SeatsioClient(String secretKey) {
        this(secretKey, "https://api.seats.io");
    }

    public Subaccounts subaccounts() {
        return new Subaccounts(secretKey, baseUrl);
    }

    public HoldTokens holdTokens() {
        return new HoldTokens(secretKey, baseUrl);
    }

    public Charts charts() {
        return new Charts(secretKey, baseUrl);
    }

    public Events events() {
        return new Events(secretKey, baseUrl);
    }
}
