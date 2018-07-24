package seatsio;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.io.IOUtils;
import org.junit.Before;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import static com.mashape.unirest.http.Unirest.post;
import static java.util.UUID.randomUUID;
import static seatsio.json.JsonObjectBuilder.aJsonObject;
import static seatsio.util.UnirestUtil.stringResponse;

public class SeatsioClientTest {

    protected static final String BASE_URL = "https://api-staging.seatsio.net";

    protected TestUser user;
    protected SeatsioClient client;

    @Before
    public void setup() throws UnirestException {
        user = createTestUser();
        client = seatsioClient(user.secretKey);
    }

    protected SeatsioClient seatsioClient(String secretKey) {
        return new SeatsioClient(secretKey, BASE_URL);
    }

    private TestUser createTestUser() throws UnirestException {
        JsonObject request = aJsonObject()
                .withProperty("email", randomEmail())
                .withProperty("password", "12345678")
                .build();
        HttpResponse<String> response = post(BASE_URL + "/system/public/users")
                .body(request.toString())
                .asString();

        return new Gson().fromJson(response.getBody(), TestUser.class);
    }

    protected String randomEmail() {
        return "test" + new Random().nextLong() + "@seats.io";
    }

    protected String createTestChart() {
        return createTestChart("/sampleChart.json");
    }

    protected String createTestChartWithSections() {
        return createTestChart("/sampleChartWithSections.json");
    }

    protected String createTestChart(String fileName) {
        String testChartJson = testChartJson(fileName);
        String chartKey = randomUUID().toString();
        stringResponse(post(BASE_URL + "/system/public/" + user.designerKey + "/charts/" + chartKey)
                .body(testChartJson));
        return chartKey;
    }

    private String testChartJson(String fileName) {
        try {
            InputStream testChartJson = SeatsioClientTest.class.getResourceAsStream(fileName);
            return IOUtils.toString(testChartJson, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
