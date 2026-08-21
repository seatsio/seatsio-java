package seatsio.workspaces;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import static org.assertj.core.api.Assertions.assertThat;

public class AddSecretKeyTest extends SeatsioClientTest {

    @Test
    public void addSecretKey() {
        Workspace workspace = client.workspaces.create("my workspace");
        String originalSecretKey = workspace.secretKey();

        String newSecretKey = client.workspaces.addSecretKey(workspace.key());
        Workspace updatedWorkspace = client.workspaces.retrieve(workspace.key());

        assertThat(updatedWorkspace.secretKeys()).hasSize(2);
        assertThat(updatedWorkspace.secretKeys()).containsExactlyInAnyOrder(originalSecretKey, newSecretKey);
    }
}
