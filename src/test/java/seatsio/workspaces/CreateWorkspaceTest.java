package seatsio.workspaces;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateWorkspaceTest extends SeatsioClientTest {

    @Test
    public void createWorkspace() {
        Workspace workspace = client.workspaces.create("my workspace");

        assertThat(workspace.name()).isEqualTo("my workspace");
        assertThat(workspace.key()).isNotNull();
        assertThat(workspace.secretKey()).isNotNull();
        assertThat(workspace.isTest()).isFalse();
        assertThat(workspace.isActive()).isTrue();
    }

    @Test
    public void createTestWorkspace() {
        Workspace workspace = client.workspaces.create("my workspace", true);

        assertThat(workspace.name()).isEqualTo("my workspace");
        assertThat(workspace.key()).isNotNull();
        assertThat(workspace.secretKey()).isNotNull();
        assertThat(workspace.isTest()).isTrue();
        assertThat(workspace.isActive()).isTrue();
    }

}
