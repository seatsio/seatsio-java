package seatsio.events;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.charts.Chart;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MarkEverythingAsForNotSaleTest extends SeatsioClientTest {

    @Test
    public void test() {
        Chart chart = client.charts.create();
        Event event = client.events.create(chart.key());
        client.events.replaceForSaleConfig(true, event.key(), List.of("o1", "o2"), null, List.of("cat1", "cat2"));

        client.events.markEverythingAsForNotSale(event.key());

        Event retrievedEvent = client.events.retrieve(event.key());
        assertThat(retrievedEvent.forSaleConfig().forSale()).isTrue();
        assertThat(retrievedEvent.forSaleConfig().objects()).isEmpty();
        assertThat(retrievedEvent.forSaleConfig().areaPlaces()).isEmpty();
        assertThat(retrievedEvent.forSaleConfig().categories()).isEmpty();
    }

}
