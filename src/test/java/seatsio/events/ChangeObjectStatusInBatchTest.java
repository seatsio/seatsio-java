package seatsio.events;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;

public class ChangeObjectStatusInBatchTest extends SeatsioClientTest {

    @Test
    public void test() {
        String chartKey1 = createTestChart();
        String chartKey2 = createTestChart();
        Event event1 = client.events.create(chartKey1);
        Event event2 = client.events.create(chartKey2);

        List<ChangeObjectStatusResult> result = client.events.changeObjectStatus(newArrayList(
                new StatusChangeRequest(event1.key, newArrayList("A-1"), "lolzor"),
                new StatusChangeRequest(event2.key, newArrayList("A-2"), "lolzor")
        ));

        assertThat(result.get(0).objects.get("A-1").status).isEqualTo("lolzor");
        assertThat(client.events.retrieveObjectStatus(event1.key, "A-1").status).isEqualTo("lolzor");

        assertThat(result.get(1).objects.get("A-2").status).isEqualTo("lolzor");
        assertThat(client.events.retrieveObjectStatus(event2.key, "A-2").status).isEqualTo("lolzor");
    }

    @Test
    public void channelKeys() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.updateChannels(event.key, ImmutableMap.of(
                "channelKey1", new Channel("channel 1", "#FFFF99", 1)
        ));
        client.events.assignObjectsToChannel(event.key, ImmutableMap.of(
                "channelKey1", newHashSet("A-1")
        ));

        List<ChangeObjectStatusResult> result = client.events.changeObjectStatus(newArrayList(
                new StatusChangeRequest(event.key, newArrayList("A-1"), "lolzor", null, null, null, null, newHashSet("channelKey1"))
        ));

        assertThat(result.get(0).objects.get("A-1").status).isEqualTo("lolzor");
    }

    @Test
    public void ignoreChannels() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.updateChannels(event.key, ImmutableMap.of(
                "channelKey1", new Channel("channel 1", "#FFFF99", 1)
        ));
        client.events.assignObjectsToChannel(event.key, ImmutableMap.of(
                "channelKey1", newHashSet("A-1")
        ));

        List<ChangeObjectStatusResult> result = client.events.changeObjectStatus(newArrayList(
                new StatusChangeRequest(event.key, newArrayList("A-1"), "lolzor", null, null, null, true, null)
        ));

        assertThat(result.get(0).objects.get("A-1").status).isEqualTo("lolzor");
    }
}
