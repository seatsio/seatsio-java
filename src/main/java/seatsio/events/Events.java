package seatsio.events;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import seatsio.json.JsonObjectBuilder;

import static seatsio.UnirestUtil.unirest;

public class Events {

    private final String secretKey;
    private final String baseUrl;

    public Events(String secretKey, String baseUrl) {
        this.secretKey = secretKey;
        this.baseUrl = baseUrl;
    }

    public Event create(String chartKey) {
        JsonObjectBuilder request = JsonObjectBuilder.aJsonObject();
        request.withProperty("chartKey", chartKey);
        HttpResponse<String> response = unirest(() -> Unirest.post(baseUrl + "/events")
                .basicAuth(secretKey, null)
                .body(request.build().toString())
                .asString());
        return new Gson().fromJson(response.getBody(), Event.class);
    }
}
