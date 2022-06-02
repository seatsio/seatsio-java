package seatsio.events;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;

public class AddObjectsToChannelTest extends SeatsioClientTest {

    @Test
    public void test() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.addChannel(event.key, "channelKey1", "channel 1", "#FFFF98", 1, newHashSet("A-1", "A-2"));
        client.events.addChannel(event.key, "channelKey2", "channel 2", "#FFFF99", 2, newHashSet("A-3", "A-4"));

        client.events.addObjectsToChannel(event.key, "channelKey1", newHashSet("A-3", "A-4"));

        Event retrievedEvent = client.events.retrieve(event.key);
        assertThat(retrievedEvent.channels).containsExactly(
                new Channel("channelKey1", "channel 1", "#FFFF98", 1, newHashSet("A-1", "A-2", "A-3", "A-4")),
                new Channel("channelKey2", "channel 2", "#FFFF99", 2, newHashSet())
        );
    }
}
