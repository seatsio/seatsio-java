package seatsio.events;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.charts.SocialDistancingRuleset;
import seatsio.holdTokens.HoldToken;

import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class ChangeObjectStatusForMultipleEventsTest extends SeatsioClientTest {

    @Test
    public void test() {
        String chartKey = createTestChart();
        Event event1 = client.events.create(chartKey);
        Event event2 = client.events.create(chartKey);

        client.events.changeObjectStatus(asList(event1.key, event2.key), newArrayList("A-1", "A-2"), "foo", null, null, null, null, null, null);

        assertThat(client.events.retrieveObjectInfo(event1.key, "A-1").status).isEqualTo("foo");
        assertThat(client.events.retrieveObjectInfo(event1.key, "A-2").status).isEqualTo("foo");
        assertThat(client.events.retrieveObjectInfo(event2.key, "A-1").status).isEqualTo("foo");
        assertThat(client.events.retrieveObjectInfo(event2.key, "A-2").status).isEqualTo("foo");
    }

    @Test
    public void book() {
        String chartKey = createTestChart();
        Event event1 = client.events.create(chartKey);
        Event event2 = client.events.create(chartKey);

        client.events.book(asList(event1.key, event2.key), newArrayList("A-1", "A-2"), null, null, null, null, null, null);

        assertThat(client.events.retrieveObjectInfo(event1.key, "A-1").status).isEqualTo(ObjectInfo.BOOKED);
        assertThat(client.events.retrieveObjectInfo(event1.key, "A-2").status).isEqualTo(ObjectInfo.BOOKED);
        assertThat(client.events.retrieveObjectInfo(event2.key, "A-1").status).isEqualTo(ObjectInfo.BOOKED);
        assertThat(client.events.retrieveObjectInfo(event2.key, "A-2").status).isEqualTo(ObjectInfo.BOOKED);
    }

    @Test
    public void hold() {
        String chartKey = createTestChart();
        Event event1 = client.events.create(chartKey);
        Event event2 = client.events.create(chartKey);
        HoldToken token = client.holdTokens.create();

        client.events.hold(asList(event1.key, event2.key), newArrayList("A-1", "A-2"), token.holdToken, null, null, null, null, null);

        assertThat(client.events.retrieveObjectInfo(event1.key, "A-1").status).isEqualTo(ObjectInfo.HELD);
        assertThat(client.events.retrieveObjectInfo(event1.key, "A-2").status).isEqualTo(ObjectInfo.HELD);
        assertThat(client.events.retrieveObjectInfo(event2.key, "A-1").status).isEqualTo(ObjectInfo.HELD);
        assertThat(client.events.retrieveObjectInfo(event2.key, "A-2").status).isEqualTo(ObjectInfo.HELD);
    }

    @Test
    public void release() {
        String chartKey = createTestChart();
        Event event1 = client.events.create(chartKey);
        Event event2 = client.events.create(chartKey);
        client.events.book(asList(event1.key, event2.key), newArrayList("A-1", "A-2"), null, null, null, null, null, null);

        client.events.release(asList(event1.key, event2.key), newArrayList("A-1", "A-2"), null, null, null, null, null);

        assertThat(client.events.retrieveObjectInfo(event1.key, "A-1").status).isEqualTo(ObjectInfo.FREE);
        assertThat(client.events.retrieveObjectInfo(event1.key, "A-2").status).isEqualTo(ObjectInfo.FREE);
        assertThat(client.events.retrieveObjectInfo(event2.key, "A-1").status).isEqualTo(ObjectInfo.FREE);
        assertThat(client.events.retrieveObjectInfo(event2.key, "A-2").status).isEqualTo(ObjectInfo.FREE);
    }

    @Test
    public void ignoreSocialDistancing() {
        String chartKey = createTestChart();
        Event event1 = client.events.create(chartKey);
        Event event2 = client.events.create(chartKey);
        SocialDistancingRuleset ruleset = SocialDistancingRuleset.fixed("ruleset").withDisabledSeats(newHashSet("A-1")).build();
        Map<String, SocialDistancingRuleset> rulesets = ImmutableMap.of(
                "ruleset", ruleset
        );
        client.charts.saveSocialDistancingRulesets(chartKey, rulesets);
        client.events.update(event1.key, null, null, null, "ruleset");
        client.events.update(event2.key, null, null, null, "ruleset");

        client.events.changeObjectStatus(asList(event1.key, event2.key), newArrayList("A-1"), ObjectInfo.BOOKED, null, null, null, null, null, true);

        assertThat(client.events.retrieveObjectInfo(event1.key, "A-1").status).isEqualTo(ObjectInfo.BOOKED);
        assertThat(client.events.retrieveObjectInfo(event2.key, "A-1").status).isEqualTo(ObjectInfo.BOOKED);
    }

}
