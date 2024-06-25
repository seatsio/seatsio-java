package seatsio.events;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class UpdateExtraDataTest extends SeatsioClientTest {

    @Test
    public void test() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        Map<String, Object> extraData = Map.of("foo", "bar");

        client.events.updateExtraData(event.key, "A-1", extraData);

        assertThat(client.events.retrieveObjectInfo(event.key, "A-1").extraData).isEqualTo(extraData);
    }

}
