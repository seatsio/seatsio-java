package seatsio.ticketBuyers;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class RemoveTicketBuyerIdsTest extends SeatsioClientTest {

    @Test
    public void canRemoveTicketBuyerIds() {
        UUID ticketBuyerId1 = UUID.randomUUID();
        UUID ticketBuyerId2 = UUID.randomUUID();
        UUID ticketBuyerId3 = UUID.randomUUID();
        client.ticketBuyers.add(ticketBuyerId1, ticketBuyerId2);

        RemoveTicketBuyerIdsResponse removeResponse = client.ticketBuyers.remove(ticketBuyerId1, ticketBuyerId2, ticketBuyerId3);

        assertThat(removeResponse.getRemoved()).containsOnly(ticketBuyerId1, ticketBuyerId2);
        assertThat(removeResponse.getNotPresent()).containsOnly(ticketBuyerId3);
    }

    @Test
    public void nullDoesNotGetRemoved() {
        UUID ticketBuyerId1 = UUID.randomUUID();
        UUID ticketBuyerId2 = UUID.randomUUID();
        UUID ticketBuyerId3 = UUID.randomUUID();
        client.ticketBuyers.add(ticketBuyerId1, ticketBuyerId2, ticketBuyerId3);

        RemoveTicketBuyerIdsResponse response = client.ticketBuyers.remove(ticketBuyerId1, null);

        assertThat(response.getRemoved()).containsOnly(ticketBuyerId1);
        assertThat(response.getNotPresent()).isEmpty();
    }

}
