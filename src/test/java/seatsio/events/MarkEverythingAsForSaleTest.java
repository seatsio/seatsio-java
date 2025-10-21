package seatsio.events;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.charts.Chart;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MarkEverythingAsForSaleTest extends SeatsioClientTest {

    @Test
    public void test() {
        Chart chart = client.charts.create();
        Event event = client.events.create(chart.key);
        client.events.replaceForSaleConfig(false, event.key, List.of("o1", "o2"), null, List.of("cat1", "cat2"));

        client.events.markEverythingAsForSale(event.key);

        Event retrievedEvent = client.events.retrieve(event.key);
        assertThat(retrievedEvent.forSaleConfig).isNull();
    }

}
