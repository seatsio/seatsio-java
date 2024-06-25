package seatsio.events.channels;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.events.Channel;
import seatsio.events.Event;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class AddObjectsToChannelTest extends SeatsioClientTest {

    @Test
    public void test() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.channels.add(event.key, "channelKey1", "channel 1", "#FFFF98", 1, Set.of("A-1", "A-2"));
        client.events.channels.add(event.key, "channelKey2", "channel 2", "#FFFF99", 2, Set.of("A-3", "A-4"));

        client.events.channels.addObjects(event.key, "channelKey1", Set.of("A-3", "A-4"));

        Event retrievedEvent = client.events.retrieve(event.key);
        assertThat(retrievedEvent.channels).containsExactly(
                new Channel("channelKey1", "channel 1", "#FFFF98", 1, Set.of("A-1", "A-2", "A-3", "A-4")),
                new Channel("channelKey2", "channel 2", "#FFFF99", 2, Set.of())
        );
    }
}
