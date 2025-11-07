package seatsio.events;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static seatsio.events.EventObjectInfo.RESALE;

public class PutUpForResaleTest extends SeatsioClientTest {

    @Test
    public void putUpForResaleForSingleEvent() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        ChangeObjectStatusResult result = client.events.putUpForResale(event.key(), List.of("A-1", "A-2"), "listing1");

        assertThat(client.events.retrieveObjectInfo(event.key(), "A-1").status()).isEqualTo(RESALE);
        assertThat(client.events.retrieveObjectInfo(event.key(), "A-1").resaleListingId()).isEqualTo("listing1");
        assertThat(client.events.retrieveObjectInfo(event.key(), "A-2").status()).isEqualTo(RESALE);
        assertThat(client.events.retrieveObjectInfo(event.key(), "A-2").resaleListingId()).isEqualTo("listing1");
        assertThat(result.objects()).containsOnlyKeys("A-1", "A-2");
    }

    @Test
    public void putUpForResaleForEventGroup() {
        String chartKey = createTestChart();
        Event event1 = client.events.create(chartKey);
        Event event2 = client.events.create(chartKey);

        ChangeObjectStatusResult result = client.events.putUpForResale(List.of(event1.key(), event2.key()), List.of("A-1", "A-2"), null);

        assertThat(client.events.retrieveObjectInfo(event1.key(), "A-1").status()).isEqualTo(RESALE);
        assertThat(client.events.retrieveObjectInfo(event1.key(), "A-2").status()).isEqualTo(RESALE);
        assertThat(client.events.retrieveObjectInfo(event2.key(), "A-1").status()).isEqualTo(RESALE);
        assertThat(client.events.retrieveObjectInfo(event2.key(), "A-2").status()).isEqualTo(RESALE);
        assertThat(result.objects()).containsOnlyKeys("A-1", "A-2");
    }

}
