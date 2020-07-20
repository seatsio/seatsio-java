package seatsio.workspaces;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import static org.assertj.core.api.Assertions.assertThat;

public class SetDefaultWorkspaceTest extends SeatsioClientTest {

    @Test
    public void test() {
        Workspace workspace = client.workspaces.create("my workspace");

        client.workspaces.setDefault(workspace.key);

        Workspace retrievedWorkspace = client.workspaces.retrieve(workspace.key);
        assertThat(retrievedWorkspace.isDefault).isTrue();
    }

}
