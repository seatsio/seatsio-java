package seatsio.events;

import seatsio.json.JsonObjectBuilder;
import seatsio.util.UnirestWrapper;

import java.util.Map;
import java.util.Set;

import static seatsio.json.JsonObjectBuilder.aJsonObject;

public class Channels {

    private final String baseUrl;
    private final UnirestWrapper unirest;

    public Channels(String baseUrl, UnirestWrapper unirest) {
        this.baseUrl = baseUrl;
        this.unirest = unirest;
    }

    public void add(String eventKey, String channelKey, String name, String color, Integer index, Set<String> objects) {
        unirest.stringResponse(UnirestWrapper.post(baseUrl + "/events/{key}/channels")
                .routeParam("key", eventKey)
                .body(aJsonObject()
                        .withProperty("key", channelKey)
                        .withProperty("name", name)
                        .withProperty("color", color)
                        .withPropertyIfNotNull("index", index)
                        .withPropertyIfNotNull("objects", objects)
                        .buildAsString())
        );
    }

    public void remove(String eventKey, String channelKey) {
        unirest.stringResponse(UnirestWrapper.delete(baseUrl + "/events/{eventKey}/channels/{channelKey}")
                .routeParam("eventKey", eventKey)
                .routeParam("channelKey", channelKey)
        );
    }

    public void update(String eventKey, String channelKey, String name, String color, Set<String> objects) {
        unirest.stringResponse(UnirestWrapper.post(baseUrl + "/events/{eventKey}/channels/{channelKey}")
                .routeParam("eventKey", eventKey)
                .routeParam("channelKey", channelKey)
                .body(aJsonObject()
                        .withPropertyIfNotNull("name", name)
                        .withPropertyIfNotNull("color", color)
                        .withPropertyIfNotNull("objects", objects)
                        .buildAsString())
        );
    }

    public void addObjects(String eventKey, String channelKey, Set<String> objects) {
        unirest.stringResponse(UnirestWrapper.post(baseUrl + "/events/{eventKey}/channels/{channelKey}/objects")
                .routeParam("eventKey", eventKey)
                .routeParam("channelKey", channelKey)
                .body(aJsonObject()
                        .withProperty("objects", objects)
                        .buildAsString())
        );
    }

    public void removeObjects(String eventKey, String channelKey, Set<String> objects) {
        unirest.stringResponse(UnirestWrapper.delete(baseUrl + "/events/{eventKey}/channels/{channelKey}/objects")
                .routeParam("eventKey", eventKey)
                .routeParam("channelKey", channelKey)
                .body(aJsonObject()
                        .withProperty("objects", objects)
                        .buildAsString())
        );
    }

    public void replace(String eventKey, Map<String, Channel> channels) {
        unirest.stringResponse(UnirestWrapper.post(baseUrl + "/events/{key}/channels/update")
                .routeParam("key", eventKey)
                .body(replaceChannelsRequest(channels))
        );
    }

    private String replaceChannelsRequest(Map<String, Channel> channels) {
        return aJsonObject()
                .withProperty("channels", aJsonObject()
                        .withProperties(channels, channel -> aJsonObject()
                                .withProperty("name", channel.name)
                                .withProperty("color", channel.color)
                                .withProperty("index", channel.index)
                                .build())
                        .build()
                ).buildAsString();
    }

    public void setObjects(String key, Map<String, Set<String>> channelKeysAndObjectLabels) {
        unirest.stringResponse(UnirestWrapper.post(baseUrl + "/events/{key}/channels/assign-objects")
                .routeParam("key", key)
                .body(assignChannelsRequest(channelKeysAndObjectLabels))
        );
    }

    private String assignChannelsRequest(Map<String, Set<String>> channelKeysAndObjectLabels) {
        JsonObjectBuilder config = aJsonObject();
        channelKeysAndObjectLabels.forEach(config::withProperty);
        return aJsonObject().withProperty("channelConfig", config.build()).buildAsString();
    }
}
