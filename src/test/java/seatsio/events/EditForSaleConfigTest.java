package seatsio.events;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class EditForSaleConfigTest extends SeatsioClientTest {

    @Test
    public void makeForSale() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey, new CreateEventParams().withForSaleConfigParams(new ForSaleConfigParams(false, List.of("A-1", "A-2", "A-3"), null, null)));

        ForSaleConfig forSaleConfig = client.events.editForSaleConfig(event.key, List.of(new ObjectAndQuantity("A-1"), new ObjectAndQuantity("A-2")), null);

        assertThat(forSaleConfig.forSale).isFalse();
        assertThat(forSaleConfig.objects).containsExactly("A-3");
    }

    @Test
    public void makeNotForSale() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        ForSaleConfig forSaleConfig = client.events.editForSaleConfig(event.key, null, List.of(new ObjectAndQuantity("A-1"), new ObjectAndQuantity("A-2")));

        assertThat(forSaleConfig.forSale).isFalse();
        assertThat(forSaleConfig.objects).containsExactly("A-1", "A-2");
    }
}
