package seatsio.events;

import org.junit.Test;
import seatsio.SeatsioClientTest;
import seatsio.charts.Chart;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;

public class CreateEventTest extends SeatsioClientTest {

    @Test
    public void chartKeyIsRequired() {
        Chart chart = client.charts.create();

        Event event = client.events.create(chart.key);

        assertThat(event.id).isNotZero();
        assertThat(event.key).isNotNull();
        assertThat(event.chartKey).isEqualTo(chart.key);
        assertThat(event.bookWholeTables).isFalse();
        assertThat(event.forSaleConfig).isNull();
        Instant now = Instant.now();
        assertThat(event.createdOn).isBetween(now.minus(1, MINUTES), now.plus(1, MINUTES));
        assertThat(event.updatedOn).isNull();
    }

    @Test
    public void eventKeyCanBePassedIn() {
        Chart chart = client.charts.create();

        Event event = client.events.create(chart.key, "eventje", null);

        assertThat(event.key).isEqualTo("eventje");
        assertThat(event.bookWholeTables).isFalse();
    }

    @Test
    public void bookWholeTablesCanBePassedIn() {
        Chart chart = client.charts.create();

        Event event = client.events.create(chart.key, null, true);

        assertThat(event.key).isNotNull();
        assertThat(event.bookWholeTables).isTrue();
    }
}
