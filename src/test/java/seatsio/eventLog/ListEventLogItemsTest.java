package seatsio.eventLog;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.charts.Chart;
import seatsio.events.CreateEventParams;
import seatsio.events.ForSaleConfigParams;
import seatsio.util.Page;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ListEventLogItemsTest extends SeatsioClientTest {

    @Test
    public void listAll() throws InterruptedException {
        Chart chart = client.charts.create();
        client.charts.update(chart.key, "a chart");

        waitUntilEventLogItemsAvailable();

        Stream<EventLogItem> eventLogItems = client.eventLog.listAll();

        assertThat(eventLogItems)
                .extracting(eventLogItem -> eventLogItem.type)
                .containsExactly("chart.created", "chart.published");
    }

    @Test
    public void properties() throws InterruptedException {
        Chart chart = client.charts.create();
        client.charts.update(chart.key, "a chart");

        waitUntilEventLogItemsAvailable();

        Stream<EventLogItem> eventLogItems = client.eventLog.listAll();

        EventLogItem eventLogItem = eventLogItems.findFirst().get();
        assertThat(eventLogItem.type).isEqualTo("chart.created");
        assertThat(eventLogItem.date).isNotNull();
        assertThat(eventLogItem.workspaceKey).isEqualTo(workspace.key);
        assertThat(eventLogItem.data).isEqualTo(Map.of("key", chart.key));
        assertThat(eventLogItem.id).isGreaterThan(0);
    }

    @Test
    public void listFirstPage() throws InterruptedException {
        Chart chart = client.charts.create();
        client.charts.update(chart.key, "a chart");
        client.events.create(chart.key, new CreateEventParams().withForSaleConfigParams(new ForSaleConfigParams(false)));

        waitUntilEventLogItemsAvailable();

        Page<EventLogItem> eventLogItems = client.eventLog.listFirstPage(2);

        assertThat(eventLogItems.items)
                .extracting(eventLogItem -> eventLogItem.type)
                .containsExactly("chart.created", "chart.published");
    }

    @Test
    public void listPageAfter() throws InterruptedException {
        Chart chart = client.charts.create();
        client.charts.update(chart.key, "a chart");
        client.events.create(chart.key, new CreateEventParams().withForSaleConfigParams(new ForSaleConfigParams(false)));

        waitUntilEventLogItemsAvailable();

        Page<EventLogItem> eventLogItems1 = client.eventLog.listPageAfter(0, 2);
        assertThat(eventLogItems1.items)
                .extracting(eventLogItem -> eventLogItem.type)
                .containsExactly("chart.created", "chart.published");

        Page<EventLogItem> eventLogItems2 = client.eventLog.listPageAfter(eventLogItems1.nextPageStartsAfter.get(), 2);
        assertThat(eventLogItems2.items)
                .extracting(eventLogItem -> eventLogItem.type)
                .containsExactly("event.for.sale.config.updated");

    }

    private static void waitUntilEventLogItemsAvailable() throws InterruptedException {
        Thread.sleep(2000);
    }
}
