package seatsio.ticketBuyers;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class AddTicketBuyerIdsTest extends SeatsioClientTest {

    @Test
    public void canAddTicketBuyerIds() {
        UUID ticketBuyerId1 = UUID.randomUUID();
        UUID ticketBuyerId2 = UUID.randomUUID();
        UUID ticketBuyerId3 = UUID.randomUUID();
        AddTicketBuyerIdsResponse response = client.ticketBuyers.add(ticketBuyerId1, ticketBuyerId2, ticketBuyerId3);
        assertThat(response.getAdded()).containsOnly(ticketBuyerId1, ticketBuyerId2, ticketBuyerId3);
        assertThat(response.getAlreadyPresent()).isEmpty();
    }

    @Test
    public void canAddTicketBuyerIds_listWithDuplicates() {
        UUID ticketBuyerId1 = UUID.randomUUID();
        UUID ticketBuyerId2 = UUID.randomUUID();
        AddTicketBuyerIdsResponse response = client.ticketBuyers.add(List.of(
                ticketBuyerId1, ticketBuyerId1, ticketBuyerId1,
                ticketBuyerId2, ticketBuyerId2
        ));
        assertThat(response.getAdded()).containsOnly(ticketBuyerId1, ticketBuyerId2);
        assertThat(response.getAlreadyPresent()).isEmpty();
    }

    @Test
    public void canAddTicketBuyerIds_sameIdDoesntGetAddedTwice() {
        UUID ticketBuyerId1 = UUID.randomUUID();
        UUID ticketBuyerId2 = UUID.randomUUID();
        AddTicketBuyerIdsResponse response = client.ticketBuyers.add(ticketBuyerId1, ticketBuyerId2);
        assertThat(response.getAdded()).containsOnly(ticketBuyerId1, ticketBuyerId2);
        assertThat(response.getAlreadyPresent()).isEmpty();

        AddTicketBuyerIdsResponse secondResponse = client.ticketBuyers.add(ticketBuyerId1);
        assertThat(secondResponse.getAlreadyPresent()).containsOnly(ticketBuyerId1);
    }

    @Test
    public void nullDoesNotGetAdded() {
        UUID ticketBuyerId1 = UUID.randomUUID();
        AddTicketBuyerIdsResponse response = client.ticketBuyers.add(ticketBuyerId1, null);
        assertThat(response.getAdded()).containsOnly(ticketBuyerId1);
        assertThat(response.getAlreadyPresent()).isEmpty();
    }

}
