package seatsio.events;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class EditForSaleConfigTest extends SeatsioClientTest {

    @Test
    public void makeForSale() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey, new CreateEventParams().withForSaleConfigParams(new ForSaleConfigParams(false, List.of("A-1", "A-2", "A-3"), null, null)));

        EditForSaleConfigResult result = client.events.editForSaleConfig(event.key(), List.of(new ObjectAndQuantity("A-1"), new ObjectAndQuantity("A-2")), null);

        assertThat(result.forSaleConfig().forSale()).isFalse();
        assertThat(result.forSaleConfig().objects()).containsExactly("A-3");
    }

    @Test
    public void rateLimitInfoIsReturned() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey, new CreateEventParams().withForSaleConfigParams(new ForSaleConfigParams(false, List.of("A-1", "A-2", "A-3"), null, null)));

        EditForSaleConfigResult result = client.events.editForSaleConfig(event.key(), List.of(new ObjectAndQuantity("A-1"), new ObjectAndQuantity("A-2")), null);

        assertThat(result.rateLimitInfo().rateLimitRemainingCalls()).isEqualTo(9);
        assertThat(result.rateLimitInfo().rateLimitResetDate()).isNotNull();
    }

    @Test
    public void makeNotForSale() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        EditForSaleConfigResult result = client.events.editForSaleConfig(event.key(), null, List.of(new ObjectAndQuantity("A-1"), new ObjectAndQuantity("A-2")));

        assertThat(result.forSaleConfig().forSale()).isFalse();
        assertThat(result.forSaleConfig().objects()).containsExactly("A-1", "A-2");
    }

    @Test
    public void makeAreaPlacesNotForSale() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        EditForSaleConfigResult result = client.events.editForSaleConfig(event.key(), null, List.of(new ObjectAndQuantity("GA1", 5)));

        assertThat(result.forSaleConfig().forSale()).isFalse();
        assertThat(result.forSaleConfig().areaPlaces()).isEqualTo(Map.of("GA1", 5));
    }
}
