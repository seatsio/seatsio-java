package seatsio.events;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.charts.Chart;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

public class MarkObjectsAsNotForSaleTest extends SeatsioClientTest{

    @Test
    public void objectsAndCategoriesAndAreaPlaces() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        client.events.markAsNotForSale(event.key, newArrayList("o1", "o2"), ImmutableMap.of("GA1", 3), newArrayList("cat1", "cat2"));

        Event retrievedEvent = client.events.retrieve(event.key);
        assertThat(retrievedEvent.forSaleConfig.forSale).isFalse();
        assertThat(retrievedEvent.forSaleConfig.objects).containsExactly("o1", "o2");
        assertThat(retrievedEvent.forSaleConfig.areaPlaces).isEqualTo(ImmutableMap.of("GA1", 3));
        assertThat(retrievedEvent.forSaleConfig.categories).containsExactly("cat1", "cat2");
    }

    @Test
    public void objects() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        client.events.markAsNotForSale(event.key, newArrayList("o1", "o2"), null, null);

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

        client.events.markAsNotForSale(event.key, null, ImmutableMap.of("GA1", 3), null);

        Event retrievedEvent = client.events.retrieve(event.key);
        assertThat(retrievedEvent.forSaleConfig.forSale).isFalse();
        assertThat(retrievedEvent.forSaleConfig.objects).isEmpty();
        assertThat(retrievedEvent.forSaleConfig.areaPlaces).isEqualTo(ImmutableMap.of("GA1", 3));
        assertThat(retrievedEvent.forSaleConfig.categories).isEmpty();
    }

    @Test
    public void categories() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        client.events.markAsNotForSale(event.key, null, null, newArrayList("cat1", "cat2"));

        Event retrievedEvent = client.events.retrieve(event.key);
        assertThat(retrievedEvent.forSaleConfig.forSale).isFalse();
        assertThat(retrievedEvent.forSaleConfig.objects).isEmpty();
        assertThat(retrievedEvent.forSaleConfig.areaPlaces).isEmpty();
        assertThat(retrievedEvent.forSaleConfig.categories).containsExactly("cat1", "cat2");
    }
}
