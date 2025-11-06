package seatsio.ticketBuyers;

import java.util.UUID;

public record AddTicketBuyerIdsResponse(UUID[] added, UUID[] alreadyPresent) {
}
