package seatsio.events;

import org.junit.Test;
import seatsio.SeatsioClientTest;
import seatsio.holdTokens.HoldToken;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static seatsio.events.ObjectStatus.HELD;

public class HoldObjectsTest extends SeatsioClientTest {

    @Test
    public void holdToken() {
        String chartKey = createTestChart();
        Event event = client.events().create(chartKey);
        HoldToken holdToken = client.holdTokens().create();

        client.events().hold(event.key, newArrayList("A-1", "A-2"), holdToken.holdToken);

        ObjectStatus status1 = client.events().getObjectStatus(event.key, "A-1");
        assertThat(status1.status).isEqualTo(HELD);
        assertThat(status1.holdToken).isEqualTo(holdToken.holdToken);

        ObjectStatus status2 = client.events().getObjectStatus(event.key, "A-2");
        assertThat(status2.status).isEqualTo(HELD);
        assertThat(status2.holdToken).isEqualTo(holdToken.holdToken);
    }

    @Test
    public void orderId() {
        String chartKey = createTestChart();
        Event event = client.events().create(chartKey);
        HoldToken holdToken = client.holdTokens().create();

        client.events().hold(event.key, newArrayList("A-1", "A-2"), holdToken.holdToken, "order1");

        assertThat(client.events().getObjectStatus(event.key, "A-1").orderId).isEqualTo("order1");
        assertThat(client.events().getObjectStatus(event.key, "A-2").orderId).isEqualTo("order1");
    }
}
