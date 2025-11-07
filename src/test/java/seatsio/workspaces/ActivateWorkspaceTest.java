package seatsio.workspaces;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import static org.assertj.core.api.Assertions.assertThat;

public class ActivateWorkspaceTest extends SeatsioClientTest {

    @Test
    public void activateWorkspace() {
        Workspace workspace = client.workspaces.create("my workspace");
        client.workspaces.deactivate(workspace.key());

        client.workspaces.activate(workspace.key());

        Workspace retrievedWorkspace = client.workspaces.retrieve(workspace.key());
        assertThat(retrievedWorkspace.isActive()).isTrue();
    }

}
