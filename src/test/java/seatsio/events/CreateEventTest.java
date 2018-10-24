package seatsio.events;

import com.google.common.collect.ImmutableMap;
import jdk.nashorn.internal.ir.annotations.Immutable;
import org.junit.Test;
import seatsio.SeatsioClientTest;
import seatsio.charts.Chart;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;
import static seatsio.events.TableBookingMode.BY_SEAT;
import static seatsio.events.TableBookingMode.BY_TABLE;

public class CreateEventTest extends SeatsioClientTest {

    @Test
    public void chartKeyIsRequired() {
        String chartKey = createTestChart();

        Event event = client.events.create(chartKey);

        assertThat(event.id).isNotZero();
        assertThat(event.key).isNotNull();
        assertThat(event.chartKey).isEqualTo(chartKey);
        assertThat(event.bookWholeTables).isFalse();
        assertThat(event.supportsBestAvailable).isTrue();
        assertThat(event.forSaleConfig).isNull();
        Instant now = Instant.now();
        assertThat(event.createdOn).isBetween(now.minus(1, MINUTES), now.plus(1, MINUTES));
        assertThat(event.updatedOn).isNull();
    }

    @Test
    public void eventKeyCanBePassedIn() {
        Chart chart = client.charts.create();

        Event event = client.events.create(chart.key, "eventje");

        assertThat(event.key).isEqualTo("eventje");
        assertThat(event.bookWholeTables).isFalse();
    }

    @Test
    public void bookWholeTablesCanBePassedIn() {
        Chart chart = client.charts.create();

        Event event = client.events.create(chart.key, null, true);

        assertThat(event.key).isNotNull();
        assertThat(event.bookWholeTables).isTrue();
    }

    @Test
    public void tableBookingModesCanBePassedIn() {
        String chartKey = createTestChartWithTables();

        Event event = client.events.create(chartKey, null, ImmutableMap.of("T1", BY_TABLE, "T2", BY_SEAT));

        assertThat(event.key).isNotNull();
        assertThat(event.bookWholeTables).isFalse();
        assertThat(event.tableBookingModes).isEqualTo(ImmutableMap.of("T1", BY_TABLE, "T2", BY_SEAT));
    }
}
