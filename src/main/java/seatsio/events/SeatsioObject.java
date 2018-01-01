package seatsio.events;

import java.util.Map;

public class SeatsioObject {

    private String objectId;
    private String ticketType;
    private Integer quantity;
    private Map<?, ?> extraData;

    public SeatsioObject(String objectId) {
        this.objectId = objectId;
    }

    public SeatsioObject(String objectId, String ticketType, Map<?, ?> extraData) {
        this.objectId = objectId;
        this.ticketType = ticketType;
        this.extraData = extraData;
    }

    public SeatsioObject(String objectId, Map<?, ?> extraData) {
        this.objectId = objectId;
        this.extraData = extraData;
    }

    public SeatsioObject(String objectId, String ticketType) {
        this.objectId = objectId;
        this.ticketType = ticketType;
    }

    public SeatsioObject(String objectId, int quantity) {
        this.objectId = objectId;
        this.quantity = quantity;
    }

    public static SeatsioObject from(Object object) {
        if(object instanceof String) {
            return new SeatsioObject((String) object);
        }
        return (SeatsioObject) object;
    }
}
