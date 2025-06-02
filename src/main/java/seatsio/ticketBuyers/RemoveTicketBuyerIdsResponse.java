package seatsio.ticketBuyers;

import java.util.Set;
import java.util.UUID;

public class RemoveTicketBuyerIdsResponse {

    private final Set<UUID> removed;
    private final Set<UUID> notPresent;

    public RemoveTicketBuyerIdsResponse(Set<UUID> removed, Set<UUID> notPresent) {
        this.removed = removed;
        this.notPresent = notPresent;
    }

    public Set<UUID> getRemoved() {
        return removed;
    }


    public Set<UUID> getNotPresent() {
        return notPresent;
    }

}
