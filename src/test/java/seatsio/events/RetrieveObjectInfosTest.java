package seatsio.events;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

public class RetrieveObjectInfosTest extends SeatsioClientTest {

    @Test
    public void test() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        Map<String, ObjectInfo> objectInfos = client.events.retrieveObjectInfos(event.key, newArrayList("A-1", "A-2"));

        assertThat(objectInfos).containsOnlyKeys("A-1", "A-2");
    }

}
