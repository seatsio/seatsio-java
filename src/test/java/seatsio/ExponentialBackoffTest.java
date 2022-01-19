package seatsio;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.Test;
import seatsio.exceptions.RateLimitExceededException;
import seatsio.util.UnirestWrapper;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static seatsio.util.UnirestWrapper.get;

// ignore until mockbin is working again
@Ignore
public class ExponentialBackoffTest {

    @Test
    public void abortsEventuallyIfServerKeepsReturning429() {
        Instant start = Instant.now();
        try {
            new UnirestWrapper("secretKey", null).stringResponse(get("https://mockbin.org/bin/0381d6f4-0155-4b8c-937b-73d3d88b2a3f"));
            throw new RuntimeException("Should have failed");
        } catch (RateLimitExceededException e) {
            long waitTime = Instant.now().toEpochMilli() - start.toEpochMilli();
            assertThat(waitTime).isGreaterThan(10000);
            assertThat(waitTime).isLessThan(20000);
        }
    }

    @Test
    public void abortsDirectlyIfServerReturnsOtherErrorThan429() {
        Instant start = Instant.now();
        try {
            new UnirestWrapper("secretKey", null).stringResponse(get("https://mockbin.org/bin/1eea3aab-2bb2-4f92-99c2-50d942fb6294"));
            throw new RuntimeException("Should have failed");
        } catch (SeatsioException e) {
            assertThat(e.getMessage()).isEqualTo("GET https://mockbin.org/bin/1eea3aab-2bb2-4f92-99c2-50d942fb6294 resulted in a 400 Bad Request response.");
            long waitTime = Instant.now().toEpochMilli() - start.toEpochMilli();
            assertThat(waitTime).isLessThan(2000);
        }
    }

    @Test
    public void abortsDirectlyIfMaxRetries0AndServerReturns429() {
        Instant start = Instant.now();
        try {
            new UnirestWrapper("secretKey", null).maxRetries(0).stringResponse(get("https://mockbin.org/bin/0381d6f4-0155-4b8c-937b-73d3d88b2a3f"));
            throw new RuntimeException("Should have failed");
        } catch (SeatsioException e) {
            assertThat(e.getMessage()).isEqualTo("GET https://mockbin.org/bin/0381d6f4-0155-4b8c-937b-73d3d88b2a3f resulted in a 429 Too Many Requests response. Reason: Rate limit exceeded. Request ID: 123456");
            long waitTime = Instant.now().toEpochMilli() - start.toEpochMilli();
            assertThat(waitTime).isLessThan(2000);
        }
    }

    @Test
    public void returnsSuccessfullyWhenTheServerSendsA429FirstAndThenASuccessfulResponse() {
        for (int i = 0; i < 10; ++i) {
            new UnirestWrapper("secretKey", null).stringResponse(get("https://httpbin.org/status/429:0.25,204:0.75"));
        }
    }
}
