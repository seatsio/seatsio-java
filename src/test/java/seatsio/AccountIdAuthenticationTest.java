package seatsio;

import org.junit.Test;
import seatsio.holdTokens.HoldToken;
import seatsio.subaccounts.Subaccount;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountIdAuthenticationTest extends SeatsioClientTest {

    @Test
    public void clientTakesOptionalAccountId() {
        Subaccount subaccount = client.subaccounts.create();

        SeatsioClient subaccountClient = new SeatsioClient(user.secretKey, subaccount.accountId, STAGING_BASE_URL);
        HoldToken holdToken = subaccountClient.holdTokens.create();

        assertThat(holdToken.accountId).isEqualTo(subaccount.accountId);
    }

}
