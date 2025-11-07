package seatsio.events.channels;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.events.Channel;
import seatsio.events.Event;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class UpdateChannelTest extends SeatsioClientTest {

    @Test
    public void updateName() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.channels.add(event.key(), "channelKey1", "channel 1", "#FFFF98", 1, Set.of("A-1", "A-2"));

        client.events.channels.update(event.key(), "channelKey1", "channel 2", null, null);

        Event retrievedEvent = client.events.retrieve(event.key());
        assertThat(retrievedEvent.channels()).containsExactly(
                new Channel("channelKey1", "channel 2", "#FFFF98", 1, Set.of("A-1", "A-2"))
        );
    }

    @Test
    public void updateColor() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.channels.add(event.key(), "channelKey1", "channel 1", "#FFFF98", 1, Set.of("A-1", "A-2"));

        client.events.channels.update(event.key(), "channelKey1", null, "#FFFF99", null);

        Event retrievedEvent = client.events.retrieve(event.key());
        assertThat(retrievedEvent.channels()).containsExactly(
                new Channel("channelKey1", "channel 1", "#FFFF99", 1, Set.of("A-1", "A-2"))
        );
    }

    @Test
    public void updateObjects() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.channels.add(event.key(), "channelKey1", "channel 1", "#FFFF98", 1, Set.of("A-1", "A-2"));

        client.events.channels.update(event.key(), "channelKey1", null, null, Set.of("A-3"));

        Event retrievedEvent = client.events.retrieve(event.key());
        assertThat(retrievedEvent.channels()).containsExactly(
                new Channel("channelKey1", "channel 1", "#FFFF98", 1, Set.of("A-3"))
        );
    }
}
