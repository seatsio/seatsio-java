package seatsio.workspaces;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.SeatsioException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DeleteWorkspaceTest extends SeatsioClientTest {

    @Test
    public void deleteInactiveWorkspace() {
        Workspace workspace = client.workspaces.create("my workspace");
        client.workspaces.deactivate(workspace.key());
        client.workspaces.delete(workspace.key());

        SeatsioException ex = assertThrows(SeatsioException.class, () -> client.workspaces.retrieve(workspace.key()));
        assertThat(ex.errors.get(0).getCode()).isEqualTo("WORKSPACE_NOT_FOUND");
    }
}
