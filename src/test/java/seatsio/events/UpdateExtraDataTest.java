package seatsio.events;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

public class UpdateExtraDataTest extends SeatsioClientTest {

    @Test
    public void test() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        Map<String, Object> extraData = ImmutableMap.of("foo", "bar");

        client.events.updateExtraData(event.key, "A-1", extraData);

        assertThat(client.events.retrieveObjectInfo(event.key, "A-1").extraData).isEqualTo(extraData);
    }

}
