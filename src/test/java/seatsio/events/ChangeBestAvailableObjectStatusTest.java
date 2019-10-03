package seatsio.events;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.reports.events.EventReportItem;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class ChangeBestAvailableObjectStatusTest extends SeatsioClientTest {

    @Test
    public void number() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        BestAvailableResult bestAvailableResult = client.events.changeObjectStatus(event.key, new BestAvailable(3), "foo");

        assertThat(bestAvailableResult.nextToEachOther).isTrue();
        assertThat(bestAvailableResult.objects).containsOnly("B-4", "B-5", "B-6");
    }

    @Test
    public void objectDetails() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        BestAvailableResult bestAvailableResult = client.events.changeObjectStatus(event.key, new BestAvailable(1), "foo");

        assertThat(bestAvailableResult.objectDetails).hasSize(1);
        EventReportItem reportItem = bestAvailableResult.objectDetails.get("B-5");
        assertThat(reportItem.status).isEqualTo("foo");
        assertThat(reportItem.label).isEqualTo("B-5");
        assertThat(reportItem.labels).isEqualTo(new Labels("5", "seat", "B", "row"));
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
        List<Map<String, Object>> extraData = newArrayList(
                ImmutableMap.of("foo", "bar"),
                ImmutableMap.of("foo", "baz")
        );

        BestAvailableResult bestAvailableResult = client.events.changeObjectStatus(event.key, new BestAvailable(2, null, extraData), "foo");

        assertThat(bestAvailableResult.objects).containsOnly("B-4", "B-5");
        assertThat(client.events.retrieveObjectStatus(event.key, "B-4").extraData).isEqualTo(ImmutableMap.of("foo", "bar"));
        assertThat(client.events.retrieveObjectStatus(event.key, "B-5").extraData).isEqualTo(ImmutableMap.of("foo", "baz"));
    }

    @Test
    public void keepExtraDataTrue() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.updateExtraData(event.key, "B-5", ImmutableMap.of("foo", "bar"));

        BestAvailableResult bestAvailableResult = client.events.changeObjectStatus(event.key, new BestAvailable(1), "foo", null, null, true);

        assertThat(bestAvailableResult.objects).containsOnly("B-5");
        assertThat(client.events.retrieveObjectStatus(event.key, "B-5").extraData).isEqualTo(ImmutableMap.of("foo", "bar"));
    }

    @Test
    public void keepExtraDataFalse() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.updateExtraData(event.key, "B-5", ImmutableMap.of("foo", "bar"));

        BestAvailableResult bestAvailableResult = client.events.changeObjectStatus(event.key, new BestAvailable(1), "foo", null, null, false);

        assertThat(bestAvailableResult.objects).containsOnly("B-5");
        assertThat(client.events.retrieveObjectStatus(event.key, "B-5").extraData).isNull();
    }

    @Test
    public void keepExtraDataNotPassIn() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.updateExtraData(event.key, "B-5", ImmutableMap.of("foo", "bar"));

        BestAvailableResult bestAvailableResult = client.events.changeObjectStatus(event.key, new BestAvailable(1), "foo", null, null, null);

        assertThat(bestAvailableResult.objects).containsOnly("B-5");
        assertThat(client.events.retrieveObjectStatus(event.key, "B-5").extraData).isNull();
    }

}
