package seatsio.events;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.holdTokens.HoldToken;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;
import static seatsio.events.EventObjectInfo.FREE;

public class ReleaseObjectsTest extends SeatsioClientTest {

    @Test
    public void test() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.book(event.key, newArrayList("A-1", "A-2"));

        ChangeObjectStatusResult result = client.events.release(event.key, newArrayList("A-1", "A-2"));

        assertThat(client.events.retrieveObjectInfo(event.key, "A-1").status).isEqualTo(FREE);
        assertThat(client.events.retrieveObjectInfo(event.key, "A-2").status).isEqualTo(FREE);
        assertThat(result.objects).containsOnlyKeys("A-1", "A-2");
    }

    @Test
    public void holdToken() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        HoldToken holdToken = client.holdTokens.create();
        client.events.hold(event.key, newArrayList("A-1", "A-2"), holdToken.holdToken);

        client.events.release(event.key, newArrayList("A-1", "A-2"), holdToken.holdToken);

        EventObjectInfo status1 = client.events.retrieveObjectInfo(event.key, "A-1");
        assertThat(status1.status).isEqualTo(FREE);
        assertThat(status1.holdToken).isNull();

        EventObjectInfo status2 = client.events.retrieveObjectInfo(event.key, "A-2");
        assertThat(status2.status).isEqualTo(FREE);
        assertThat(status2.holdToken).isNull();
    }

    @Test
    public void orderId() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        client.events.release(event.key, newArrayList("A-1", "A-2"), null, "order1", null, null, null);

        assertThat(client.events.retrieveObjectInfo(event.key, "A-1").orderId).isEqualTo("order1");
        assertThat(client.events.retrieveObjectInfo(event.key, "A-2").orderId).isEqualTo("order1");
    }

    @Test
    public void keepExtraData() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.updateExtraData(event.key, "A-1", ImmutableMap.of("foo", "bar"));

        client.events.release(event.key, newArrayList("A-1"), null, null, true, null, null);

        assertThat(client.events.retrieveObjectInfo(event.key, "A-1").extraData).isEqualTo(ImmutableMap.of("foo", "bar"));
    }

    @Test
    public void channelKeys() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.channels.replace(event.key, List.of(new Channel("channelKey1", "channel 1", "#FFFF99", 1, newHashSet("A-1", "A-2"))));

        client.events.book(event.key, newArrayList("A-1"), null, null, true, null, newHashSet("channelKey1"));

        client.events.release(event.key, newArrayList("A-1"), null, null, true, null, newHashSet("channelKey1"));

        assertThat(client.events.retrieveObjectInfo(event.key, "A-1").status).isEqualTo(FREE);
    }

    @Test
    public void ignoreChannels() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.channels.replace(event.key, List.of(new Channel("channelKey1", "channel 1", "#FFFF99", 1, newHashSet("A-1", "A-2"))));
        client.events.book(event.key, newArrayList("A-1"), null, null, true, true, null);

        client.events.release(event.key, newArrayList("A-1"), null, null, null, true, null);

        assertThat(client.events.retrieveObjectInfo(event.key, "A-1").status).isEqualTo(FREE);
    }
}
