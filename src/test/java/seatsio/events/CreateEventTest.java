package seatsio.events;

import org.junit.Test;
import seatsio.SeatsioClientTest;
import seatsio.charts.Chart;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;

public class CreateEventTest extends SeatsioClientTest {

    @Test
    public void test() {
        Chart chart = client.charts().create();

        Event event = client.events().create(chart.key);

        assertThat(event.id).isNotZero();
        assertThat(event.key).isNotNull();
        assertThat(event.chartKey).isEqualTo(chart.key);
        assertThat(event.bookWholeTables).isFalse();
        assertThat(event.forSaleConfig).isNull();
        Instant now = Instant.now();
        assertThat(event.createdOn).isBetween(now.minus(1, MINUTES), now);
        assertThat(event.updatedOn).isNull();
    }
}
