package seatsio.charts;

import org.junit.Test;
import seatsio.SeatsioClientTest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class RetrievePublishedVersionTest extends SeatsioClientTest {

    @Test
    public void test() {
        Chart chart = client.charts.create("chartName");

        Map<?, ?> publishedVersion = client.charts.retrievePublishedVersion(chart.key);

        assertThat(publishedVersion.get("name")).isEqualTo("chartName");
    }
}
