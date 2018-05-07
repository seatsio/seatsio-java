package seatsio.events;

import org.junit.Test;
import seatsio.SeatsioClientTest;
import seatsio.charts.Chart;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

public class MarkEverythingAsForSaleTest extends SeatsioClientTest{

    @Test
    public void test() {
        Chart chart = client.charts.create();
        Event event = client.events.create(chart.key);
        client.events.markAsNotForSale(event.key, newArrayList("o1", "o2"), newArrayList("cat1", "cat2"));

        client.events.markEverythingAsForSale(event.key);

        Event retrievedEvent = client.events.retrieve(event.key);
        assertThat(retrievedEvent.forSaleConfig).isNull();
    }

}
