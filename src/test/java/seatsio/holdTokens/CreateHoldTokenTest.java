package seatsio.holdTokens;

import org.junit.Test;
import seatsio.SeatsioClientTest;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;

public class CreateHoldTokenTest extends SeatsioClientTest {

    @Test
    public void test() {
        Instant creationTime = Instant.now();

        HoldToken holdToken = client.holdTokens().create();

        assertThat(holdToken.holdToken).isNotEmpty();
        assertThat(holdToken.expiresAt).isBetween(creationTime.plus(15, MINUTES), creationTime.plus(16, MINUTES));
    }

}
