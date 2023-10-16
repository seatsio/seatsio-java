package seatsio.events;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.SeatsioException;
import seatsio.charts.CategoryKey;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seatsio.events.TableBookingMode.BY_SEAT;
import static seatsio.events.TableBookingMode.BY_TABLE;

public class CreateEventsTest extends SeatsioClientTest {

    @Test
    public void multipleEvents() {
        String chartKey = createTestChart();

        List<CreateEventParams> params = newArrayList(new CreateEventParams(), new CreateEventParams());
        List<Event> events = client.events.create(chartKey, params);

        assertThat(events)
                .hasSize(2)
                .extracting("key")
                .allMatch(Objects::nonNull);
    }

    @Test
    public void singleEvent() {
        String chartKey = createTestChart();

        List<Event> events = client.events.create(chartKey, newArrayList(new CreateEventParams()));

        Event event = events.get(0);
        assertThat(event.id).isNotZero();
        assertThat(event.key).isNotNull();
        assertThat(event.chartKey).isEqualTo(chartKey);
        assertThat(event.tableBookingConfig).isEqualTo(TableBookingConfig.inherit());
        assertThat(event.supportsBestAvailable).isTrue();
        assertThat(event.categories).hasSize(3);
        assertThat(event.tableBookingConfig).isEqualTo(TableBookingConfig.inherit());
        assertThat(event.forSaleConfig).isNull();
        Instant now = Instant.now();
        assertThat(event.createdOn).isBetween(now.minus(1, MINUTES), now.plus(1, MINUTES));
        assertThat(event.updatedOn).isNull();
    }

    @Test
    public void eventKeyCanBePassedIn() {
        String chartKey = createTestChart();

        List<Event> events = client.events.create(chartKey, newArrayList(
                new CreateEventParams().withKey("event1"),
                new CreateEventParams().withKey("event2")
        ));

        assertThat(events).extracting("key").containsExactly("event1", "event2");
    }

    @Test
    public void tableBookingConfigCanBePassedIn() {
        String chartKey = createTestChartWithTables();

        List<Event> events = client.events.create(chartKey, newArrayList(
                new CreateEventParams().withTableBookingConfig(TableBookingConfig.custom(ImmutableMap.of("T1", BY_TABLE, "T2", BY_SEAT))),
                new CreateEventParams().withTableBookingConfig(TableBookingConfig.custom(ImmutableMap.of("T1", BY_SEAT, "T2", BY_TABLE)))
        ));

        assertThat(events).extracting("tableBookingConfig").containsExactly(
                TableBookingConfig.custom(ImmutableMap.of("T1", BY_TABLE, "T2", BY_SEAT)),
                TableBookingConfig.custom(ImmutableMap.of("T2", BY_TABLE, "T1", BY_SEAT))
        );
    }

    @Test
    public void objectCategoriesCanBePassedIn() {
        String chartKey = createTestChart();
        Map<String, CategoryKey> objectCategories = ImmutableMap.of("A-1", CategoryKey.of(10L));

        List<Event> events = client.events.create(chartKey, newArrayList(
                new CreateEventParams().withObjectCategories(objectCategories)
        ));

        assertThat(events).extracting("objectCategories").containsExactly(objectCategories);
    }

    @Test
    public void nameCanBePassedIn() {
        String chartKey = createTestChart();

        List<Event> events = client.events.create(chartKey, newArrayList(
                new CreateEventParams().withName("My event")
        ));

        assertThat(events).extracting("name").containsExactly("My event");
    }

    @Test
    public void dateCanBePassedIn() {
        String chartKey = createTestChart();

        List<Event> events = client.events.create(chartKey, newArrayList(
                new CreateEventParams().withDate(LocalDate.of(2022, 10, 1))
        ));

        assertThat(events).extracting("date").containsExactly(LocalDate.of(2022, 10, 1));
    }

    @Test
    public void channelsCanBePassedIn() {
        String chartKey = createTestChart();
        List<Channel> channels = List.of(
                new Channel("channelKey1", "channel 1", "#FFFF99", 1, Set.of("A-1")),
                new Channel("channelKey2", "channel 2", "#FFFF99", 2, Set.of("A-2"))
        );

        List<Event> events = client.events.create(chartKey, newArrayList(
                new CreateEventParams().withChannels(channels)
        ));

        assertThat(events)
                .extracting("channels")
                .containsExactly(List.of(
                        new Channel("channelKey1", "channel 1", "#FFFF99", 1, Set.of("A-1")),
                        new Channel("channelKey2", "channel 2", "#FFFF99", 2, Set.of("A-2"))
                ));
    }

    @Test
    public void forSaleConfigCanBePassedIn() {
        String chartKey = createTestChart();
        ForSaleConfigParams forSaleConfigParams1 = new ForSaleConfigParams(false, List.of("A-1"), Map.of("GA1", 5), List.of("Cat1"));
        ForSaleConfigParams forSaleConfigParams2 = new ForSaleConfigParams(false, List.of("A-2"), Map.of("GA1", 7), List.of("Cat1"));

        List<Event> events = client.events.create(chartKey, newArrayList(
                new CreateEventParams().withForSaleConfigParams(forSaleConfigParams1),
                new CreateEventParams().withForSaleConfigParams(forSaleConfigParams2)
        ));

        assertThat(events.get(0))
                .hasFieldOrPropertyWithValue("forSaleConfig", toForSaleConfig(forSaleConfigParams1));
        assertThat(events.get(1))
                .hasFieldOrPropertyWithValue("forSaleConfig", toForSaleConfig(forSaleConfigParams2));
    }

    @Test
    public void errorOnDuplicateKeys() {
        String chartKey = createTestChart();

        assertThrows(SeatsioException.class, () -> client.events.create(chartKey, newArrayList(
                new CreateEventParams().withKey("event1"),
                new CreateEventParams().withKey("event1")
        )));
    }

    private static ForSaleConfig toForSaleConfig(ForSaleConfigParams params) {
        ForSaleConfig forSaleConfig = new ForSaleConfig();
        forSaleConfig.forSale = params.forSale;
        forSaleConfig.objects = params.objects;
        forSaleConfig.areaPlaces = params.areaPlaces;
        forSaleConfig.categories = params.categories;
        return forSaleConfig;
    }
}
