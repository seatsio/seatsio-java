package seatsio.ticketBuyers;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ListTicketBuyerIdsTest extends SeatsioClientTest {
    @Test
    public void canListTicketBuyerIds() {
        UUID ticketBuyerId1 = UUID.randomUUID();
        UUID ticketBuyerId2 = UUID.randomUUID();
        UUID ticketBuyerId3 = UUID.randomUUID();
        client.ticketBuyers.add(ticketBuyerId1, ticketBuyerId2, ticketBuyerId3);

        Stream<UUID> ticketBuyerIds = client.ticketBuyers.listAll();

        assertThat(ticketBuyerIds).contains(ticketBuyerId1, ticketBuyerId2, ticketBuyerId3);
    }
}
