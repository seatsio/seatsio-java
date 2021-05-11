package seatsio.workspaces;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ListActiveWorkspacesTest extends SeatsioClientTest {

    @Test
    public void test() {
        Workspace ws1 = client.workspaces.create("ws1");
        Workspace ws2 = client.workspaces.create("ws2");
        client.workspaces.deactivate(ws2.key);
        Workspace ws3 = client.workspaces.create("ws3");

        Stream<Workspace> workspaces = client.workspaces.active.all(null);

        assertThat(workspaces)
                .extracting(workspace -> workspace.key)
                .containsExactly(ws3.key, ws1.key, workspace.key);
    }

    @Test
    public void takesFilter() {
        Workspace ws1 = client.workspaces.create("someWorkspace");
        Workspace ws2 = client.workspaces.create("anotherWorkspace");
        Workspace ws3 = client.workspaces.create("anotherAnotherWorkspace");
        Workspace ws4 = client.workspaces.create("anotherAnotherAnotherWorkspace");
        client.workspaces.deactivate(ws4.key);

        Stream<Workspace> workspaces = client.workspaces.active.all("another");

        assertThat(workspaces)
                .extracting(workspace -> workspace.name)
                .containsExactly("anotherAnotherWorkspace", "anotherWorkspace");
    }

}
