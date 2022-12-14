package seatsio.charts;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClient;
import seatsio.SeatsioClientTest;
import seatsio.workspaces.Workspace;

import static org.assertj.core.api.Assertions.assertThat;

public class CopyChartToWorkspaceTest extends SeatsioClientTest {

    @Test
    public void test() {
        Chart chart = client.charts.create("my chart", "BOOTHS");
        Workspace workspace = client.workspaces.create("my ws");

        Chart copiedChart = client.charts.copyToWorkspace(chart.key, workspace.key);

        SeatsioClient workspaceClient = new SeatsioClient(workspace.secretKey, null, apiUrl());
        assertThat(copiedChart.name).isEqualTo("my chart");
        Chart retrievedChart = workspaceClient.charts.retrieve(copiedChart.key);
        assertThat(retrievedChart.name).isEqualTo("my chart");
    }

}
