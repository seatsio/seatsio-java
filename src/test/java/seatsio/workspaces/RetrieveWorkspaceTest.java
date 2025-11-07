package seatsio.workspaces;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import static org.assertj.core.api.Assertions.assertThat;

public class RetrieveWorkspaceTest extends SeatsioClientTest {

    @Test
    public void retrieveWorkspace() {
        Workspace workspace = client.workspaces.create("my workspace");

        Workspace retrievedWorkspace = client.workspaces.retrieve(workspace.key());

        assertThat(retrievedWorkspace.id()).isNotZero();
        assertThat(retrievedWorkspace.name()).isEqualTo("my workspace");
        assertThat(retrievedWorkspace.key()).isNotNull();
        assertThat(retrievedWorkspace.secretKey()).isNotNull();
        assertThat(retrievedWorkspace.isTest()).isFalse();
        assertThat(retrievedWorkspace.isActive()).isTrue();
    }

}
