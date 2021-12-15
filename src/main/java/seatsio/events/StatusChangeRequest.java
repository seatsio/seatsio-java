package seatsio.events;

import seatsio.util.ValueObject;

import java.util.List;
import java.util.Set;

public class StatusChangeRequest extends ValueObject {

    public final String eventKey;
    public final List<?> objects;
    public final String status;
    public final String holdToken;
    public final String orderId;
    public final Boolean keepExtraData;
    public final Boolean ignoreChannels;
    public final Set<String> channelKeys;
    public final Set<String> allowedPreviousStatuses;
    public final Set<String> rejectedPreviousStatuses;

    public StatusChangeRequest(String eventKey, List<?> objects, String status) {
        this(eventKey, objects, status, null, null, null, null, null, null, null);
    }

    public StatusChangeRequest(String eventKey, List<?> objects, String status, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys) {
        this(eventKey, objects, status, holdToken, orderId, keepExtraData, ignoreChannels, channelKeys, null, null);
    }

    public StatusChangeRequest(String eventKey, List<?> objects, String status, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys, Set<String> allowedPreviousStatuses, Set<String> rejectedPreviousStatuses) {
        this.eventKey = eventKey;
        this.objects = objects;
        this.status = status;
        this.holdToken = holdToken;
        this.orderId = orderId;
        this.keepExtraData = keepExtraData;
        this.ignoreChannels = ignoreChannels;
        this.channelKeys = channelKeys;
        this.allowedPreviousStatuses = allowedPreviousStatuses;
        this.rejectedPreviousStatuses = rejectedPreviousStatuses;
    }
}
