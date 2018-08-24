package seatsio.eventreports;

import seatsio.util.ValueObject;

import java.util.Map;

public class EventReportItem extends ValueObject {

    public String status;
    public String label;
    public String categoryLabel;
    public Integer categoryKey;
    public String ticketType;
    public String holdToken;
    public String objectType;
    public String orderId;
    public boolean forSale;
    public String section;
    public String entrance;
    public Integer capacity;
    public Integer numBooked;
    public Map<?, ?> extraData;
}
