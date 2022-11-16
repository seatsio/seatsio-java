package seatsio;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import seatsio.util.UnirestWrapper;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static seatsio.util.UnirestWrapper.get;

public class ExponentialBackoffTest {

    @Test
    @Disabled
    public void abortsEventuallyIfServerKeepsReturning429() {
        Instant start = Instant.now();
        try {
            new UnirestWrapper("secretKey", null).stringResponse(get("https://httpbin.org/status/429"));
            throw new RuntimeException("Should have failed");
        } catch (SeatsioException e) {
            // TODO: check for RateLimitExceededException when httpbin supports status 429 with a JSON response body
            long waitTime = Instant.now().toEpochMilli() - start.toEpochMilli();
            assertThat(waitTime).isGreaterThan(10000);
            assertThat(waitTime).isLessThan(20000);
        }
    }

    @Test
    @Disabled
    public void abortsDirectlyIfServerReturnsOtherErrorThan429() {
        Instant start = Instant.now();
        try {
            new UnirestWrapper("secretKey", null).stringResponse(get("https://httpbin.org/status/400"));
            throw new RuntimeException("Should have failed");
        } catch (SeatsioException e) {
            assertThat(e.getMessage()).isEqualTo("GET https://httpbin.org/status/400 resulted in a 400 BAD REQUEST response.");
            long waitTime = Instant.now().toEpochMilli() - start.toEpochMilli();
            assertThat(waitTime).isLessThan(2000);
        }
    }

    @Test
    @Disabled
    public void abortsDirectlyIfMaxRetries0AndServerReturns429() {
        Instant start = Instant.now();
        try {
            new UnirestWrapper("secretKey", null).maxRetries(0).stringResponse(get("https://httpbin.org/status/429"));
            throw new RuntimeException("Should have failed");
        } catch (SeatsioException e) {
            assertThat(e.getMessage()).isEqualTo("GET https://httpbin.org/status/429 resulted in a 429 TOO MANY REQUESTS response.");
            long waitTime = Instant.now().toEpochMilli() - start.toEpochMilli();
            assertThat(waitTime).isLessThan(2000);
        }
    }

    @Test
    @Disabled
    public void returnsSuccessfullyWhenTheServerSendsA429FirstAndThenASuccessfulResponse() {
        for (int i = 0; i < 10; ++i) {
            new UnirestWrapper("secretKey", null).stringResponse(get("https://httpbin.org/status/429:0.25,204:0.75"));
        }
    }
}
