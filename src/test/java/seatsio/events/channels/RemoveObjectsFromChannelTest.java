package seatsio.events.channels;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.events.Channel;
import seatsio.events.Event;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class RemoveObjectsFromChannelTest extends SeatsioClientTest {

    @Test
    public void test() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.channels.add(event.key, "channelKey1", "channel 1", "#FFFF98", 1, Set.of("A-1", "A-2", "A-3", "A-4"));

        client.events.channels.removeObjects(event.key, "channelKey1", Set.of("A-3", "A-4"));

        Event retrievedEvent = client.events.retrieve(event.key);
        assertThat(retrievedEvent.channels).containsExactly(
                new Channel("channelKey1", "channel 1", "#FFFF98", 1, Set.of("A-1", "A-2"))
        );
    }
}
