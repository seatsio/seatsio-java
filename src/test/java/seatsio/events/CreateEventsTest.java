package seatsio.events;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.SeatsioException;
import seatsio.charts.CategoryKey;
import seatsio.charts.SocialDistancingRuleset;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
        assertThat(event.tableBookingConfig).isEqualTo(TableBookingConfig.inherit());
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
    public void tableBookingConfigCanBePassedIn() {
        String chartKey = createTestChartWithTables();
        List<EventCreationParams> params = newArrayList(
                new EventCreationParams(null, TableBookingConfig.custom(ImmutableMap.of("T1", BY_TABLE, "T2", BY_SEAT))),
                new EventCreationParams(null, TableBookingConfig.custom(ImmutableMap.of("T1", BY_SEAT, "T2", BY_TABLE)))
        );

        List<Event> events = client.events.create(chartKey, params);

        assertThat(events).extracting("tableBookingConfig").containsExactly(
                TableBookingConfig.custom(ImmutableMap.of("T1", BY_TABLE, "T2", BY_SEAT)),
                TableBookingConfig.custom(ImmutableMap.of("T2", BY_TABLE, "T1", BY_SEAT))
        );
    }

    @Test
    public void socialDistancingRulesetKeyCanBePassedIn() {
        String chartKey = createTestChartWithTables();
        Map<String, SocialDistancingRuleset> rulesets = ImmutableMap.of("ruleset1", SocialDistancingRuleset.ruleBased("My ruleset").build());
        client.charts.saveSocialDistancingRulesets(chartKey, rulesets);
        List<EventCreationParams> params = newArrayList(
                new EventCreationParams(null, "ruleset1"),
                new EventCreationParams(null, "ruleset1")
        );

        List<Event> events = client.events.create(chartKey, params);

        assertThat(events).extracting("socialDistancingRulesetKey").containsExactly("ruleset1", "ruleset1");
    }

    @Test
    public void objectCategoriesCanBePassedIn() {
        String chartKey = createTestChart();
        Map<String, CategoryKey> objectCategories = ImmutableMap.of("A-1", CategoryKey.of(10L));
        List<EventCreationParams> params = newArrayList(
                new EventCreationParams(null, objectCategories)
        );

        List<Event> events = client.events.create(chartKey, params);

        assertThat(events).extracting("objectCategories").containsExactly(objectCategories);
    }

    @Test
    public void errorOnDuplicateKeys() {
        String chartKey = createTestChart();
        List<EventCreationParams> params = newArrayList(new EventCreationParams("event1"), new EventCreationParams("event1"));

        assertThrows(SeatsioException.class, () -> client.events.create(chartKey, params));
    }

}
