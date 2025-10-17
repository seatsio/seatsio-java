package seatsio.events;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class MarkObjectsAsForSaleTest extends SeatsioClientTest{

    @Test
    public void objectsAndCategories() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        client.events.markAsForSale(event.key, List.of("o1", "o2"), Map.of("GA1", 3), List.of("cat1", "cat2"));

        Event retrievedEvent = client.events.retrieve(event.key);
        assertThat(retrievedEvent.forSaleConfig.forSale).isTrue();
        assertThat(retrievedEvent.forSaleConfig.objects).containsExactly("o1", "o2");
        assertThat(retrievedEvent.forSaleConfig.areaPlaces).isEqualTo(Map.of("GA1", 3));
        assertThat(retrievedEvent.forSaleConfig.categories).containsExactly("cat1", "cat2");
    }
}
