package seatsio.events;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.SeatsioException;
import seatsio.seasons.SeasonParams;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static seatsio.events.EventObjectInfo.*;
import static seatsio.events.StatusChangeType.*;

public class ChangeObjectStatusInBatchTest extends SeatsioClientTest {

    @Test
    public void test() {
        String chartKey1 = createTestChart();
        String chartKey2 = createTestChart();
        Event event1 = client.events.create(chartKey1);
        Event event2 = client.events.create(chartKey2);

        List<ChangeObjectStatusResult> result = client.events.changeObjectStatus(List.of(
                new StatusChangeRequest.Builder().withType(CHANGE_STATUS_TO).withEventKey(event1.key).withObjects(List.of("A-1")).withStatus("lolzor").build(),
                new StatusChangeRequest.Builder().withType(CHANGE_STATUS_TO).withEventKey(event2.key).withObjects(List.of("A-2")).withStatus("lolzor").build()
        ));

        assertThat(result.get(0).objects.get("A-1").status).isEqualTo("lolzor");
        assertThat(client.events.retrieveObjectInfo(event1.key, "A-1").status).isEqualTo("lolzor");

        assertThat(result.get(1).objects.get("A-2").status).isEqualTo("lolzor");
        assertThat(client.events.retrieveObjectInfo(event2.key, "A-2").status).isEqualTo("lolzor");
    }

    @Test
    public void channelKeys() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey, new CreateEventParams().withChannels(List.of(
                new Channel("channelKey1", "channel 1", "#FFFF99", 1, Set.of("A-1"))
        )));

        List<ChangeObjectStatusResult> result = client.events.changeObjectStatus(List.of(
                new StatusChangeRequest.Builder().withEventKey(event.key).withObjects(List.of("A-1")).withStatus("lolzor").withChannelKeys(Set.of("channelKey1")).build()
        ));

        assertThat(result.get(0).objects.get("A-1").status).isEqualTo("lolzor");
    }

    @Test
    public void ignoreChannels() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey, new CreateEventParams().withChannels(List.of(
                new Channel("channelKey1", "channel 1", "#FFFF99", 1, Set.of("A-1"))
        )));

        List<ChangeObjectStatusResult> result = client.events.changeObjectStatus(List.of(
                new StatusChangeRequest.Builder().withEventKey(event.key).withObjects(List.of("A-1")).withStatus("lolzor").withIgnoreChannels(true).build()
        ));

        assertThat(result.get(0).objects.get("A-1").status).isEqualTo("lolzor");
    }

    @Test
    public void allowedPreviousStatus() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        try {
            client.events.changeObjectStatus(List.of(
                    new StatusChangeRequest.Builder().withEventKey(event.key).withObjects(List.of("A-1")).withStatus("lolzor").withAllowedPreviousStatuses(Set.of("MustBeThisStatus")).build()
            ));
            fail("expected exception");
        } catch (SeatsioException e) {
            assertThat(e.errors).hasSize(1);
            assertThat(e.errors.get(0).getCode()).isEqualTo("ILLEGAL_STATUS_CHANGE");
        }
    }

    @Test
    public void rejectedPreviousStatus() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        try {
            client.events.changeObjectStatus(List.of(
                    new StatusChangeRequest.Builder().withEventKey(event.key).withObjects(List.of("A-1")).withStatus("lolzor").withRejectedPreviousStatuses(Set.of("free")).build()
            ));
            fail("expected exception");
        } catch (SeatsioException e) {
            assertThat(e.errors).hasSize(1);
            assertThat(e.errors.get(0).getCode()).isEqualTo("ILLEGAL_STATUS_CHANGE");
        }
    }

    @Test
    public void release() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.book(event.key, List.of("A-1"));

        List<ChangeObjectStatusResult> result = client.events.changeObjectStatus(List.of(
                new StatusChangeRequest.Builder().withType(RELEASE).withEventKey(event.key).withObjects(List.of("A-1")).build()
        ));

        assertThat(result.get(0).objects.get("A-1").status).isEqualTo(FREE);
        assertThat(client.events.retrieveObjectInfo(event.key, "A-1").status).isEqualTo(FREE);
    }

    @Test
    public void overrideSeasonStatus() {
        String chartKey = createTestChart();
        client.seasons.create(chartKey, new SeasonParams().key("aSeason").eventKeys(List.of("event1")));
        client.events.book("aSeason", List.of("A-1"));

        List<ChangeObjectStatusResult> result = client.events.changeObjectStatus(List.of(
                new StatusChangeRequest.Builder().withType(OVERRIDE_SEASON_STATUS).withEventKey("event1").withObjects(List.of("A-1")).build()
        ));

        assertThat(result.get(0).objects.get("A-1").status).isEqualTo(FREE);
        assertThat(client.events.retrieveObjectInfo("event1", "A-1").status).isEqualTo(FREE);
    }

    @Test
    public void useSeasonStatus() {
        String chartKey = createTestChart();
        client.seasons.create(chartKey, new SeasonParams().key("aSeason").eventKeys(List.of("event1")));
        client.events.book("aSeason", List.of("A-1"));
        client.events.overrideSeasonObjectStatus("event1", List.of("A-1"));

        List<ChangeObjectStatusResult> result = client.events.changeObjectStatus(List.of(
                new StatusChangeRequest.Builder().withType(USE_SEASON_STATUS).withEventKey("event1").withObjects(List.of("A-1")).build()
        ));

        assertThat(result.get(0).objects.get("A-1").status).isEqualTo(BOOKED);
        assertThat(client.events.retrieveObjectInfo("event1", "A-1").status).isEqualTo(BOOKED);
    }

    @Test
    public void resaleListingId() {
        String chartKey1 = createTestChart();
        String chartKey2 = createTestChart();
        Event event1 = client.events.create(chartKey1);
        Event event2 = client.events.create(chartKey2);

        List<ChangeObjectStatusResult> result = client.events.changeObjectStatus(List.of(
                new StatusChangeRequest.Builder().withType(CHANGE_STATUS_TO).withEventKey(event1.key).withObjects(List.of("A-1")).withResaleListingId("listing1").withStatus(RESALE).build(),
                new StatusChangeRequest.Builder().withType(CHANGE_STATUS_TO).withEventKey(event2.key).withObjects(List.of("A-2")).withResaleListingId("listing1").withStatus(RESALE).build()
        ));

        assertThat(result.get(0).objects.get("A-1").resaleListingId).isEqualTo("listing1");
        assertThat(client.events.retrieveObjectInfo(event1.key, "A-1").resaleListingId).isEqualTo("listing1");

        assertThat(result.get(1).objects.get("A-2").resaleListingId).isEqualTo("listing1");
        assertThat(client.events.retrieveObjectInfo(event2.key, "A-2").resaleListingId).isEqualTo("listing1");
    }
}
