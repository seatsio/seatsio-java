package seatsio.events;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static seatsio.events.BestAvailableBuilder.someBestAvailable;

public class ChangeBestAvailableObjectStatusTest extends SeatsioClientTest {

    @Test
    public void number() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        BestAvailableResult bestAvailableResult = client.events.changeObjectStatus(event.key, new BestAvailable(3), "foo");

        assertThat(bestAvailableResult.nextToEachOther).isTrue();
        assertThat(bestAvailableResult.objects).containsOnly("A-4", "A-5", "A-6");
    }

    @Test
    public void objectDetails() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        BestAvailableResult bestAvailableResult = client.events.changeObjectStatus(event.key, new BestAvailable(1), "foo");

        assertThat(bestAvailableResult.objectDetails).hasSize(1);
        EventObjectInfo reportItem = bestAvailableResult.objectDetails.get("A-5");
        assertThat(reportItem.status).isEqualTo("foo");
        assertThat(reportItem.label).isEqualTo("A-5");
        assertThat(reportItem.labels).isEqualTo(new Labels("5", "seat", "A", "row"));
        assertThat(reportItem.ids).isEqualTo(new IDs("5", "A", null));
        assertThat(reportItem.categoryLabel).isEqualTo("Cat1");
        assertThat(reportItem.categoryKey).isEqualTo("9");
        assertThat(reportItem.ticketType).isNull();
        assertThat(reportItem.orderId).isNull();
        assertThat(reportItem.forSale).isTrue();
        assertThat(reportItem.section).isNull();
        assertThat(reportItem.entrance).isNull();
        assertThat(reportItem.numBooked).isNull();
        assertThat(reportItem.capacity).isNull();
        assertThat(reportItem.objectType).isEqualTo("seat");
        assertThat(reportItem.extraData).isEqualTo(null);
        assertThat(reportItem.leftNeighbour).isEqualTo("A-4");
        assertThat(reportItem.rightNeighbour).isEqualTo("A-6");
    }

    @Test
    public void categories() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        BestAvailableResult bestAvailableResult = client.events.changeObjectStatus(event.key, new BestAvailable(3, asList("cat2")), "foo");

        assertThat(bestAvailableResult.objects).containsOnly("C-4", "C-5", "C-6");
    }

    @Test
    public void extraData() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        List<Map<String, Object>> extraData = List.of(
                Map.of("foo", "bar"),
                Map.of("foo", "baz")
        );

        BestAvailableResult bestAvailableResult = client.events.changeObjectStatus(event.key, someBestAvailable().withNumber(2).withExtraData(extraData).build(), "foo");

        assertThat(bestAvailableResult.objects).containsOnly("A-4", "A-5");
        assertThat(client.events.retrieveObjectInfo(event.key, "A-4").extraData).isEqualTo(Map.of("foo", "bar"));
        assertThat(client.events.retrieveObjectInfo(event.key, "A-5").extraData).isEqualTo(Map.of("foo", "baz"));
    }

    @Test
    public void ticketTypes() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        BestAvailableResult bestAvailableResult = client.events.changeObjectStatus(event.key, someBestAvailable().withNumber(2).withTicketTypes(List.of("adult", "child")).build(), "foo");

        assertThat(bestAvailableResult.objects).containsOnly("A-4", "A-5");
        assertThat(client.events.retrieveObjectInfo(event.key, "A-4").ticketType).isEqualTo("adult");
        assertThat(client.events.retrieveObjectInfo(event.key, "A-5").ticketType).isEqualTo("child");
    }

    @Test
    public void preventsOrphanSeatsByDefault() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.book(event.key, List.of("A-4", "A-5"));

        BestAvailableResult bestAvailableResult = client.events.changeObjectStatus(event.key, new BestAvailable(2), "foo");

        assertThat(bestAvailableResult.objects).containsOnly("B-4", "B-5");
    }

    @Test
    public void dontTryToPreventOrphanSeats() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.book(event.key, List.of("A-4", "A-5"));

        BestAvailableResult bestAvailableResult = client.events.changeObjectStatus(event.key, someBestAvailable().withNumber(2).withTryToPreventOrphanSeats(false).build(), "foo");

        assertThat(bestAvailableResult.objects).containsOnly("A-2", "A-3");
    }

    @Test
    public void keepExtraDataTrue() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.updateExtraData(event.key, "A-5", Map.of("foo", "bar"));

        BestAvailableResult bestAvailableResult = client.events.changeObjectStatus(event.key, new BestAvailable(1), "foo", null, null, true, null, null);

        assertThat(bestAvailableResult.objects).containsOnly("A-5");
        assertThat(client.events.retrieveObjectInfo(event.key, "A-5").extraData).isEqualTo(Map.of("foo", "bar"));
    }

    @Test
    public void keepExtraDataFalse() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.updateExtraData(event.key, "A-5", Map.of("foo", "bar"));

        BestAvailableResult bestAvailableResult = client.events.changeObjectStatus(event.key, new BestAvailable(1), "foo", null, null, false, null, null);

        assertThat(bestAvailableResult.objects).containsOnly("A-5");
        assertThat(client.events.retrieveObjectInfo(event.key, "A-5").extraData).isNull();
    }

    @Test
    public void keepExtraDataNotPassIn() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.updateExtraData(event.key, "A-5", Map.of("foo", "bar"));

        BestAvailableResult bestAvailableResult = client.events.changeObjectStatus(event.key, new BestAvailable(1), "foo", null, null, null, null, null);

        assertThat(bestAvailableResult.objects).containsOnly("A-5");
        assertThat(client.events.retrieveObjectInfo(event.key, "A-5").extraData).isNull();
    }

    @Test
    public void channelKeys() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey, new CreateEventParams().withChannels(List.of(
                new Channel("channelKey1", "channel 1", "#FFFF99", 1, Set.of("B-6"))
        )));

        BestAvailableResult bestAvailableResult = client.events.changeObjectStatus(event.key, new BestAvailable(1), "foo", null, null, null, null, Set.of("channelKey1"));

        assertThat(bestAvailableResult.objects).containsOnly("B-6");
    }

    @Test
    public void ignoreChannels() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey, new CreateEventParams().withChannels(List.of(
                new Channel("channelKey1", "channel 1", "#FFFF99", 1, Set.of("A-5"))
        )));

        BestAvailableResult bestAvailableResult = client.events.changeObjectStatus(event.key, new BestAvailable(1), "foo", null, null, null, true, null);

        assertThat(bestAvailableResult.objects).containsOnly("A-5");
    }

}
