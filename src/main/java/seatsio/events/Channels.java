package seatsio.events;

import com.google.gson.JsonArray;
import seatsio.util.UnirestWrapper;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static seatsio.json.JsonArrayBuilder.aJsonArray;
import static seatsio.json.JsonObjectBuilder.aJsonObject;

public class Channels {

    private final String baseUrl;
    private final UnirestWrapper unirest;

    public Channels(String baseUrl, UnirestWrapper unirest) {
        this.baseUrl = baseUrl;
        this.unirest = unirest;
    }

    public void add(String eventKey, String channelKey, String name, String color, Integer index, Set<String> objects) {
        this.add(eventKey, List.of(new ChannelCreationParams(channelKey, name, color, index, objects)));
    }

    public void add(String eventKey, Collection<ChannelCreationParams> paramsList) {
        unirest.stringResponse(UnirestWrapper.post(baseUrl + "/events/{key}/channels")
                .routeParam("key", eventKey)
                .body(aJsonArray().withItems(paramsList.stream().map(ChannelCreationParams::toJson).collect(toList())).buildAsString())
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

    public void replace(String eventKey, List<Channel> channels) {
        unirest.stringResponse(UnirestWrapper.post(baseUrl + "/events/{key}/channels/replace")
                .routeParam("key", eventKey)
                .body(channelsToJson(channels))
        );
    }

    private static String channelsToJson(List<Channel> channels) {
        JsonArray channelsJson = new JsonArray();
        channels.forEach(channel -> channelsJson.add(channel.toJson()));
        return aJsonObject().withProperty("channels", channelsJson).buildAsString();
    }
}
