package seatsio;

import org.junit.jupiter.api.Test;
import seatsio.holdTokens.HoldToken;
import seatsio.subaccounts.Subaccount;

import static org.assertj.core.api.Assertions.assertThat;

public class WorkspaceKeyAuthenticationTest extends SeatsioClientTest {

    @Test
    public void clientTakesOptionalWorkspaceKey() {
        Subaccount subaccount = client.subaccounts.create();

        SeatsioClient subaccountClient = new SeatsioClient(user.secretKey, subaccount.publicKey, STAGING_BASE_URL);
        HoldToken holdToken = subaccountClient.holdTokens.create();

        assertThat(holdToken.workspaceKey).isEqualTo(subaccount.publicKey);
    }

}
