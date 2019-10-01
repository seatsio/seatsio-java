package seatsio.charts;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class RetrieveDraftVersionTest extends SeatsioClientTest {

    @Test
    public void test() {
        Chart chart = client.charts.create();
        client.events.create(chart.key);
        client.charts.update(chart.key, "newName");

        Map<?, ?> draft = client.charts.retrieveDraftVersion(chart.key);

        assertThat(draft.get("name")).isEqualTo("newName");
    }
}
