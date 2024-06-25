package seatsio.events;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class RetrieveEventObjectInfosTest extends SeatsioClientTest {

    @Test
    public void test() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        Map<String, EventObjectInfo> objectInfos = client.events.retrieveObjectInfos(event.key, List.of("A-1", "A-2"));

        assertThat(objectInfos).containsOnlyKeys("A-1", "A-2");
    }

}
