package seatsio.events;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import seatsio.SeatsioClientTest;
import seatsio.SeatsioException;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import static com.google.common.collect.Lists.newArrayList;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;
import static seatsio.events.TableBookingMode.BY_SEAT;
import static seatsio.events.TableBookingMode.BY_TABLE;

public class CreateEventsTest extends SeatsioClientTest {

    @Test
    public void multipleEvents() {
        String chartKey = createTestChart();
        List<EventCreationParams> params = newArrayList(new EventCreationParams(), new EventCreationParams());

        List<Event> events = client.events.create(chartKey, params);

        assertThat(events)
                .hasSize(2)
                .extracting("key")
                .allMatch(Objects::nonNull);
    }

    @Test
    public void singleEvent() {
        String chartKey = createTestChart();
        List<EventCreationParams> params = newArrayList(new EventCreationParams());

        List<Event> events = client.events.create(chartKey, params);

        Event event = events.get(0);
        assertThat(event.id).isNotZero();
        assertThat(event.key).isNotNull();
        assertThat(event.chartKey).isEqualTo(chartKey);
        assertThat(event.bookWholeTables).isFalse();
        assertThat(event.supportsBestAvailable).isNull();
        assertThat(event.forSaleConfig).isNull();
        Instant now = Instant.now();
        assertThat(event.createdOn).isBetween(now.minus(1, MINUTES), now.plus(1, MINUTES));
        assertThat(event.updatedOn).isNull();
    }

    @Test
    public void eventKeyCanBePassedIn() {
        String chartKey = createTestChart();
        List<EventCreationParams> params = newArrayList(new EventCreationParams("event1"), new EventCreationParams("event2"));

        List<Event> events = client.events.create(chartKey, params);

        assertThat(events).extracting("key").containsExactly("event1", "event2");
    }

    @Test
    public void bookWholeTablesCanBePassedIn() {
        String chartKey = createTestChart();
        List<EventCreationParams> params = newArrayList(new EventCreationParams(null, true), new EventCreationParams(null, false));

        List<Event> events = client.events.create(chartKey, params);

        assertThat(events).extracting("bookWholeTables").containsExactly(true, false);
    }

    @Test
    public void tableBookingModesCanBePassedIn() {
        String chartKey = createTestChartWithTables();
        List<EventCreationParams> params = newArrayList(
                new EventCreationParams(null, ImmutableMap.of("T1", BY_TABLE, "T2", BY_SEAT)),
                new EventCreationParams(null, ImmutableMap.of("T1", BY_SEAT, "T2", BY_TABLE))
        );

        List<Event> events = client.events.create(chartKey, params);

        assertThat(events).extracting("bookWholeTables").containsExactly(false, false);
        assertThat(events).extracting("tableBookingModes").containsExactly(
                ImmutableMap.of("T1", BY_TABLE, "T2", BY_SEAT),
                ImmutableMap.of("T2", BY_TABLE, "T1", BY_SEAT)
        );
    }

    @Test(expected = SeatsioException.class)
    public void errorOnDuplicateKeys() {
        String chartKey = createTestChart();
        List<EventCreationParams> params = newArrayList(new EventCreationParams("event1"), new EventCreationParams("event1"));

        client.events.create(chartKey, params);
    }

}
