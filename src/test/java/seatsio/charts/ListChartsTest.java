package seatsio.charts;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.events.Event;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ListChartsTest extends SeatsioClientTest {

    @Test
    public void all() {
        Chart chart1 = client.charts.create();
        Chart chart2 = client.charts.create();
        Chart chart3 = client.charts.create();

        Stream<Chart> charts = client.charts.listAll();

        assertThat(charts)
                .extracting(chart -> chart.key)
                .containsExactly(chart3.key, chart2.key, chart1.key);
    }

    @Test
    public void filter() {
        Chart chart1 = client.charts.create("foo");
        Chart chart2 = client.charts.create("bar");
        Chart chart3 = client.charts.create("foofoo");

        Stream<Chart> charts = client.charts.listAll(new ChartListParams().withFilter("foo"));

        assertThat(charts)
                .extracting(chart -> chart.key)
                .containsExactly(chart3.key, chart1.key);
    }

    @Test
    public void tag() {
        Chart chart1 = client.charts.create();
        client.charts.addTag(chart1.key, "foo");

        Chart chart2 = client.charts.create();

        Chart chart3 = client.charts.create();
        client.charts.addTag(chart3.key, "foo");

        Stream<Chart> charts = client.charts.listAll(new ChartListParams().withTag("foo"));

        assertThat(charts)
                .extracting(chart -> chart.key)
                .containsExactly(chart3.key, chart1.key);
    }

    @Test
    public void tagAndFilter() {
        Chart chart1 = client.charts.create("bar");
        client.charts.addTag(chart1.key, "foo");

        Chart chart2 = client.charts.create();
        client.charts.addTag(chart2.key, "foo");

        Chart chart3 = client.charts.create("bar");
        client.charts.addTag(chart3.key, "foo");

        Chart chart4 = client.charts.create("bar");

        Stream<Chart> charts = client.charts.listAll(new ChartListParams().withFilter("bar").withTag("foo"));

        assertThat(charts)
                .extracting(chart -> chart.key)
                .containsExactly(chart3.key, chart1.key);
    }

    @Test
    public void expand() {
        Chart chart = client.charts.create();
        Event event1 = client.events.create(chart.key);
        Event event2 = client.events.create(chart.key);

        Chart retrievedChart = client.charts.listAll(new ChartListParams().withExpandEvents(true)).findFirst().get();

        assertThat(retrievedChart.events)
                .extracting(event -> event.id)
                .containsExactly(event2.id, event1.id);
    }

    @Test
    public void pageSize() {
        Chart chart1 = client.charts.create();
        Chart chart2 = client.charts.create();
        Chart chart3 = client.charts.create();

        List<Chart> charts = client.charts.listFirstPage(null, 2).items;

        assertThat(charts)
                .extracting(chart -> chart.key)
                .containsExactly(chart3.key, chart2.key);
    }

    @Test
    public void listChartsWithValidationTest() {
        createTestChartWithErrors();

        ChartListParams params = new ChartListParams().withValidation(true);

        List<Chart> charts = client.charts.listFirstPage(params, 10).items;

        assertThat(charts.get(0).validation.errors)
                .isEqualTo(Arrays.asList("VALIDATE_DUPLICATE_LABELS", "VALIDATE_UNLABELED_OBJECTS", "VALIDATE_OBJECTS_WITHOUT_CATEGORIES"));

        assertThat(charts.get(0).validation.warnings)
                .isEqualTo(Arrays.asList());
    }

    @Test
    public void listChartsWithoutValidationTest() {
        createTestChartWithErrors();

        ChartListParams params = new ChartListParams();

        List<Chart> charts = client.charts.listFirstPage(params, 10).items;

        assertThat(charts.get(0).validation).isNull();
    }

}
