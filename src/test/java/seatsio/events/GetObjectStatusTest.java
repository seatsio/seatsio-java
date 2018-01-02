package seatsio.events;

import org.junit.Test;
import seatsio.SeatsioClientTest;
import seatsio.holdTokens.HoldToken;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static seatsio.events.ObjectStatus.BOOKED;
import static seatsio.events.ObjectStatus.FREE;

public class GetObjectStatusTest extends SeatsioClientTest {

    @Test
    public void test() {
        String chartKey = createTestChart();
        Event event = client.events().create(chartKey);

        ObjectStatus objectStatus = client.events().getObjectStatus(event.key, "A-1");

        assertThat(objectStatus.status).isEqualTo(FREE);
    }

}
