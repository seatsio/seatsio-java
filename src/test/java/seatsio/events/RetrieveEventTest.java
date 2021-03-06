package seatsio.events;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;

public class RetrieveEventTest extends SeatsioClientTest {

    @Test
    public void test() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        Event retrievedEvent = client.events.retrieve(event.key);

        assertThat(retrievedEvent.id).isNotZero();
        assertThat(retrievedEvent.key).isNotNull();
        assertThat(retrievedEvent.chartKey).isEqualTo(chartKey);
        assertThat(retrievedEvent.tableBookingConfig).isEqualTo(TableBookingConfig.inherit());
        assertThat(retrievedEvent.forSaleConfig).isNull();
        assertThat(retrievedEvent.supportsBestAvailable).isTrue();
        Instant now = Instant.now();
        assertThat(retrievedEvent.createdOn).isBetween(now.minus(1, MINUTES), now.plus(1, MINUTES));
        assertThat(retrievedEvent.updatedOn).isNull();
    }
}
