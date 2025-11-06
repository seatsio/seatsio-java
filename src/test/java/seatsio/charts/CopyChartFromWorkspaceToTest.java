package seatsio.charts;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClient;
import seatsio.SeatsioClientTest;
import seatsio.workspaces.Workspace;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class CopyChartFromWorkspaceToTest extends SeatsioClientTest {

    @Test
    public void test() {
        Chart chart = client.charts.create("my chart", "SIMPLE");
        Workspace toWorkspace = client.workspaces.create("my ws");

        Chart copiedChart = client.charts.copyToWorkspace(chart.key(), workspace.key(), toWorkspace.key());

        SeatsioClient workspaceClient = new SeatsioClient(toWorkspace.secretKey(), null, apiUrl());
        assertThat(copiedChart.name()).isEqualTo("my chart");
        Chart retrievedChart = workspaceClient.charts.retrieve(copiedChart.key());
        assertThat(retrievedChart.name()).isEqualTo("my chart");
        Map<?, ?> drawing = workspaceClient.charts.retrievePublishedVersion(copiedChart.key());
        assertThat(drawing.get("venueType")).isEqualTo("SIMPLE");
    }

}
