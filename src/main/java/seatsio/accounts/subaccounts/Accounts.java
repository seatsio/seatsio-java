package seatsio.accounts.subaccounts;

import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import seatsio.charts.Chart;
import seatsio.util.Lister;
import seatsio.util.Page;
import seatsio.util.PageFetcher;

import java.util.stream.Stream;

import static com.mashape.unirest.http.Unirest.get;
import static com.mashape.unirest.http.Unirest.post;
import static seatsio.json.JsonObjectBuilder.aJsonObject;
import static seatsio.json.SeatsioGson.gson;
import static seatsio.util.UnirestUtil.stringResponse;

public class Accounts {

    private final String secretKey;
    private final String baseUrl;

    public Accounts(String secretKey, String baseUrl) {
        this.secretKey = secretKey;
        this.baseUrl = baseUrl;
    }

    public Account retrieveMyAccount() {
        HttpResponse<String> response = stringResponse(get(baseUrl + "/accounts/me")
                .basicAuth(secretKey, null));
        return gson().fromJson(response.getBody(), Account.class);
    }

}
