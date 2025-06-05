package seatsio.ticketBuyers;

import java.util.UUID;

public class AddTicketBuyerIdsResponse {
    private final UUID[] added;
    private final UUID[] alreadyPresent;

    public AddTicketBuyerIdsResponse(UUID[] added, UUID[] alreadyPresent) {
        this.added = added;
        this.alreadyPresent = alreadyPresent;
    }

    public UUID[] getAdded() {
        return added;
    }

    public UUID[] getAlreadyPresent() {
        return alreadyPresent;
    }
}
