package seatsio.events;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.SeatsioException;
import seatsio.holdTokens.HoldToken;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static seatsio.events.EventObjectInfo.FREE;
import static seatsio.events.EventObjectInfo.RESALE;

public class ChangeObjectStatusTest extends SeatsioClientTest {

    @Test
    public void test() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        client.events.changeObjectStatus(event.key, List.of("A-1", "A-2"), "foo");

        assertThat(client.events.retrieveObjectInfo(event.key, "A-1").status).isEqualTo("foo");
        assertThat(client.events.retrieveObjectInfo(event.key, "A-2").status).isEqualTo("foo");
        assertThat(client.events.retrieveObjectInfo(event.key, "A-3").status).isEqualTo(FREE);
    }

    @Test
    public void objectDetails() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        ChangeObjectStatusResult result = client.events.changeObjectStatus(event.key, List.of("A-1"), "foo", null, "order1", null, null, null);

        assertThat(result.objects).hasSize(1);
        EventObjectInfo reportItem = result.objects.get("A-1");
        assertThat(reportItem.status).isEqualTo("foo");
        assertThat(reportItem.label).isEqualTo("A-1");
        assertThat(reportItem.labels).isEqualTo(new Labels("1", "seat", "A", "row"));
        assertThat(reportItem.ids).isEqualTo(new IDs("1", "A", null));
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

        ChangeObjectStatusResult result = client.events.changeObjectStatus(event.key, List.of("A-1"), "foo");

        assertThat(result.objects).containsOnlyKeys("A-1");
    }

    @Test
    public void seatInTable() {
        String chartKey = createTestChartWithTables();
        Event event = client.events.create(chartKey);

        ChangeObjectStatusResult result = client.events.changeObjectStatus(event.key, List.of("T1-1"), "foo");

        assertThat(result.objects).containsOnlyKeys("T1-1");
    }

    @Test
    public void table() {
        String chartKey = createTestChartWithTables();
        Event event = client.events.create(chartKey, new CreateEventParams().withTableBookingConfig(TableBookingConfig.allByTable()));

        ChangeObjectStatusResult result = client.events.changeObjectStatus(event.key, List.of("T1"), "foo");

        assertThat(result.objects).containsOnlyKeys("T1");
    }

    @Test
    public void holdToken() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        HoldToken holdToken = client.holdTokens.create();
        client.events.hold(event.key, List.of("A-1", "A-2"), holdToken.holdToken);

        client.events.changeObjectStatus(event.key, List.of("A-1", "A-2"), "foo", holdToken.holdToken);

        EventObjectInfo info1 = client.events.retrieveObjectInfo(event.key, "A-1");
        assertThat(info1.status).isEqualTo("foo");
        assertThat(info1.holdToken).isNull();

        EventObjectInfo info2 = client.events.retrieveObjectInfo(event.key, "A-2");
        assertThat(info2.status).isEqualTo("foo");
        assertThat(info2.holdToken).isNull();
    }

    @Test
    public void orderId() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        client.events.changeObjectStatus(event.key, List.of("A-1", "A-2"), "foo", null, "order1", null, null, null);

        assertThat(client.events.retrieveObjectInfo(event.key, "A-1").orderId).isEqualTo("order1");
        assertThat(client.events.retrieveObjectInfo(event.key, "A-2").orderId).isEqualTo("order1");
    }

    @Test
    public void keepExtraDataTrue() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.updateExtraData(event.key, "A-1", Map.of("foo", "bar"));

        client.events.changeObjectStatus(event.key, List.of("A-1"), "foo", null, "order1", true, null, null);

        assertThat(client.events.retrieveObjectInfo(event.key, "A-1").extraData).isEqualTo(Map.of("foo", "bar"));
    }

    @Test
    public void keepExtraDataFalse() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.updateExtraData(event.key, "A-1", Map.of("foo", "bar"));

        client.events.changeObjectStatus(event.key, List.of("A-1"), "foo", null, "order1", false, null, null);

        assertThat(client.events.retrieveObjectInfo(event.key, "A-1").extraData).isNull();
    }

    @Test
    public void keepExtraDataNotPassedIn() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.updateExtraData(event.key, "A-1", Map.of("foo", "bar"));

        client.events.changeObjectStatus(event.key, List.of("A-1"), "foo", null, "order1", null, null, null);

        assertThat(client.events.retrieveObjectInfo(event.key, "A-1").extraData).isNull();
    }

    @Test
    public void ticketType() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        ObjectProperties object1 = new ObjectProperties("A-1", "T1");
        ObjectProperties object2 = new ObjectProperties("A-2", "T2");

        client.events.changeObjectStatus(event.key, List.of(object1, object2), "foo");

        EventObjectInfo info1 = client.events.retrieveObjectInfo(event.key, "A-1");
        assertThat(info1.status).isEqualTo("foo");
        assertThat(info1.ticketType).isEqualTo("T1");

        EventObjectInfo info2 = client.events.retrieveObjectInfo(event.key, "A-2");
        assertThat(info2.status).isEqualTo("foo");
        assertThat(info2.ticketType).isEqualTo("T2");
    }

    @Test
    public void quantity() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        ObjectProperties object1 = new ObjectProperties("GA1", 5);
        ObjectProperties object2 = new ObjectProperties("GA2", 10);

        client.events.changeObjectStatus(event.key, List.of(object1, object2), "foo");

        EventObjectInfo info1 = client.events.retrieveObjectInfo(event.key, "GA1");
        assertThat(info1.numBooked).isEqualTo(5);

        EventObjectInfo status2 = client.events.retrieveObjectInfo(event.key, "GA2");
        assertThat(status2.numBooked).isEqualTo(10);
    }

    @Test
    public void extraData() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        ObjectProperties object1 = new ObjectProperties("A-1", Map.of("foo", "bar"));
        ObjectProperties object2 = new ObjectProperties("A-2", Map.of("foo", "baz"));

        client.events.changeObjectStatus(event.key, List.of(object1, object2), "foo");

        EventObjectInfo status1 = client.events.retrieveObjectInfo(event.key, "A-1");
        assertThat(status1.extraData).isEqualTo(Map.of("foo", "bar"));

        EventObjectInfo status2 = client.events.retrieveObjectInfo(event.key, "A-2");
        assertThat(status2.extraData).isEqualTo(Map.of("foo", "baz"));
    }

    @Test
    public void channelKeys() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey, new CreateEventParams().withChannels(List.of(
                new Channel("channelKey1", "channel 1", "#FFFF99", 1, Set.of("A-1", "A-2"))
        )));

        client.events.changeObjectStatus(event.key, List.of("A-1"), "someStatus", null, null, true, null, Set.of("channelKey1"));

        assertThat(client.events.retrieveObjectInfo(event.key, "A-1").status).isEqualTo("someStatus");
    }

    @Test
    public void ignoreChannels() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey, new CreateEventParams().withChannels(List.of(
                new Channel("channelKey1", "channel 1", "#FFFF99", 1, Set.of("A-1", "A-2"))
        )));

        client.events.changeObjectStatus(event.key, List.of("A-1"), "someStatus", null, null, null, true, null);

        assertThat(client.events.retrieveObjectInfo(event.key, "A-1").status).isEqualTo("someStatus");
    }

    @Test
    public void allowedPreviousStatus() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        try {
            client.events.changeObjectStatus(event.key, List.of("A-1"), "someStatus", null, null, null, null, null, Set.of("onlyAllowedPreviousStatus"), null, null);
            fail("expected exception");
        } catch (SeatsioException e) {
            assertThat(e.errors).hasSize(1);
            assertThat(e.errors.get(0).getCode()).isEqualTo("ILLEGAL_STATUS_CHANGE");
        }
    }

    @Test
    public void rejectedPreviousStatus() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        try {
            client.events.changeObjectStatus(event.key, List.of("A-1"), "someStatus", null, null, null, null, null, null, Set.of("free"), null);
            fail("expected exception");
        } catch (SeatsioException e) {
            assertThat(e.errors).hasSize(1);
            assertThat(e.errors.get(0).getCode()).isEqualTo("ILLEGAL_STATUS_CHANGE");
        }
    }

    @Test
    public void resaleListingId() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        client.events.changeObjectStatus(event.key, List.of("A-1"), RESALE, null, null, null, null, null, null, null, "listing1");

        assertThat(client.events.retrieveObjectInfo(event.key, "A-1").resaleListingId).isEqualTo("listing1");
    }
}
