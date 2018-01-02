package seatsio.events;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import seatsio.SeatsioClientTest;

import java.time.Instant;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class ListStatusChangesTest extends SeatsioClientTest {

    @Test
    public void test() {
        String chartKey = createTestChart();
        Event event = client.events().create(chartKey);
        client.events().changeObjectStatus(event.key, asList("A-1"), "s1");
        client.events().changeObjectStatus(event.key, asList("A-2"), "s2");
        client.events().changeObjectStatus(event.key, asList("A-3"), "s3");

        Stream<StatusChange> statusChanges = client.events().statusChanges(event.key).all();

        assertThat(statusChanges)
                .extracting(statusChange -> statusChange.status)
                .containsExactly("s3", "s2", "s1");
    }

    @Test
    public void propertiesOfStatusChange() {
        String chartKey = createTestChart();
        Event event = client.events().create(chartKey);
        SeatsioObject object = new SeatsioObject("A-1", ImmutableMap.of("foo", "bar"));
        client.events().changeObjectStatus(event.key, asList(object), "s1", null, "order1");

        Stream<StatusChange> statusChanges = client.events().statusChanges(event.key).all();
        StatusChange statusChange = statusChanges.findFirst().get();

        assertThat(statusChange.id).isNotZero();
        Instant now = Instant.now();
        assertThat(statusChange.date).isBetween(now.minus(1, MINUTES), now.plus(1, MINUTES));
        assertThat(statusChange.orderId).isEqualTo("order1");
        assertThat(statusChange.status).isEqualTo("s1");
        assertThat(statusChange.objectLabelOrUuid).isEqualTo("A-1");
        assertThat(statusChange.eventId).isEqualTo(event.id);
        assertThat(statusChange.extraData).isEqualTo(ImmutableMap.of("foo", "bar"));
    }

}
