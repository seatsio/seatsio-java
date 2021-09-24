package seatsio.events;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.holdTokens.HoldToken;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static seatsio.events.ObjectInfo.FREE;

public class RetrieveObjectInfoTest extends SeatsioClientTest {

    @Test
    public void test() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        ObjectInfo objectInfo = client.events.retrieveObjectInfo(event.key, "A-1");

        assertThat(objectInfo.status).isEqualTo(FREE);
        assertThat(objectInfo.forSale).isEqualTo(true);
    }

    @Test
    public void ga() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        HoldToken holdToken = client.holdTokens.create();
        client.events.hold(event.key, newArrayList("GA1"), holdToken.holdToken);

        ObjectInfo objectInfo = client.events.retrieveObjectInfo(event.key, "GA1");

        assertThat(objectInfo.holds).isEqualTo(ImmutableMap.of(holdToken.holdToken, ImmutableMap.of("NO_TICKET_TYPE", 1)));
    }

}
