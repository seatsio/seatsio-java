package seatsio.events;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class UpdateExtraDatasTest extends SeatsioClientTest {

    @Test
    public void test() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        Map<String, Object> extraData1 = Map.of("foo1", "bar1");
        Map<String, Object> extraData2 = Map.of("foo2", "bar2");

        client.events.updateExtraDatas(event.key, Map.of("A-1", extraData1, "A-2", extraData2));

        assertThat(client.events.retrieveObjectInfo(event.key, "A-1").extraData).isEqualTo(extraData1);
        assertThat(client.events.retrieveObjectInfo(event.key, "A-2").extraData).isEqualTo(extraData2);
    }

}
