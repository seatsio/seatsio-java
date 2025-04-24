package seatsio.events;

import seatsio.util.ValueObject;

import java.util.List;
import java.util.Set;

import static seatsio.events.StatusChangeType.CHANGE_STATUS_TO;

public class StatusChangeRequest extends ValueObject {

    public final StatusChangeType type;
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
    public final String resaleListingId;

    public StatusChangeRequest(StatusChangeType type, String eventKey, List<?> objects, String status, String holdToken, String orderId, Boolean keepExtraData, Boolean ignoreChannels, Set<String> channelKeys, Set<String> allowedPreviousStatuses, Set<String> rejectedPreviousStatuses, String resaleListingId) {
        this.type = type;
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
        this.resaleListingId = resaleListingId;
    }

    public static class Builder {
        private StatusChangeType type = CHANGE_STATUS_TO;
        private String eventKey;
        private List<?> objects;
        private String status;
        private String holdToken;
        private String orderId;
        private Boolean keepExtraData;
        private Boolean ignoreChannels;
        private Set<String> channelKeys;
        private Set<String> allowedPreviousStatuses;
        private Set<String> rejectedPreviousStatuses;
        private String resaleListingId;

        public Builder withType(StatusChangeType type) {
            this.type = type;
            return this;
        }

        public Builder withEventKey(String eventKey) {
            this.eventKey = eventKey;
            return this;
        }

        public Builder withObjects(List<?> objects) {
            this.objects = objects;
            return this;
        }

        public Builder withStatus(String status) {
            this.status = status;
            return this;
        }

        public Builder withHoldToken(String holdToken) {
            this.holdToken = holdToken;
            return this;
        }

        public Builder withOrderId(String orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder withKeepExtraData(Boolean keepExtraData) {
            this.keepExtraData = keepExtraData;
            return this;
        }

        public Builder withIgnoreChannels(Boolean ignoreChannels) {
            this.ignoreChannels = ignoreChannels;
            return this;
        }

        public Builder withChannelKeys(Set<String> channelKeys) {
            this.channelKeys = channelKeys;
            return this;
        }

        public Builder withAllowedPreviousStatuses(Set<String> allowedPreviousStatuses) {
            this.allowedPreviousStatuses = allowedPreviousStatuses;
            return this;
        }

        public Builder withRejectedPreviousStatuses(Set<String> rejectedPreviousStatuses) {
            this.rejectedPreviousStatuses = rejectedPreviousStatuses;
            return this;
        }

        public Builder withResaleListingId(String resaleListingId) {
            this.resaleListingId = resaleListingId;
            return this;
        }

        public StatusChangeRequest build() {
            return new StatusChangeRequest(type, eventKey, objects, status, holdToken, orderId, keepExtraData, ignoreChannels, channelKeys, allowedPreviousStatuses, rejectedPreviousStatuses, resaleListingId);
        }
    }
}
