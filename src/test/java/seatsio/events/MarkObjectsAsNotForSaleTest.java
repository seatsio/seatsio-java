package seatsio.events;

import org.junit.Test;
import seatsio.SeatsioClientTest;
import seatsio.charts.Chart;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

public class MarkObjectsAsNotForSaleTest extends SeatsioClientTest{

    @Test
    public void objectsAndCategories() {
        Chart chart = client.charts.create();
        Event event = client.events.create(chart.key);

        client.events.markAsNotForSale(event.key, newArrayList("o1", "o2"), newArrayList("cat1", "cat2"));

        Event retrievedEvent = client.events.retrieve(event.key);
        assertThat(retrievedEvent.forSaleConfig.forSale).isFalse();
        assertThat(retrievedEvent.forSaleConfig.objects).containsExactly("o1", "o2");
        assertThat(retrievedEvent.forSaleConfig.categories).containsExactly("cat1", "cat2");
    }

    @Test
    public void objects() {
        Chart chart = client.charts.create();
        Event event = client.events.create(chart.key);

        client.events.markAsNotForSale(event.key, newArrayList("o1", "o2"), null);

        Event retrievedEvent = client.events.retrieve(event.key);
        assertThat(retrievedEvent.forSaleConfig.forSale).isFalse();
        assertThat(retrievedEvent.forSaleConfig.objects).containsExactly("o1", "o2");
        assertThat(retrievedEvent.forSaleConfig.categories).isEmpty();
    }

    @Test
    public void categories() {
        Chart chart = client.charts.create();
        Event event = client.events.create(chart.key);

        client.events.markAsNotForSale(event.key, null, newArrayList("cat1", "cat2"));

        Event retrievedEvent = client.events.retrieve(event.key);
        assertThat(retrievedEvent.forSaleConfig.forSale).isFalse();
        assertThat(retrievedEvent.forSaleConfig.objects).isEmpty();
        assertThat(retrievedEvent.forSaleConfig.categories).containsExactly("cat1", "cat2");
    }
}
