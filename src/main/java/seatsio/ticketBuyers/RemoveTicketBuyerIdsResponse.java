package seatsio.ticketBuyers;

import java.util.Set;
import java.util.UUID;

public record RemoveTicketBuyerIdsResponse(Set<UUID> removed, Set<UUID> notPresent) {

}
