package seatsio.events.channels;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.events.Channel;
import seatsio.events.Event;

import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;


public class SetObjectsForChannelsTest extends SeatsioClientTest {

    @Test
    public void setObjects() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        Channel channel1 = new Channel("channel 1", "#FFFF99", 1);
        Channel channel2 = new Channel("channel 2", "#FFFF99", 2);
        client.events.channels.replace(event.key, ImmutableMap.of(
                "channelKey1", channel1,
                "channelKey2", channel2
        ));

        client.events.channels.setObjects(event.key, ImmutableMap.of(
                "channelKey1", newHashSet("A-1", "A-2"),
                "channelKey2", newHashSet("A-3")
        ));

        Channel[] channels = client.events.retrieve(event.key).channels;
        assertThat(channels[0].objects).containsExactly("A-1", "A-2");
        assertThat(channels[1].objects).containsExactly("A-3");
    }

}
