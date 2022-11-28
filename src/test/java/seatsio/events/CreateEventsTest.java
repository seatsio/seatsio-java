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
import static seatsio.events.EventCreationParamsBuilder.anEvent;
import static seatsio.events.TableBookingMode.BY_SEAT;
import static seatsio.events.TableBookingMode.BY_TABLE;

public class CreateEventsTest extends SeatsioClientTest {

    @Test
    public void multipleEvents() {
        String chartKey = createTestChart();

        List<EventCreationParamsBuilder> params = newArrayList(anEvent(), anEvent());
        List<Event> events = client.events.create(chartKey, params);

        assertThat(events)
                .hasSize(2)
                .extracting("key")
                .allMatch(Objects::nonNull);
    }

    @Test
    public void singleEvent() {
        String chartKey = createTestChart();

        List<Event> events = client.events.create(chartKey, newArrayList(anEvent()));

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
                anEvent().withKey("event1"),
                anEvent().withKey("event2")
        ));

        assertThat(events).extracting("key").containsExactly("event1", "event2");
    }

    @Test
    public void tableBookingConfigCanBePassedIn() {
        String chartKey = createTestChartWithTables();

        List<Event> events = client.events.create(chartKey, newArrayList(
                anEvent().withTableBookingConfig(TableBookingConfig.custom(ImmutableMap.of("T1", BY_TABLE, "T2", BY_SEAT))),
                anEvent().withTableBookingConfig(TableBookingConfig.custom(ImmutableMap.of("T1", BY_SEAT, "T2", BY_TABLE)))
        ));

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

        List<Event> events = client.events.create(chartKey, newArrayList(
                anEvent().withSocialDistancingRulesetKey("ruleset1"),
                anEvent().withSocialDistancingRulesetKey("ruleset1")
        ));

        assertThat(events).extracting("socialDistancingRulesetKey").containsExactly("ruleset1", "ruleset1");
    }

    @Test
    public void objectCategoriesCanBePassedIn() {
        String chartKey = createTestChart();
        Map<String, CategoryKey> objectCategories = ImmutableMap.of("A-1", CategoryKey.of(10L));

        List<Event> events = client.events.create(chartKey, newArrayList(
                anEvent().withObjectCategories(objectCategories)
        ));

        assertThat(events).extracting("objectCategories").containsExactly(objectCategories);
    }

    @Test
    public void errorOnDuplicateKeys() {
        String chartKey = createTestChart();

        assertThrows(SeatsioException.class, () -> client.events.create(chartKey, newArrayList(
                anEvent().withKey("event1"),
                anEvent().withKey("event1")
        )));
    }

}
