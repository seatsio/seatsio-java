package seatsio.holdTokens;

import org.junit.Test;
import seatsio.SeatsioClientTest;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;

public class RetrieveHoldTokenTest extends SeatsioClientTest {

    @Test
    public void test() {
        HoldToken holdToken = client.holdTokens().create();

        HoldToken retrievedHoldToken = client.holdTokens().retrieve(holdToken.holdToken);

        assertThat(retrievedHoldToken.holdToken).isEqualTo(holdToken.holdToken);
        assertThat(retrievedHoldToken.expiresAt).isEqualTo(holdToken.expiresAt);
    }

}
