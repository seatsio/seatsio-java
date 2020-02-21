package seatsio.workspaces;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ListWorkspacesTest extends SeatsioClientTest {

    @Test
    public void test() {
        Workspace ws1 = client.workspaces.create("ws1");
        Workspace ws2 = client.workspaces.create("ws2");
        Workspace ws3 = client.workspaces.create("ws3");

        Stream<Workspace> workspaces = client.workspaces.listAll();

        assertThat(workspaces)
                .extracting(workspace -> workspace.name)
                .containsExactly("ws3", "ws2", "ws1", "Main workspace");
    }

}
