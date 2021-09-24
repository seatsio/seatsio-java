package seatsio.events;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.holdTokens.HoldToken;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static seatsio.events.EventObjectInfo.FREE;

public class RetrieveEventObjectInfoTest extends SeatsioClientTest {

    @Test
    public void test() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        EventObjectInfo objectInfo = client.events.retrieveObjectInfo(event.key, "A-1");

        assertThat(objectInfo.status).isEqualTo(FREE);
        assertThat(objectInfo.forSale).isEqualTo(true);
    }

    @Test
    public void ga() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        HoldToken holdToken = client.holdTokens.create();
        client.events.hold(event.key, newArrayList("GA1"), holdToken.holdToken);

        EventObjectInfo objectInfo = client.events.retrieveObjectInfo(event.key, "GA1");

        assertThat(objectInfo.holds).isEqualTo(ImmutableMap.of(holdToken.holdToken, ImmutableMap.of("NO_TICKET_TYPE", 1)));
    }

}
