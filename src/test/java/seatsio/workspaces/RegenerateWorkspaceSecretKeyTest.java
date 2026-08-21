package seatsio.workspaces;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.SeatsioException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    public void regenerationIsNotPossibleWhenMultipleKeysArePresent() {
        Workspace workspace = client.workspaces.create("my workspace");
        client.workspaces.addSecretKey(workspace.key());

        SeatsioException ex = assertThrows(SeatsioException.class, () -> client.workspaces.regenerateSecretKey(workspace.key()));
        assertThat(ex.errors.get(0).getCode()).isEqualTo("MULTIPLE_SECRET_KEYS_PRESENT");
    }

}
