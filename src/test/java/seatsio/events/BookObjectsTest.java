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
import static seatsio.events.ObjectStatus.BOOKED;
import static seatsio.events.ObjectStatus.FREE;

public class BookObjectsTest extends SeatsioClientTest {

    @Test
    public void test() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        ChangeObjectStatusResult result = client.events.book(event.key, newArrayList("A-1", "A-2"));

        assertThat(client.events.retrieveObjectStatus(event.key, "A-1").status).isEqualTo(BOOKED);
        assertThat(client.events.retrieveObjectStatus(event.key, "A-2").status).isEqualTo(BOOKED);
        assertThat(client.events.retrieveObjectStatus(event.key, "A-3").status).isEqualTo(FREE);
        assertThat(result.objects).containsOnlyKeys("A-1", "A-2");
    }

    @Test
    public void sections() {
        String chartKey = createTestChartWithSections();
        Event event = client.events.create(chartKey);

        ChangeObjectStatusResult result = client.events.book(event.key, newArrayList("Section A-A-1", "Section A-A-2"));

        assertThat(client.events.retrieveObjectStatus(event.key, "Section A-A-1").status).isEqualTo(BOOKED);
        assertThat(client.events.retrieveObjectStatus(event.key, "Section A-A-2").status).isEqualTo(BOOKED);
        assertThat(client.events.retrieveObjectStatus(event.key, "Section A-A-3").status).isEqualTo(FREE);

        assertThat(result.objects).containsOnlyKeys("Section A-A-1", "Section A-A-2");
        assertThat(result.objects.get("Section A-A-1").entrance).isEqualTo("Entrance 1");
        assertThat(result.objects.get("Section A-A-1").section).isEqualTo("Section A");
        assertThat(result.objects.get("Section A-A-1").labels).isEqualTo(new Labels("1", "seat", "A", "row", "Section A"));
        assertThat(result.objects.get("Section A-A-1").ids).isEqualTo(new IDs("1", "A", "Section A"));
    }

    @Test
    public void holdToken() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        HoldToken holdToken = client.holdTokens.create();
        client.events.hold(event.key, newArrayList("A-1", "A-2"), holdToken.holdToken);

        client.events.book(event.key, newArrayList("A-1", "A-2"), holdToken.holdToken);

        ObjectStatus status1 = client.events.retrieveObjectStatus(event.key, "A-1");
        assertThat(status1.status).isEqualTo(BOOKED);
        assertThat(status1.holdToken).isNull();

        ObjectStatus status2 = client.events.retrieveObjectStatus(event.key, "A-2");
        assertThat(status2.status).isEqualTo(BOOKED);
        assertThat(status2.holdToken).isNull();
    }

    @Test
    public void orderId() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        client.events.book(event.key, newArrayList("A-1", "A-2"), null, "order1", null, null, null, null);

        assertThat(client.events.retrieveObjectStatus(event.key, "A-1").orderId).isEqualTo("order1");
        assertThat(client.events.retrieveObjectStatus(event.key, "A-2").orderId).isEqualTo("order1");
    }

    @Test
    public void keepExtraData() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.updateExtraData(event.key, "A-1", ImmutableMap.of("foo", "bar"));

        client.events.book(event.key, newArrayList("A-1"), null, null, true, null, null, null);

        assertThat(client.events.retrieveObjectStatus(event.key, "A-1").extraData).isEqualTo(ImmutableMap.of("foo", "bar"));
    }

    @Test
    public void channelKeys() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.updateChannels(event.key, ImmutableMap.of(
                "channelKey1", new Channel("channel 1", "#FFFF99", 1)
        ));
        client.events.assignObjectsToChannel(event.key, ImmutableMap.of(
                "channelKey1", newHashSet("A-1", "A-2")
        ));

        client.events.book(event.key, newArrayList("A-1"), null, null, true, null, newHashSet("channelKey1"), null);

        assertThat(client.events.retrieveObjectStatus(event.key, "A-1").status).isEqualTo(BOOKED);
    }

    @Test
    public void ignoreChannels() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.updateChannels(event.key, ImmutableMap.of(
                "channelKey1", new Channel("channel 1", "#FFFF99", 1)
        ));
        client.events.assignObjectsToChannel(event.key, ImmutableMap.of(
                "channelKey1", newHashSet("A-1", "A-2")
        ));

        client.events.book(event.key, newArrayList("A-1"), null, null, true, true, null, null);

        assertThat(client.events.retrieveObjectStatus(event.key, "A-1").status).isEqualTo(BOOKED);
    }

    @Test
    public void ignoreSocialDistancing() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        SocialDistancingRuleset ruleset = SocialDistancingRuleset.fixed("ruleset").withDisabledSeats(newHashSet("A-1")).build();
        Map<String, SocialDistancingRuleset> rulesets = ImmutableMap.of(
                "ruleset", ruleset
        );
        client.charts.saveSocialDistancingRulesets(chartKey, rulesets);
        client.events.update(event.key, null, null, null, "ruleset");

        client.events.book(event.key, newArrayList("A-1"), null, null, null, null, null, true);

        assertThat(client.events.retrieveObjectStatus(event.key, "A-1").status).isEqualTo(BOOKED);
    }

}
