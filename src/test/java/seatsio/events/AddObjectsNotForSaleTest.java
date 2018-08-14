package seatsio.events;

import org.junit.Test;
import seatsio.SeatsioClientTest;
import seatsio.SeatsioException;
import seatsio.charts.Chart;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

public class AddObjectsNotForSaleTest extends SeatsioClientTest{

    @Test
    public void addObjects() {
        Chart chart = client.charts.create();
        Event event = client.events.create(chart.key);

        client.events.addObjectsNotForSale(event.key, newArrayList("o1", "o2"));

        Event retrievedEvent = client.events.retrieve(event.key);
        assertThat(retrievedEvent.forSaleConfig.forSale).isFalse();
        assertThat(retrievedEvent.forSaleConfig.objects).containsExactly("o1", "o2");
        assertThat(retrievedEvent.forSaleConfig.categories).isEmpty();
    }

    @Test
    public void someObjectsAlreadyMarkedAsNotForSale() {
        Chart chart = client.charts.create();
        Event event = client.events.create(chart.key);
        client.events.markAsNotForSale(event.key, newArrayList("o1", "o2"), null);

        client.events.addObjectsNotForSale(event.key, newArrayList("o2", "o3"));

        Event retrievedEvent = client.events.retrieve(event.key);
        assertThat(retrievedEvent.forSaleConfig.forSale).isFalse();
        assertThat(retrievedEvent.forSaleConfig.objects).containsExactly("o1", "o2", "o3");
        assertThat(retrievedEvent.forSaleConfig.categories).isEmpty();
    }

    @Test(expected = SeatsioException.class)
    public void someObjectsMarkedAsForSale() {
        Chart chart = client.charts.create();
        Event event = client.events.create(chart.key);
        client.events.markAsForSale(event.key, newArrayList("o1", "o2"), null);

        client.events.addObjectsNotForSale(event.key, newArrayList("o2", "o3"));
    }
}
