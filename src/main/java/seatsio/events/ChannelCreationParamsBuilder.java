package seatsio.events;

import java.util.Set;

public class ChannelCreationParamsBuilder {

    private String key;
    private String name;
    private String color;
    private Integer index;
    private Set<String> objects;

    /**
     * Private. Use the static aChannel() initializer instead
     */
    private ChannelCreationParamsBuilder() {
    }

    public static ChannelCreationParamsBuilder aChannel() {
        return new ChannelCreationParamsBuilder();
    }

    public ChannelCreationParamsBuilder withKey(String channelKey) {
        this.key = channelKey;
        return this;
    }

    public ChannelCreationParamsBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ChannelCreationParamsBuilder withColor(String color) {
        this.color = color;
        return this;
    }

    public ChannelCreationParamsBuilder withIndex(int index) {
        this.index = index;
        return this;
    }

    public ChannelCreationParamsBuilder withObjects(Set<String> objectLabels) {
        this.objects = objectLabels;
        return this;
    }

    public ChannelCreationParams build() {
        return new ChannelCreationParams(
                this.key,
                this.name,
                this.color,
                this.index,
                this.objects
        );
    }
}
