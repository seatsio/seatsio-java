package seatsio.holdTokens;

import org.junit.Test;
import seatsio.SeatsioClientTest;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;

public class UpdateHoldTokenExpirationDateTest extends SeatsioClientTest {

    @Test
    public void test() {
        HoldToken holdToken = client.holdTokens.create();
        Instant now = Instant.now();

        HoldToken updatedHoldToken = client.holdTokens.expireInMinutes(holdToken.holdToken, 30);

        assertThat(updatedHoldToken.holdToken).isEqualTo(holdToken.holdToken);
        assertThat(updatedHoldToken.expiresAt).isBetween(now.plus(29, MINUTES), now.plus(31, MINUTES));
    }

}
