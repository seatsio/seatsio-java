package seatsio.events.channels;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.events.Channel;
import seatsio.events.ChannelCreationParams;
import seatsio.events.Event;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class AddChannelTest extends SeatsioClientTest {

    @Test
    public void addChannel() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        client.events.channels.add(event.key, "channelKey1", "channel 1", "#FFFF98", 1, Set.of("A-1", "A-2"));
        client.events.channels.add(event.key, "channelKey2", "channel 2", "#FFFF99", 2, Set.of("A-3"));

        Event retrievedEvent = client.events.retrieve(event.key);
        assertThat(retrievedEvent.channels).containsExactly(
                new Channel("channelKey1", "channel 1", "#FFFF98", 1, Set.of("A-1", "A-2")),
                new Channel("channelKey2", "channel 2", "#FFFF99", 2, Set.of("A-3"))
        );
    }

    @Test
    public void addChannels() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        client.events.channels.add(
                event.key,
                List.of(
                        new ChannelCreationParams.Builder().withKey("channelKey1").withName("channel 1").withColor("#FFFF98").withIndex(1).withObjects(Set.of("A-1", "A-2")).build(),
                        new ChannelCreationParams.Builder().withKey("channelKey2").withName("channel 2").withColor("#FFFF99").withIndex(2).withObjects(Set.of("A-3")).build()
                )
        );

        Event retrievedEvent = client.events.retrieve(event.key);
        assertThat(retrievedEvent.channels).containsExactly(
                new Channel("channelKey1", "channel 1", "#FFFF98", 1, Set.of("A-1", "A-2")),
                new Channel("channelKey2", "channel 2", "#FFFF99", 2, Set.of("A-3"))
        );
    }

    @Test
    public void indexIsOptional() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        client.events.channels.add(event.key, "channelKey1", "channel 1", "#FFFF98", null, Set.of("A-1", "A-2"));

        Event retrievedEvent = client.events.retrieve(event.key);
        assertThat(retrievedEvent.channels).containsExactly(
                new Channel("channelKey1", "channel 1", "#FFFF98", null, Set.of("A-1", "A-2"))
        );
    }

    @Test
    public void objectsAreOptional() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        client.events.channels.add(event.key, "channelKey1", "channel 1", "#FFFF98", 1, null);

        Event retrievedEvent = client.events.retrieve(event.key);
        assertThat(retrievedEvent.channels).containsExactly(
                new Channel("channelKey1", "channel 1", "#FFFF98", 1, Set.of())
        );
    }

}
