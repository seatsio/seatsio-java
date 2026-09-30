package seatsio.events;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.holdTokens.HoldToken;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static seatsio.events.AreaType.GENERAL_ADMISSION;
import static seatsio.events.EventObjectInfo.FREE;
import static seatsio.events.TableBookingConfig.allByTable;
import static seatsio.events.TableType.BOOK_BY_TABLE;

public class RetrieveEventObjectInfoTest extends SeatsioClientTest {

    @Test
    public void test() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        EventObjectInfo objectInfo = client.events.retrieveObjectInfo(event.key(), "A-1");

        assertThat(objectInfo.status()).isEqualTo(FREE);
        assertThat(objectInfo.forSale()).isEqualTo(true);
    }

    @Test
    public void ga() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        HoldToken holdToken = client.holdTokens.create();
        client.events.hold(event.key(), List.of("GA1"), holdToken.holdToken());

        EventObjectInfo objectInfo = client.events.retrieveObjectInfo(event.key(), "GA1");

        assertThat(objectInfo.holds()).isEqualTo(Map.of(holdToken.holdToken(), Map.of("NO_TICKET_TYPE", 1)));
        assertThat(objectInfo.areaType()).isEqualTo(GENERAL_ADMISSION);
        assertThat(objectInfo.tableType()).isNull();
    }

    @Test
    public void table() {
        String chartKey = createTestChartWithTables();
        Event event = client.events.create(chartKey, new CreateEventParams().withTableBookingConfig(allByTable()));

        EventObjectInfo objectInfo = client.events.retrieveObjectInfo(event.key(), "T1");

        assertThat(objectInfo.tableType()).isEqualTo(BOOK_BY_TABLE);
        assertThat(objectInfo.areaType()).isNull();
    }

}
