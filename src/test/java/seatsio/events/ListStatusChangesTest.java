package seatsio.events;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import seatsio.SeatsioClient;
import seatsio.SeatsioClientTest;
import seatsio.util.Lister;
import seatsio.util.Page;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static seatsio.SortDirection.DESC;
import static seatsio.events.ObjectNotPresentReason.SWITCHED_TO_BOOK_BY_SEAT;
import static seatsio.events.StatusChangeOriginType.API_CALL;
import static seatsio.events.TableBookingConfig.allBySeat;
import static seatsio.events.TableBookingConfig.allByTable;

public class ListStatusChangesTest extends SeatsioClientTest {

    @Test
    public void test() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.changeObjectStatus(newArrayList(
                new StatusChangeRequest(event.key, newArrayList("A-1"), "s1"),
                new StatusChangeRequest(event.key, newArrayList("A-2"), "s2"),
                new StatusChangeRequest(event.key, newArrayList("A-3"), "s3")
        ));
        waitForStatusChanges(client, event, 3);

        Stream<StatusChange> statusChanges = client.events.statusChanges(event.key).all();
        assertThat(statusChanges)
                .extracting(statusChange -> statusChange.status)
                .containsExactly("s3", "s2", "s1");
    }

    @Test
    public void propertiesOfStatusChange() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        ObjectProperties object = new ObjectProperties("A-1", ImmutableMap.of("foo", "bar"));
        client.events.changeObjectStatus(event.key, asList(object), "s1", null, "order1", null, null, null, null);
        waitForStatusChanges(client, event, 1);

        Stream<StatusChange> statusChanges = client.events.statusChanges(event.key).all();

        StatusChange statusChange = statusChanges.collect(toList()).get(0);
        assertThat(statusChange.id).isNotZero();
        Instant now = Instant.now();
        assertThat(statusChange.date).isBetween(now.minus(1, MINUTES), now.plus(1, MINUTES));
        assertThat(statusChange.orderId).isEqualTo("order1");
        assertThat(statusChange.status).isEqualTo("s1");
        assertThat(statusChange.objectLabel).isEqualTo("A-1");
        assertThat(statusChange.eventId).isEqualTo(event.id);
        assertThat(statusChange.extraData).isEqualTo(ImmutableMap.of("foo", "bar"));
        assertThat(statusChange.origin.type).isEqualTo(API_CALL);
        assertThat(statusChange.origin.ip).isNotNull();
        assertThat(statusChange.isPresentOnChart).isTrue();
        assertThat(statusChange.notPresentOnChartReason).isNull();
    }

    @Test
    public void objectNotPresentAnymore() {
        String chartKey = createTestChartWithTables();
        Event event = client.events.create(chartKey, "event1", allByTable());
        client.events.book(event.key, newArrayList("T1"));
        client.events.update("event1", null, null, allBySeat());
        waitForStatusChanges(client, event, 1);

        Stream<StatusChange> statusChanges = client.events.statusChanges(event.key).all();

        StatusChange statusChange = statusChanges.collect(toList()).get(0);
        assertThat(statusChange.isPresentOnChart).isFalse();
        assertThat(statusChange.notPresentOnChartReason).isEqualTo(SWITCHED_TO_BOOK_BY_SEAT);
    }

    @Test
    public void filter() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.changeObjectStatus(newArrayList(
                new StatusChangeRequest(event.key, newArrayList("A-1"), "s1"),
                new StatusChangeRequest(event.key, newArrayList("A-2"), "s2"),
                new StatusChangeRequest(event.key, newArrayList("B-1"), "s3"),
                new StatusChangeRequest(event.key, newArrayList("A-3"), "s4")
        ));
        waitForStatusChanges(client, event, 4);

        Stream<StatusChange> statusChanges = client.events.statusChanges(event.key, "A").all();
        assertThat(statusChanges)
                .extracting(statusChange -> statusChange.status)
                .containsExactly("s4", "s2", "s1");
    }

    @Test
    public void sortAsc() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.changeObjectStatus(newArrayList(
                new StatusChangeRequest(event.key, newArrayList("A-1"), "s1"),
                new StatusChangeRequest(event.key, newArrayList("A-2"), "s2"),
                new StatusChangeRequest(event.key, newArrayList("A-3"), "s3")
        ));
        waitForStatusChanges(client, event, 3);

        Stream<StatusChange> statusChanges = client.events.statusChanges(event.key, null, "objectLabel", null).all();

        assertThat(statusChanges)
                .extracting(statusChange -> statusChange.status)
                .containsExactly("s1", "s2", "s3");
    }

    @Test
    public void sortAscPageBefore() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.changeObjectStatus(newArrayList(
                new StatusChangeRequest(event.key, newArrayList("A-1"), "s1"),
                new StatusChangeRequest(event.key, newArrayList("A-2"), "s2"),
                new StatusChangeRequest(event.key, newArrayList("A-3"), "s3")
        ));
        waitForStatusChanges(client, event, 3);

        Lister<StatusChange> allStatusChanges = client.events.statusChanges(event.key, null, "objectLabel", null);
        List<StatusChange> list = allStatusChanges.all().collect(toList());
        Page<StatusChange> statusChanges = allStatusChanges.pageBefore(list.get(list.size() - 1).id);

        assertThat(statusChanges.items)
                .extracting(statusChange -> statusChange.status)
                .containsExactly("s1", "s2");
    }

    @Test
    public void sortAscPageAfter() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.changeObjectStatus(newArrayList(
                new StatusChangeRequest(event.key, newArrayList("A-1"), "s1"),
                new StatusChangeRequest(event.key, newArrayList("A-2"), "s2"),
                new StatusChangeRequest(event.key, newArrayList("A-3"), "s3")
        ));
        waitForStatusChanges(client, event, 3);

        Lister<StatusChange> allStatusChanges = client.events.statusChanges(event.key, null, "objectLabel", null);

        List<StatusChange> list = allStatusChanges.all().collect(toList());
        Page<StatusChange> statusChanges = allStatusChanges.pageAfter(list.get(0).id);
        assertThat(statusChanges.items)
                .extracting(statusChange -> statusChange.status)
                .containsExactly("s2", "s3");
    }

    @Test
    public void sortDesc() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.changeObjectStatus(newArrayList(
                new StatusChangeRequest(event.key, newArrayList("A-1"), "s1"),
                new StatusChangeRequest(event.key, newArrayList("A-2"), "s2"),
                new StatusChangeRequest(event.key, newArrayList("A-3"), "s3")
        ));
        waitForStatusChanges(client, event, 3);

        Stream<StatusChange> statusChanges = client.events.statusChanges(event.key, null, "objectLabel", DESC).all();

        assertThat(statusChanges)
                .extracting(statusChange -> statusChange.status)
                .containsExactly("s3", "s2", "s1");
    }

    public static void waitForStatusChanges(SeatsioClient client, Event event, int numStatusChanges) {
        await()
                .atMost(10, SECONDS)
                .until(() -> client.events.statusChanges(event.key).all(), statusChanges -> statusChanges.count() == numStatusChanges);
    }
}
