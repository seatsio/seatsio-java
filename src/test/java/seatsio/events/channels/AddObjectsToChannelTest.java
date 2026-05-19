package seatsio.events.channels;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.events.Channel;
import seatsio.events.Event;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class AddObjectsToChannelTest extends SeatsioClientTest {

    @Test
    public void addObjects() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.channels.add(event.key(), "channelKey1", "channel 1", "#FFFF98", 1, Set.of("A-1", "A-2"));
        client.events.channels.add(event.key(), "channelKey2", "channel 2", "#FFFF99", 2, Set.of("A-3", "A-4"));

        client.events.channels.addObjects(event.key(), "channelKey1", Set.of("A-3", "A-4"));

        Event retrievedEvent = client.events.retrieve(event.key());
        assertThat(retrievedEvent.channels()).containsExactly(
                new Channel("channelKey1", "channel 1", "#FFFF98", 1, Set.of("A-1", "A-2", "A-3", "A-4")),
                new Channel("channelKey2", "channel 2", "#FFFF99", 2, Set.of())
        );
    }

    @Test
    public void addAreaPlaces() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.channels.add(event.key(), "channelKey1", "channel 1", "#FFFF98", 1, Set.of(), Map.of("GA1", 10));
        client.events.channels.add(event.key(), "channelKey2", "channel 2", "#FFFF99", 2, Set.of(), Map.of("GA1", 5));

        client.events.channels.addObjects(event.key(), "channelKey1", null, Map.of("GA1", 20));

        Event retrievedEvent = client.events.retrieve(event.key());
        assertThat(retrievedEvent.channels()).containsExactly(
                new Channel("channelKey1", "channel 1", "#FFFF98", 1, Set.of(), Map.of("GA1", 30)),
                new Channel("channelKey2", "channel 2", "#FFFF99", 2, Set.of(), Map.of("GA1", 5))
        );
    }

    @Test
    public void addBothObjectsAndAreaPlaces() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.channels.add(event.key(), "channelKey1", "channel 1", "#FFFF98", 1, Set.of("A-1"), Map.of("GA1", 10));

        client.events.channels.addObjects(event.key(), "channelKey1", Set.of("A-2", "A-3"), Map.of("GA1", 5));

        Event retrievedEvent = client.events.retrieve(event.key());
        assertThat(retrievedEvent.channels()).containsExactly(
                new Channel("channelKey1", "channel 1", "#FFFF98", 1, Set.of("A-1", "A-2", "A-3"), Map.of("GA1", 15))
        );
    }
}
