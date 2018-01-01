package seatsio.events;

import org.junit.Test;
import seatsio.SeatsioClientTest;
import seatsio.charts.Chart;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;

public class RetrieveEventTest extends SeatsioClientTest {

    @Test
    public void test() {
        Chart chart = client.charts().create();
        Event event = client.events().create(chart.key);

        Event retrievedEvent = client.events().retrieve(event.key);

        assertThat(retrievedEvent.id).isNotZero();
        assertThat(retrievedEvent.key).isNotNull();
        assertThat(retrievedEvent.chartKey).isEqualTo(chart.key);
        assertThat(retrievedEvent.bookWholeTables).isFalse();
        assertThat(retrievedEvent.forSaleConfig).isNull();
        Instant now = Instant.now();
        assertThat(retrievedEvent.createdOn).isBetween(now.minus(1, MINUTES), now);
        assertThat(retrievedEvent.updatedOn).isNull();
    }
}
