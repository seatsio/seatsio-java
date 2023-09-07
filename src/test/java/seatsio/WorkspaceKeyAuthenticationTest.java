package seatsio;

import org.junit.jupiter.api.Test;
import seatsio.holdTokens.HoldToken;
import seatsio.workspaces.Workspace;

import static org.assertj.core.api.Assertions.assertThat;

public class WorkspaceKeyAuthenticationTest extends SeatsioClientTest {

    @Test
    public void clientTakesOptionalWorkspaceKey() {
        Workspace workspace = client.workspaces.create("some workspace");

        SeatsioClient subaccountClient = new SeatsioClient(user.secretKey, workspace.key, apiUrl());
        HoldToken holdToken = subaccountClient.holdTokens.create();

        assertThat(holdToken.workspaceKey).isEqualTo(workspace.key);
    }

}
