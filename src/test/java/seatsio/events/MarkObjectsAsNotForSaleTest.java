package seatsio.events;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class MarkObjectsAsNotForSaleTest extends SeatsioClientTest{

    @Test
    public void objectsAndCategoriesAndAreaPlaces() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        client.events.markAsNotForSale(event.key, List.of("o1", "o2"), Map.of("GA1", 3), List.of("cat1", "cat2"));

        Event retrievedEvent = client.events.retrieve(event.key);
        assertThat(retrievedEvent.forSaleConfig.forSale).isFalse();
        assertThat(retrievedEvent.forSaleConfig.objects).containsExactly("o1", "o2");
        assertThat(retrievedEvent.forSaleConfig.areaPlaces).isEqualTo(Map.of("GA1", 3));
        assertThat(retrievedEvent.forSaleConfig.categories).containsExactly("cat1", "cat2");
    }

    @Test
    public void objects() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        client.events.markAsNotForSale(event.key, List.of("o1", "o2"), null, null);

        Event retrievedEvent = client.events.retrieve(event.key);
        assertThat(retrievedEvent.forSaleConfig.forSale).isFalse();
        assertThat(retrievedEvent.forSaleConfig.objects).containsExactly("o1", "o2");
        assertThat(retrievedEvent.forSaleConfig.areaPlaces).isEmpty();
        assertThat(retrievedEvent.forSaleConfig.categories).isEmpty();
    }

    @Test
    public void areaPlaces() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        client.events.markAsNotForSale(event.key, null, Map.of("GA1", 3), null);

        Event retrievedEvent = client.events.retrieve(event.key);
        assertThat(retrievedEvent.forSaleConfig.forSale).isFalse();
        assertThat(retrievedEvent.forSaleConfig.objects).isEmpty();
        assertThat(retrievedEvent.forSaleConfig.areaPlaces).isEqualTo(Map.of("GA1", 3));
        assertThat(retrievedEvent.forSaleConfig.categories).isEmpty();
    }

    @Test
    public void categories() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        client.events.markAsNotForSale(event.key, null, null, List.of("cat1", "cat2"));

        Event retrievedEvent = client.events.retrieve(event.key);
        assertThat(retrievedEvent.forSaleConfig.forSale).isFalse();
        assertThat(retrievedEvent.forSaleConfig.objects).isEmpty();
        assertThat(retrievedEvent.forSaleConfig.areaPlaces).isEmpty();
        assertThat(retrievedEvent.forSaleConfig.categories).containsExactly("cat1", "cat2");
    }

    @Test
    public void numNotForSale() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        client.events.markAsNotForSale(event.key, null, Map.of("GA1", 5), null);

        EventObjectInfo status = client.events.retrieveObjectInfo(event.key, "GA1");
        assertThat(status.numNotForSale).isEqualTo(5);
    }
}
