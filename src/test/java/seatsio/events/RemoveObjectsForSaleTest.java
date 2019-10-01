package seatsio.events;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.SeatsioException;
import seatsio.charts.Chart;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RemoveObjectsForSaleTest extends SeatsioClientTest {

    @Test
    public void removeAllObjects() {
        Chart chart = client.charts.create();
        Event event = client.events.create(chart.key);
        client.events.markAsForSale(event.key, newArrayList("o1", "o2"), newArrayList());

        client.events.removeObjectsForSale(event.key, newArrayList("o1", "o2"));

        Event retrievedEvent = client.events.retrieve(event.key);
        assertThat(retrievedEvent.forSaleConfig).isNull();
    }

    @Test
    public void removeSomeObjects() {
        Chart chart = client.charts.create();
        Event event = client.events.create(chart.key);
        client.events.markAsForSale(event.key, newArrayList("o1", "o2"), newArrayList("cat1", "cat2"));

        client.events.removeObjectsForSale(event.key, newArrayList("o1"));

        Event retrievedEvent = client.events.retrieve(event.key);
        assertThat(retrievedEvent.forSaleConfig.forSale).isTrue();
        assertThat(retrievedEvent.forSaleConfig.objects).containsExactly("o2");
        assertThat(retrievedEvent.forSaleConfig.categories).containsExactly("cat1", "cat2");
    }

    @Test
    public void nothingMarkedAsForSale() {
        Chart chart = client.charts.create();
        Event event = client.events.create(chart.key);

        client.events.removeObjectsForSale(event.key, newArrayList("o1", "o2"));

        Event retrievedEvent = client.events.retrieve(event.key);
        assertThat(retrievedEvent.forSaleConfig).isNull();
    }

    @Test
    public void someObjectsMarkedAsNotForSale() {
        Chart chart = client.charts.create();
        Event event = client.events.create(chart.key);
        client.events.markAsNotForSale(event.key, newArrayList("o1", "o2"), null);

        assertThrows(SeatsioException.class, () -> client.events.removeObjectsForSale(event.key, newArrayList("o2", "o3")));
    }
}
