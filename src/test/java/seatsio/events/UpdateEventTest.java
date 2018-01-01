package seatsio.events;

import org.junit.Test;
import seatsio.SeatsioClientTest;
import seatsio.charts.Chart;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;

public class UpdateEventTest extends SeatsioClientTest {

    @Test
    public void updateChartKey() {
        Chart chart1 = client.charts().create();
        Event event = client.events().create(chart1.key);
        Chart chart2 = client.charts().create();

        client.events().update(event.key, chart2.key, null, null);

        Event retrievedEvent = client.events().retrieve(event.key);
        assertThat(retrievedEvent.key).isEqualTo(event.key);
        assertThat(retrievedEvent.chartKey).isEqualTo(chart2.key);
        assertThat(retrievedEvent.updatedOn).isBetween(now().minus(1, MINUTES), now().plus(1, MINUTES));
    }

    @Test
    public void updateEventKey() {
        Chart chart = client.charts().create();
        Event event = client.events().create(chart.key);

        client.events().update(event.key, null, "newKey", null);

        Event retrievedEvent = client.events().retrieve("newKey");
        assertThat(retrievedEvent.key).isEqualTo("newKey");
        assertThat(retrievedEvent.id).isEqualTo(event.id);
    }

    @Test
    public void updateBookWholeTables() {
        Chart chart = client.charts().create();
        Event event = client.events().create(chart.key);

        client.events().update(event.key, null, null, true);

        Event retrievedEvent = client.events().retrieve(event.key);
        assertThat(retrievedEvent.bookWholeTables).isTrue();
    }
}
