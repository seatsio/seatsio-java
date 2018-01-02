package seatsio.events;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import seatsio.SeatsioClientTest;
import seatsio.holdTokens.HoldToken;

import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static seatsio.events.ObjectStatus.BOOKED;
import static seatsio.events.ObjectStatus.FREE;

public class UpdateExtraDataTest extends SeatsioClientTest {

    @Test
    public void test() {
        String chartKey = createTestChart();
        Event event = client.events().create(chartKey);
        Map<String, String> extraData = ImmutableMap.of("foo", "bar");

        client.events().updateExtraData(event.key, "A-1", extraData);

        assertThat(client.events().getObjectStatus(event.key, "A-1").extraData).isEqualTo(extraData);
    }

}
