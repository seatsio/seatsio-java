package seatsio.workspaces;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import static org.assertj.core.api.Assertions.assertThat;

public class RemoveSecretKeyTest extends SeatsioClientTest {

    @Test
    public void removeSecretKey() {
        Workspace workspace = client.workspaces.create("my workspace");
        String originalSecretKey = workspace.secretKey();

        String newSecretKey = client.workspaces.addSecretKey(workspace.key());
        client.workspaces.removeSecretKey(workspace.key(), originalSecretKey);
        Workspace updatedWorkspace = client.workspaces.retrieve(workspace.key());

        assertThat(updatedWorkspace.secretKeys()).hasSize(1);
        assertThat(updatedWorkspace.secretKeys()).containsExactly(newSecretKey);
    }
}
