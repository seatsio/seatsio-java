package seatsio;

import mockwebserver3.MockResponse;

public class MockResponses {

    public static MockResponse json(int code, String body) {
        return new MockResponse.Builder().code(code).setHeader("Content-Type", "application/json").body(body).build();
    }
}
