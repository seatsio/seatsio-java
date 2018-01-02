package seatsio.events;

import java.util.Map;

public class ObjectProperties {

    private String objectId;
    private String ticketType;
    private Integer quantity;
    private Map<?, ?> extraData;

    public ObjectProperties(String objectId) {
        this.objectId = objectId;
    }

    public ObjectProperties(String objectId, String ticketType, Map<?, ?> extraData) {
        this.objectId = objectId;
        this.ticketType = ticketType;
        this.extraData = extraData;
    }

    public ObjectProperties(String objectId, Map<?, ?> extraData) {
        this.objectId = objectId;
        this.extraData = extraData;
    }

    public ObjectProperties(String objectId, String ticketType) {
        this.objectId = objectId;
        this.ticketType = ticketType;
    }

    public ObjectProperties(String objectId, int quantity) {
        this.objectId = objectId;
        this.quantity = quantity;
    }

    public static ObjectProperties from(Object object) {
        if(object instanceof String) {
            return new ObjectProperties((String) object);
        }
        return (ObjectProperties) object;
    }
}
