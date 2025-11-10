package seatsio.events;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class EditForSaleConfigForEventsTest extends SeatsioClientTest {

    @Test
    public void makeForSale() {
        String chartKey = createTestChart();
        Event event1 = client.events.create(chartKey, new CreateEventParams().withForSaleConfigParams(new ForSaleConfigParams(false, List.of("A-1", "A-2", "A-3"), null, null)));
        Event event2 = client.events.create(chartKey, new CreateEventParams().withForSaleConfigParams(new ForSaleConfigParams(false, List.of("A-1", "A-2", "A-3"), null, null)));

        Map<String, EditForSaleConfigResult> result = client.events.editForSaleConfigForEvents(Map.of(
                event1.key(), new ForSaleAndNotForSaleParams(List.of(new ObjectAndQuantity("A-1")), null),
                event2.key(), new ForSaleAndNotForSaleParams(List.of(new ObjectAndQuantity("A-2")), null))
        );

        assertThat(result.get(event1.key()).forSaleConfig().forSale()).isFalse();
        assertThat(result.get(event1.key()).forSaleConfig().objects()).containsExactly("A-2", "A-3");

        assertThat(result.get(event2.key()).forSaleConfig().forSale()).isFalse();
        assertThat(result.get(event2.key()).forSaleConfig().objects()).containsExactly("A-1", "A-3");
    }

    @Test
    public void makeNotForSale() {
        String chartKey = createTestChart();
        Event event1 = client.events.create(chartKey);
        Event event2 = client.events.create(chartKey);

        Map<String, EditForSaleConfigResult> result = client.events.editForSaleConfigForEvents(Map.of(
                event1.key(), new ForSaleAndNotForSaleParams(null, List.of(new ObjectAndQuantity("A-1"))),
                event2.key(), new ForSaleAndNotForSaleParams(null, List.of(new ObjectAndQuantity("A-2"))))
        );

        assertThat(result.get(event1.key()).forSaleConfig().forSale()).isFalse();
        assertThat(result.get(event1.key()).forSaleConfig().objects()).containsExactly("A-1");

        assertThat(result.get(event2.key()).forSaleConfig().forSale()).isFalse();
        assertThat(result.get(event2.key()).forSaleConfig().objects()).containsExactly("A-2");
    }
}
