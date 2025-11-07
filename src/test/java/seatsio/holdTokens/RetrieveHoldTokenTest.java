package seatsio.holdTokens;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import static org.assertj.core.api.Assertions.assertThat;

public class RetrieveHoldTokenTest extends SeatsioClientTest {

    @Test
    public void test() {
        HoldToken holdToken = client.holdTokens.create();

        HoldToken retrievedHoldToken = client.holdTokens.retrieve(holdToken.holdToken());

        assertThat(retrievedHoldToken.holdToken()).isEqualTo(holdToken.holdToken());
        assertThat(retrievedHoldToken.expiresAt()).isEqualTo(holdToken.expiresAt());
        assertThat(holdToken.expiresInSeconds()).isBetween(14L * 60, 15L * 60);
    }

}
