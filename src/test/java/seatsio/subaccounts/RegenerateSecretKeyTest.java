package seatsio.subaccounts;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import static org.assertj.core.api.Assertions.assertThat;

public class RegenerateSecretKeyTest extends SeatsioClientTest {

    @Test
    public void test() {
        Subaccount subaccount = client.subaccounts.create();

        client.subaccounts.regenerateSecretKey(subaccount.id);

        Subaccount retrievedSubaccount = client.subaccounts.retrieve(subaccount.id);
        assertThat(retrievedSubaccount.secretKey)
                .isNotBlank()
                .isNotEqualTo(subaccount.secretKey);
    }

}
