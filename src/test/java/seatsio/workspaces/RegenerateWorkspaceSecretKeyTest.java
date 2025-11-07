package seatsio.workspaces;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import static org.assertj.core.api.Assertions.assertThat;

public class RegenerateWorkspaceSecretKeyTest extends SeatsioClientTest {

    @Test
    public void updateWorkspace() {
        Workspace workspace = client.workspaces.create("my workspace");

        String newSecretKey = client.workspaces.regenerateSecretKey(workspace.key());

        Workspace retrievedWorkspace = client.workspaces.retrieve(workspace.key());
        assertThat(retrievedWorkspace.secretKey()).isNotEqualTo(workspace.secretKey());
        assertThat(retrievedWorkspace.secretKey())
                .isNotNull()
                .isEqualTo(newSecretKey);
    }

}
