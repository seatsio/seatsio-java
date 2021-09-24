package seatsio.events;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.charts.SocialDistancingRuleset;
import seatsio.holdTokens.HoldToken;

import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;
import static seatsio.events.ObjectInfo.HELD;

public class HoldObjectsTest extends SeatsioClientTest {

    @Test
    public void holdToken() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        HoldToken holdToken = client.holdTokens.create();

        client.events.hold(event.key, newArrayList("A-1", "A-2"), holdToken.holdToken);

        ObjectInfo status1 = client.events.retrieveObjectInfo(event.key, "A-1");
        assertThat(status1.status).isEqualTo(HELD);
        assertThat(status1.holdToken).isEqualTo(holdToken.holdToken);

        ObjectInfo status2 = client.events.retrieveObjectInfo(event.key, "A-2");
        assertThat(status2.status).isEqualTo(HELD);
        assertThat(status2.holdToken).isEqualTo(holdToken.holdToken);
    }

    @Test
    public void orderId() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        HoldToken holdToken = client.holdTokens.create();

        client.events.hold(event.key, newArrayList("A-1", "A-2"), holdToken.holdToken, "order1", null, null, null, null);

        assertThat(client.events.retrieveObjectInfo(event.key, "A-1").orderId).isEqualTo("order1");
        assertThat(client.events.retrieveObjectInfo(event.key, "A-2").orderId).isEqualTo("order1");
    }

    @Test
    public void keepExtraData() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        HoldToken holdToken = client.holdTokens.create();
        client.events.updateExtraData(event.key, "A-1", ImmutableMap.of("foo", "bar"));

        client.events.hold(event.key, newArrayList("A-1"), holdToken.holdToken, null, true, null, null, null);

        assertThat(client.events.retrieveObjectInfo(event.key, "A-1").extraData).isEqualTo(ImmutableMap.of("foo", "bar"));
    }

    @Test
    public void labels() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        HoldToken holdToken = client.holdTokens.create();

        ChangeObjectStatusResult result = client.events.hold(event.key, newArrayList("A-1", "A-2"), holdToken.holdToken);

        assertThat(result.objects).containsOnlyKeys("A-1", "A-2");
    }

    @Test
    public void channelKeys() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        HoldToken holdToken = client.holdTokens.create();
        client.events.updateChannels(event.key, ImmutableMap.of(
                "channelKey1", new Channel("channel 1", "#FFFF99", 1)
        ));
        client.events.assignObjectsToChannel(event.key, ImmutableMap.of(
                "channelKey1", newHashSet("A-1", "A-2")
        ));

        client.events.hold(event.key, newArrayList("A-1"), holdToken.holdToken, null, true, null, newHashSet("channelKey1"), null);

        assertThat(client.events.retrieveObjectInfo(event.key, "A-1").status).isEqualTo(HELD);
    }

    @Test
    public void ignoreChannels() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        HoldToken holdToken = client.holdTokens.create();
        client.events.updateChannels(event.key, ImmutableMap.of(
                "channelKey1", new Channel("channel 1", "#FFFF99", 1)
        ));
        client.events.assignObjectsToChannel(event.key, ImmutableMap.of(
                "channelKey1", newHashSet("A-1", "A-2")
        ));

        client.events.hold(event.key, newArrayList("A-1"), holdToken.holdToken, null, true, true, null, null);

        assertThat(client.events.retrieveObjectInfo(event.key, "A-1").status).isEqualTo(HELD);
    }

    @Test
    public void ignoreSocialDistancing() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        HoldToken holdToken = client.holdTokens.create();
        SocialDistancingRuleset ruleset = SocialDistancingRuleset.fixed("ruleset").withDisabledSeats(newHashSet("A-1")).build();
        Map<String, SocialDistancingRuleset> rulesets = ImmutableMap.of(
                "ruleset", ruleset
        );
        client.charts.saveSocialDistancingRulesets(chartKey, rulesets);
        client.events.update(event.key, null, null, null, "ruleset");

        client.events.hold(event.key, newArrayList("A-1"), holdToken.holdToken, null, null, null, null, true);

        assertThat(client.events.retrieveObjectInfo(event.key, "A-1").status).isEqualTo(HELD);
    }
}
