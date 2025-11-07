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
        client.workspaces.deactivate(ws2.key());
        Workspace ws3 = client.workspaces.create("ws3");

        Stream<Workspace> workspaces = client.workspaces.listAll();

        assertThat(workspaces)
                .extracting(workspace -> workspace.key())
                .containsExactly(ws3.key(), ws2.key(), ws1.key(), workspace.key());
    }

    @Test
    public void takesFilter() {
        Workspace ws1 = client.workspaces.create("someWorkspace");
        Workspace ws2 = client.workspaces.create("anotherWorkspace");
        client.workspaces.deactivate(ws2.key());
        Workspace ws3 = client.workspaces.create("anotherAnotherWorkspace");

        Stream<Workspace> workspaces = client.workspaces.listAll("another");

        assertThat(workspaces)
                .extracting(workspace -> workspace.name())
                .containsExactly("anotherAnotherWorkspace", "anotherWorkspace");
    }

}
