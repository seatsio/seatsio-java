package seatsio.accounts.subaccounts;

import com.mashape.unirest.http.HttpResponse;
import seatsio.util.UnirestUtil;

import static seatsio.json.SeatsioGson.gson;
import static seatsio.util.UnirestUtil.stringResponse;

public class Accounts {

    private final String secretKey;
    private final String workspaceKey;
    private final String baseUrl;

    public Accounts(String secretKey, String workspaceKey, String baseUrl) {
        this.secretKey = secretKey;
        this.workspaceKey = workspaceKey;
        this.baseUrl = baseUrl;
    }

    public Account retrieveMyAccount() {
        HttpResponse<String> response = stringResponse(UnirestUtil.get(baseUrl + "/accounts/me", secretKey, workspaceKey));
        return gson().fromJson(response.getBody(), Account.class);
    }

}
