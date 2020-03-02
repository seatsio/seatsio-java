package seatsio.events;

import seatsio.util.ValueObject;

import java.util.List;

public class StatusChangeRequest extends ValueObject {

    public final String eventKey;
    public final List<?> objects;
    public final String status;
    public final String holdToken;
    public final String orderId;
    public final Boolean keepExtraData;

    public StatusChangeRequest(String eventKey, List<?> objects, String status) {
        this(eventKey, objects, status, null, null, null);
    }

    public StatusChangeRequest(String eventKey, List<?> objects, String status, String holdToken, String orderId, Boolean keepExtraData) {
        this.eventKey = eventKey;
        this.objects = objects;
        this.status = status;
        this.holdToken = holdToken;
        this.orderId = orderId;
        this.keepExtraData = keepExtraData;
    }
}
