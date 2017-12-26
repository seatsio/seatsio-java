package seatsio;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.Before;

import java.util.Random;

import static seatsio.json.JsonObjectBuilder.aJsonObject;

public class SeatsioClientTest {

    private static final String BASE_URL = "https://api-staging.seats.io";

    protected TestUser user;
    protected SeatsioClient client;

    @Before
    public void setup() throws UnirestException {
        user = createTestUser();
        client = new SeatsioClient(user.secretKey, BASE_URL);
    }

    private TestUser createTestUser() throws UnirestException {
        JsonObject request = aJsonObject()
                .withProperty("email", "test" + new Random().nextLong() + "@seats.io")
                .withProperty("password", "12345678")
                .build();
        HttpResponse<String> response = Unirest.post(BASE_URL + "/system/public/users")
                .body(request.toString())
                .asString();

        return new Gson().fromJson(response.getBody(), TestUser.class);
    }
}
