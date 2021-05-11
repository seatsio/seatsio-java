package seatsio.workspaces;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ListInactiveWorkspacesTest extends SeatsioClientTest {

    @Test
    public void test() {
        Workspace ws1 = client.workspaces.create("ws1");
        client.workspaces.deactivate(ws1.key);
        Workspace ws2 = client.workspaces.create("ws2");
        Workspace ws3 = client.workspaces.create("ws3");
        client.workspaces.deactivate(ws3.key);

        Stream<Workspace> workspaces = client.workspaces.inactive.all(null);

        assertThat(workspaces)
                .extracting(workspace -> workspace.key)
                .containsExactly(ws3.key, ws1.key);
    }

    @Test
    public void takesFilter() {
        Workspace ws1 = client.workspaces.create("someWorkspace");
        client.workspaces.deactivate(ws1.key);
        Workspace ws2 = client.workspaces.create("anotherWorkspace");
        client.workspaces.deactivate(ws2.key);
        Workspace ws3 = client.workspaces.create("anotherAnotherWorkspace");
        client.workspaces.deactivate(ws3.key);
        Workspace ws4 = client.workspaces.create("anotherAnotherAnotherWorkspace");

        Stream<Workspace> workspaces = client.workspaces.inactive.all("another");

        assertThat(workspaces)
                .extracting(workspace -> workspace.name)
                .containsExactly("anotherAnotherWorkspace", "anotherWorkspace");
    }

}
