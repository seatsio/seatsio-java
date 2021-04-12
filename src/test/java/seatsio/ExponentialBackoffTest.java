package seatsio;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static seatsio.util.UnirestUtil.get;
import static seatsio.util.UnirestUtil.stringResponse;

public class ExponentialBackoffTest {

    @Test
    public void abortsEventuallyIfServerKeepsReturning429() {
        Instant start = Instant.now();
        try {
            stringResponse(get("https://httpbin.org/status/429", "secretKey"));
            throw new RuntimeException("Should have failed");
        } catch (SeatsioException e) {
            assertThat(e.getMessage()).isEqualTo("GET https://httpbin.org/status/429 resulted in a 429 TOO MANY REQUESTS response.");
            long waitTime = Instant.now().toEpochMilli() - start.toEpochMilli();
            assertThat(waitTime).isGreaterThan(10000);
            assertThat(waitTime).isLessThan(20000);
        }
    }

    @Test
    public void abortsDirectlyIfServerReturnsOtherErrorThan429() {
        Instant start = Instant.now();
        try {
            stringResponse(get("https://httpbin.org/status/400", "secretKey"));
            throw new RuntimeException("Should have failed");
        } catch (SeatsioException e) {
            assertThat(e.getMessage()).isEqualTo("GET https://httpbin.org/status/400 resulted in a 400 BAD REQUEST response.");
            long waitTime = Instant.now().toEpochMilli() - start.toEpochMilli();
            assertThat(waitTime).isLessThan(2000);
        }
    }

    @Test
    public void returnsSuccessfullyWhenTheServerSendsA429FirstAndThenASuccessfulResponse() {
        for (int i = 0; i < 20; ++i) {
            stringResponse(get("https://httpbin.org/status/429:0.25,204:0.75", "secretKey"));
        }
    }
}
