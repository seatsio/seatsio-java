package seatsio.events;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.holdTokens.HoldToken;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static seatsio.events.EventObjectInfo.HELD;

public class HoldObjectsTest extends SeatsioClientTest {

    @Test
    public void holdToken() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        HoldToken holdToken = client.holdTokens.create();

        client.events.hold(event.key(), List.of("A-1", "A-2"), holdToken.holdToken());

        EventObjectInfo status1 = client.events.retrieveObjectInfo(event.key(), "A-1");
        assertThat(status1.status()).isEqualTo(HELD);
        assertThat(status1.holdToken()).isEqualTo(holdToken.holdToken());

        EventObjectInfo status2 = client.events.retrieveObjectInfo(event.key(), "A-2");
        assertThat(status2.status()).isEqualTo(HELD);
        assertThat(status2.holdToken()).isEqualTo(holdToken.holdToken());
    }

    @Test
    public void orderId() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        HoldToken holdToken = client.holdTokens.create();

        client.events.hold(event.key(), List.of("A-1", "A-2"), holdToken.holdToken(), "order1", null, null, null);

        assertThat(client.events.retrieveObjectInfo(event.key(), "A-1").orderId()).isEqualTo("order1");
        assertThat(client.events.retrieveObjectInfo(event.key(), "A-2").orderId()).isEqualTo("order1");
    }

    @Test
    public void keepExtraData() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        HoldToken holdToken = client.holdTokens.create();
        client.events.updateExtraData(event.key(), "A-1", Map.of("foo", "bar"));

        client.events.hold(event.key(), List.of("A-1"), holdToken.holdToken(), null, true, null, null);

        assertThat(client.events.retrieveObjectInfo(event.key(), "A-1").extraData()).isEqualTo(Map.of("foo", "bar"));
    }

    @Test
    public void labels() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        HoldToken holdToken = client.holdTokens.create();

        ChangeObjectStatusResult result = client.events.hold(event.key(), List.of("A-1", "A-2"), holdToken.holdToken());

        assertThat(result.objects()).containsOnlyKeys("A-1", "A-2");
    }

    @Test
    public void channelKeys() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey, new CreateEventParams().withChannels(List.of(
                new Channel("channelKey1", "channel 1", "#FFFF99", 1, Set.of("A-1", "A-2"))
        )));
        HoldToken holdToken = client.holdTokens.create();

        client.events.hold(event.key(), List.of("A-1"), holdToken.holdToken(), null, true, null, Set.of("channelKey1"));

        assertThat(client.events.retrieveObjectInfo(event.key(), "A-1").status()).isEqualTo(HELD);
    }

    @Test
    public void ignoreChannels() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey, new CreateEventParams().withChannels(List.of(
                new Channel("channelKey1", "channel 1", "#FFFF99", 1, Set.of("A-1", "A-2"))
        )));
        HoldToken holdToken = client.holdTokens.create();

        client.events.hold(event.key(), List.of("A-1"), holdToken.holdToken(), null, null, true, null);

        assertThat(client.events.retrieveObjectInfo(event.key(), "A-1").status()).isEqualTo(HELD);
    }
}
