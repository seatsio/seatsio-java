package seatsio.workspaces;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import static org.assertj.core.api.Assertions.assertThat;

public class DeactivateWorkspaceTest extends SeatsioClientTest {

    @Test
    public void deactivateWorkspace() {
        Workspace workspace = client.workspaces.create("my workspace");

        client.workspaces.deactivate(workspace.key());

        Workspace retrievedWorkspace = client.workspaces.retrieve(workspace.key());
        assertThat(retrievedWorkspace.isActive()).isFalse();
    }

}
