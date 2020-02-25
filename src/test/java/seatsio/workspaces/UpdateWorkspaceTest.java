package seatsio.workspaces;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import static org.assertj.core.api.Assertions.assertThat;

public class UpdateWorkspaceTest extends SeatsioClientTest {

    @Test
    public void updateWorkspace() {
        Workspace workspace = client.workspaces.create("my workspace");

        client.workspaces.update(workspace.key, "my ws");

        Workspace retrievedWorkspace = client.workspaces.retrieve(workspace.key);
        assertThat(retrievedWorkspace.name).isEqualTo("my ws");
        assertThat(retrievedWorkspace.key).isNotNull();
        assertThat(retrievedWorkspace.secretKey).isNotNull();
        assertThat(retrievedWorkspace.isTest).isFalse();
    }

}
