package seatsio;

import org.junit.jupiter.api.Test;
import seatsio.util.UnirestWrapper;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static seatsio.util.UnirestWrapper.get;

public class ExponentialBackoffTest {

    @Test
    public void abortsEventuallyIfServerKeepsReturning429() {
        Instant start = Instant.now();
        try {
            new UnirestWrapper("secretKey", null).stringResponse(get("https://httpbin.seatsio.net/status/429"));
            throw new RuntimeException("Should have failed");
        } catch (SeatsioException e) {
            // TODO: check for RateLimitExceededException when httpbin supports status 429 with a JSON response body
            long waitTime = Instant.now().toEpochMilli() - start.toEpochMilli();
            assertThat(waitTime).isGreaterThan(10000);
            assertThat(waitTime).isLessThan(20000);
        }
    }

    @Test
    public void abortsDirectlyIfServerReturnsOtherErrorThan429() {
        Instant start = Instant.now();
        try {
            new UnirestWrapper("secretKey", null).stringResponse(get("https://httpbin.seatsio.net/status/400"));
            throw new RuntimeException("Should have failed");
        } catch (SeatsioException e) {
            assertThat(e.getMessage()).isEqualTo("GET https://httpbin.seatsio.net/status/400 resulted in a 400 Bad Request response. Body: ");
            long waitTime = Instant.now().toEpochMilli() - start.toEpochMilli();
            assertThat(waitTime).isLessThan(2000);
        }
    }

    @Test
    public void abortsDirectlyIfMaxRetries0AndServerReturns429() {
        Instant start = Instant.now();
        try {
            new UnirestWrapper("secretKey", null).maxRetries(0).stringResponse(get("https://httpbin.seatsio.net/status/429"));
            throw new RuntimeException("Should have failed");
        } catch (SeatsioException e) {
            assertThat(e.getMessage()).isEqualTo("GET https://httpbin.seatsio.net/status/429 resulted in a 429 Too Many Requests response. Body: ");
            long waitTime = Instant.now().toEpochMilli() - start.toEpochMilli();
            assertThat(waitTime).isLessThan(2000);
        }
    }

    @Test
    public void returnsSuccessfullyWhenTheServerSendsA429FirstAndThenASuccessfulResponse() {
        for (int i = 0; i < 10; ++i) {
            new UnirestWrapper("secretKey", null)
                    .maxRetries(20)
                    .stringResponse(get("https://httpbin.seatsio.net/status/429:0.25,204:0.75"));
        }
    }
}
