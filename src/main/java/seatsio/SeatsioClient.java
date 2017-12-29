package seatsio;

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
}
