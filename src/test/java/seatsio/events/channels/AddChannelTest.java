package seatsio.events.channels;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.events.Channel;
import seatsio.events.Event;

import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;
import static seatsio.events.ChannelCreationParamsBuilder.aChannel;

public class AddChannelTest extends SeatsioClientTest {

    @Test
    public void addChannel() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        client.events.channels.add(event.key, "channelKey1", "channel 1", "#FFFF98", 1, newHashSet("A-1", "A-2"));
        client.events.channels.add(event.key, "channelKey2", "channel 2", "#FFFF99", 2, newHashSet("A-3"));

        Event retrievedEvent = client.events.retrieve(event.key);
        assertThat(retrievedEvent.channels).containsExactly(
                new Channel("channelKey1", "channel 1", "#FFFF98", 1, newHashSet("A-1", "A-2")),
                new Channel("channelKey2", "channel 2", "#FFFF99", 2, newHashSet("A-3"))
        );
    }

    @Test
    public void addChannels_builderVararg() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        client.events.channels.add(
                event.key,
                aChannel().withKey("channelKey1").withName("channel 1").withColor("#FFFF98").withIndex(1).withObjects(newHashSet("A-1", "A-2")),
                aChannel().withKey("channelKey2").withName("channel 2").withColor("#FFFF99").withIndex(2).withObjects(newHashSet("A-3"))
        );

        Event retrievedEvent = client.events.retrieve(event.key);
        assertThat(retrievedEvent.channels).containsExactly(
                new Channel("channelKey1", "channel 1", "#FFFF98", 1, newHashSet("A-1", "A-2")),
                new Channel("channelKey2", "channel 2", "#FFFF99", 2, newHashSet("A-3"))
        );
    }

    @Test
    public void addChannels_paramsVararg() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        client.events.channels.add(
                event.key,
                aChannel().withKey("channelKey1").withName("channel 1").withColor("#FFFF98").withIndex(1).withObjects(newHashSet("A-1", "A-2")).build(),
                aChannel().withKey("channelKey2").withName("channel 2").withColor("#FFFF99").withIndex(2).withObjects(newHashSet("A-3")).build()
        );

        Event retrievedEvent = client.events.retrieve(event.key);
        assertThat(retrievedEvent.channels).containsExactly(
                new Channel("channelKey1", "channel 1", "#FFFF98", 1, newHashSet("A-1", "A-2")),
                new Channel("channelKey2", "channel 2", "#FFFF99", 2, newHashSet("A-3"))
        );
    }

    @Test
    public void indexIsOptional() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        client.events.channels.add(event.key, "channelKey1", "channel 1", "#FFFF98", null, newHashSet("A-1", "A-2"));

        Event retrievedEvent = client.events.retrieve(event.key);
        assertThat(retrievedEvent.channels).containsExactly(
                new Channel("channelKey1", "channel 1", "#FFFF98", 0, newHashSet("A-1", "A-2"))
        );
    }

    @Test
    public void objectsAreOptional() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        client.events.channels.add(event.key, "channelKey1", "channel 1", "#FFFF98", 1, null);

        Event retrievedEvent = client.events.retrieve(event.key);
        assertThat(retrievedEvent.channels).containsExactly(
                new Channel("channelKey1", "channel 1", "#FFFF98", 1, newHashSet())
        );
    }

}
