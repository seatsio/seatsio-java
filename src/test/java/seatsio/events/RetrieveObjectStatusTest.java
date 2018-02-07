package seatsio.events;

import org.junit.Test;
import seatsio.SeatsioClientTest;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static seatsio.events.ObjectStatus.FREE;

public class RetrieveObjectStatusTest extends SeatsioClientTest {

    @Test
    public void test() {
        String chartKey = createTestChart();
        Event event = client.events().create(chartKey);

        ObjectStatus objectStatus = client.events().retrieveObjectStatus(event.key, "A-1");

        assertThat(objectStatus.status).isEqualTo(FREE);
    }

}
