package seatsio.events;

import seatsio.util.ValueObject;

public class EventReportItem extends ValueObject {

    public String status;
    public String label;
    public String uuid;
    public String categoryLabel;
    public Integer categoryKey;
    public String ticketType;
    public String orderId;
    public boolean forSale;
    public String section;
    public String entrance;
    public Integer capacity;
    public Integer numBooked;
}
