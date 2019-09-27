package seatsio.accounts.subaccounts;

import com.mashape.unirest.http.HttpResponse;
import seatsio.util.UnirestUtil;

import static seatsio.json.SeatsioGson.gson;
import static seatsio.util.UnirestUtil.stringResponse;

public class Accounts {

    private final String secretKey;
    private final Long accountId;
    private final String baseUrl;

    public Accounts(String secretKey, Long accountId, String baseUrl) {
        this.secretKey = secretKey;
        this.accountId = accountId;
        this.baseUrl = baseUrl;
    }

    public Account retrieveMyAccount() {
        HttpResponse<String> response = stringResponse(UnirestUtil.get(baseUrl + "/accounts/me", secretKey, accountId));
        return gson().fromJson(response.getBody(), Account.class);
    }

}
