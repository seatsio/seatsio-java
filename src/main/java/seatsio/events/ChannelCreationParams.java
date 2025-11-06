package seatsio.events;

import com.google.gson.JsonObject;

import java.util.Set;

import static seatsio.json.JsonObjectBuilder.aJsonObject;

public record ChannelCreationParams(String key, String name, String color, Integer index, Set<String> objects) {

    public JsonObject toJson() {
        return aJsonObject()
                .withProperty("key", key)
                .withProperty("name", name)
                .withProperty("color", color)
                .withPropertyIfNotNull("index", index)
                .withPropertyIfNotNull("objects", objects)
                .build();
    }

    public static class Builder {

        private String key;
        private String name;
        private String color;
        private Integer index;
        private Set<String> objects;

        public Builder withKey(String channelKey) {
            this.key = channelKey;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withColor(String color) {
            this.color = color;
            return this;
        }

        public Builder withIndex(int index) {
            this.index = index;
            return this;
        }

        public Builder withObjects(Set<String> objectLabels) {
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
}
