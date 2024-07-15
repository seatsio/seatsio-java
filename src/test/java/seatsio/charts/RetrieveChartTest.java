package seatsio.charts;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.events.Event;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class RetrieveChartTest extends SeatsioClientTest {

    @Test
    public void test() {
        Chart chart = client.charts.create();
        client.charts.addTag(chart.key, "tag1");
        client.charts.addTag(chart.key, "tag2");

        Chart retrievedChart = client.charts.retrieve(chart.key);

        assertThat(retrievedChart.id).isNotZero();
        assertThat(retrievedChart.key).isNotBlank();
        assertThat(retrievedChart.status).isEqualTo("NOT_USED");
        assertThat(retrievedChart.name).isEqualTo("Untitled chart");
        assertThat(retrievedChart.publishedVersionThumbnailUrl).isNotBlank();
        assertThat(chart.draftVersionThumbnailUrl).isNull();
        assertThat(retrievedChart.events).isNull();
        assertThat(retrievedChart.tags).containsOnly("tag1", "tag2");
        assertThat(retrievedChart.archived).isFalse();
        assertThat(retrievedChart.venueType).isEqualTo("MIXED");
        assertThat(retrievedChart.validation).isNotNull();
        assertThat(retrievedChart.zones).isNull();
    }

    @Test
    public void zones() {
        String chart = createTestChartWithZones();

        Chart retrievedChart = client.charts.retrieve(chart);

        assertThat(retrievedChart.zones).isEqualTo(List.of(
                new Zone("finishline", "Finish Line"),
                new Zone("midtrack", "Mid Track")
        ));
    }

    @Test
    public void withEvents() {
        Chart chart = client.charts.create();
        Event event1 = client.events.create(chart.key);
        Event event2 = client.events.create(chart.key);

        Chart retrievedChart = client.charts.retrieveWithEvents(chart.key);

        assertThat(retrievedChart.events)
                .extracting("id")
                .containsExactly(event2.id, event1.id);
    }
}
