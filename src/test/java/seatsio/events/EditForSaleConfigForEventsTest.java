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

        Map<String, ForSaleConfig> forSaleConfigs = client.events.editForSaleConfigForEvents(Map.of(
                event1.key, new ForSaleAndNotForSaleParams(List.of(new ObjectAndQuantity("A-1")), null),
                event2.key, new ForSaleAndNotForSaleParams(List.of(new ObjectAndQuantity("A-2")), null))
        );

        assertThat(forSaleConfigs.get(event1.key).forSale).isFalse();
        assertThat(forSaleConfigs.get(event1.key).objects).containsExactly("A-2", "A-3");

        assertThat(forSaleConfigs.get(event2.key).forSale).isFalse();
        assertThat(forSaleConfigs.get(event2.key).objects).containsExactly("A-1", "A-3");
    }

    @Test
    public void makeNotForSale() {
        String chartKey = createTestChart();
        Event event1 = client.events.create(chartKey);
        Event event2 = client.events.create(chartKey);

        Map<String, ForSaleConfig> forSaleConfigs = client.events.editForSaleConfigForEvents(Map.of(
                event1.key, new ForSaleAndNotForSaleParams(null, List.of(new ObjectAndQuantity("A-1"))),
                event2.key, new ForSaleAndNotForSaleParams(null, List.of(new ObjectAndQuantity("A-2"))))
        );

        assertThat(forSaleConfigs.get(event1.key).forSale).isFalse();
        assertThat(forSaleConfigs.get(event1.key).objects).containsExactly("A-1");

        assertThat(forSaleConfigs.get(event2.key).forSale).isFalse();
        assertThat(forSaleConfigs.get(event2.key).objects).containsExactly("A-2");
    }
}
