package seatsio.events;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.charts.SocialDistancingRuleset;
import seatsio.holdTokens.HoldToken;
import seatsio.reports.events.EventReportItem;

import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;
import static seatsio.events.ObjectStatus.FREE;

public class ChangeObjectStatusTest extends SeatsioClientTest {

    @Test
    public void test() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        client.events.changeObjectStatus(event.key, newArrayList("A-1", "A-2"), "foo");

        assertThat(client.events.retrieveObjectStatus(event.key, "A-1").status).isEqualTo("foo");
        assertThat(client.events.retrieveObjectStatus(event.key, "A-2").status).isEqualTo("foo");
        assertThat(client.events.retrieveObjectStatus(event.key, "A-3").status).isEqualTo(FREE);
    }

    @Test
    public void objectDetails() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        ChangeObjectStatusResult result = client.events.changeObjectStatus(event.key, newArrayList("A-1"), "foo", null, "order1", null, null, null, null);

        assertThat(result.objects).hasSize(1);
        EventReportItem reportItem = result.objects.get("A-1");
        assertThat(reportItem.status).isEqualTo("foo");
        assertThat(reportItem.label).isEqualTo("A-1");
        assertThat(reportItem.labels).isEqualTo(new Labels("1", "seat", "A", "row"));
        assertThat(reportItem.categoryLabel).isEqualTo("Cat1");
        assertThat(reportItem.categoryKey).isEqualTo("9");
        assertThat(reportItem.ticketType).isNull();
        assertThat(reportItem.orderId).isEqualTo("order1");
        assertThat(reportItem.forSale).isTrue();
        assertThat(reportItem.section).isNull();
        assertThat(reportItem.entrance).isNull();
        assertThat(reportItem.numBooked).isNull();
        assertThat(reportItem.capacity).isNull();
        assertThat(reportItem.objectType).isEqualTo("seat");
        assertThat(reportItem.extraData).isEqualTo(null);
        assertThat(reportItem.leftNeighbour).isNull();
        assertThat(reportItem.rightNeighbour).isEqualTo("A-2");
    }

    @Test
    public void seatInRow() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        ChangeObjectStatusResult result = client.events.changeObjectStatus(event.key, newArrayList("A-1"), "foo");

        assertThat(result.objects).containsOnlyKeys("A-1");
    }

    @Test
    public void seatInTable() {
        String chartKey = createTestChartWithTables();
        Event event = client.events.create(chartKey);

        ChangeObjectStatusResult result = client.events.changeObjectStatus(event.key, newArrayList("T1-1"), "foo");

        assertThat(result.objects).containsOnlyKeys("T1-1");
    }

    @Test
    public void table() {
        String chartKey = createTestChartWithTables();
        Event event = client.events.create(chartKey, null, TableBookingConfig.allByTable());

        ChangeObjectStatusResult result = client.events.changeObjectStatus(event.key, newArrayList("T1"), "foo");

        assertThat(result.objects).containsOnlyKeys("T1");
    }

    @Test
    public void holdToken() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        HoldToken holdToken = client.holdTokens.create();
        client.events.hold(event.key, newArrayList("A-1", "A-2"), holdToken.holdToken);

        client.events.changeObjectStatus(event.key, newArrayList("A-1", "A-2"), "foo", holdToken.holdToken);

        ObjectStatus status1 = client.events.retrieveObjectStatus(event.key, "A-1");
        assertThat(status1.status).isEqualTo("foo");
        assertThat(status1.holdToken).isNull();

        ObjectStatus status2 = client.events.retrieveObjectStatus(event.key, "A-2");
        assertThat(status2.status).isEqualTo("foo");
        assertThat(status2.holdToken).isNull();
    }

    @Test
    public void orderId() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        client.events.changeObjectStatus(event.key, newArrayList("A-1", "A-2"), "foo", null, "order1", null, null, null, null);

        assertThat(client.events.retrieveObjectStatus(event.key, "A-1").orderId).isEqualTo("order1");
        assertThat(client.events.retrieveObjectStatus(event.key, "A-2").orderId).isEqualTo("order1");
    }

    @Test
    public void keepExtraDataTrue() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.updateExtraData(event.key, "A-1", ImmutableMap.of("foo", "bar"));

        client.events.changeObjectStatus(event.key, newArrayList("A-1"), "foo", null, "order1", true, null, null, null);

        assertThat(client.events.retrieveObjectStatus(event.key, "A-1").extraData).isEqualTo(ImmutableMap.of("foo", "bar"));
    }

    @Test
    public void keepExtraDataFalse() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.updateExtraData(event.key, "A-1", ImmutableMap.of("foo", "bar"));

        client.events.changeObjectStatus(event.key, newArrayList("A-1"), "foo", null, "order1", false, null, null, null);

        assertThat(client.events.retrieveObjectStatus(event.key, "A-1").extraData).isNull();
    }

    @Test
    public void keepExtraDataNotPassedIn() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.updateExtraData(event.key, "A-1", ImmutableMap.of("foo", "bar"));

        client.events.changeObjectStatus(event.key, newArrayList("A-1"), "foo", null, "order1", null, null, null, null);

        assertThat(client.events.retrieveObjectStatus(event.key, "A-1").extraData).isNull();
    }

    @Test
    public void ticketType() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        ObjectProperties object1 = new ObjectProperties("A-1", "T1");
        ObjectProperties object2 = new ObjectProperties("A-2", "T2");

        client.events.changeObjectStatus(event.key, newArrayList(object1, object2), "foo");

        ObjectStatus status1 = client.events.retrieveObjectStatus(event.key, "A-1");
        assertThat(status1.status).isEqualTo("foo");
        assertThat(status1.ticketType).isEqualTo("T1");

        ObjectStatus status2 = client.events.retrieveObjectStatus(event.key, "A-2");
        assertThat(status2.status).isEqualTo("foo");
        assertThat(status2.ticketType).isEqualTo("T2");
    }

    @Test
    public void quantity() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        ObjectProperties object1 = new ObjectProperties("GA1", 5);
        ObjectProperties object2 = new ObjectProperties("GA2", 10);

        client.events.changeObjectStatus(event.key, newArrayList(object1, object2), "foo");

        ObjectStatus status1 = client.events.retrieveObjectStatus(event.key, "GA1");
        assertThat(status1.quantity).isEqualTo(5);

        ObjectStatus status2 = client.events.retrieveObjectStatus(event.key, "GA2");
        assertThat(status2.quantity).isEqualTo(10);
    }

    @Test
    public void extraData() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        ObjectProperties object1 = new ObjectProperties("A-1", ImmutableMap.of("foo", "bar"));
        ObjectProperties object2 = new ObjectProperties("A-2", ImmutableMap.of("foo", "baz"));

        client.events.changeObjectStatus(event.key, newArrayList(object1, object2), "foo");

        ObjectStatus status1 = client.events.retrieveObjectStatus(event.key, "A-1");
        assertThat(status1.extraData).isEqualTo(ImmutableMap.of("foo", "bar"));

        ObjectStatus status2 = client.events.retrieveObjectStatus(event.key, "A-2");
        assertThat(status2.extraData).isEqualTo(ImmutableMap.of("foo", "baz"));
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

        client.events.changeObjectStatus(event.key, newArrayList("A-1"), "someStatus", null, null, true, null, newHashSet("channelKey1"), null);

        assertThat(client.events.retrieveObjectStatus(event.key, "A-1").status).isEqualTo("someStatus");
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

        client.events.changeObjectStatus(event.key, newArrayList("A-1"), "someStatus", null, null, true, true, null, null);

        assertThat(client.events.retrieveObjectStatus(event.key, "A-1").status).isEqualTo("someStatus");
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

        client.events.changeObjectStatus(event.key, newArrayList("A-1"), "someStatus", null, null, null, null, null, true);

        assertThat(client.events.retrieveObjectStatus(event.key, "A-1").status).isEqualTo("someStatus");
    }
}
