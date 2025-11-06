package seatsio.holdTokens;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;

public class CreateHoldTokenTest extends SeatsioClientTest {

    @Test
    public void test() {
        Instant creationTime = Instant.now();

        HoldToken holdToken = client.holdTokens.create();

        assertThat(holdToken.holdToken()).isNotEmpty();
        Instant expiration = creationTime.plus(15, MINUTES);
        assertThat(holdToken.expiresAt()).isBetween(expiration.minus(1, MINUTES), expiration.plus(1, MINUTES));
        assertThat(holdToken.expiresInSeconds()).isBetween(14L * 60, 15L * 60);
    }

    @Test
    public void expiresInMinutes() {
        Instant creationTime = Instant.now();

        HoldToken holdToken = client.holdTokens.create(5);

        assertThat(holdToken.holdToken()).isNotEmpty();
        assertThat(holdToken.expiresAt()).isBetween(creationTime.plus(4, MINUTES), creationTime.plus(6, MINUTES));
        assertThat(holdToken.expiresInSeconds()).isBetween(4L * 60, 5L * 60);
    }

}
