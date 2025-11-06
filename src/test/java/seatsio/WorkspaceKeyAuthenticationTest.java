package seatsio;

import org.junit.jupiter.api.Test;
import seatsio.holdTokens.HoldToken;
import seatsio.workspaces.Workspace;

import static org.assertj.core.api.Assertions.assertThat;

public class WorkspaceKeyAuthenticationTest extends SeatsioClientTest {

    @Test
    public void clientTakesOptionalWorkspaceKey() {
        Workspace workspace = client.workspaces.create("some workspace");

        SeatsioClient workspaceClient = new SeatsioClient(user.secretKey, workspace.key(), apiUrl());
        HoldToken holdToken = workspaceClient.holdTokens.create();

        assertThat(holdToken.workspaceKey()).isEqualTo(workspace.key());
    }

}
