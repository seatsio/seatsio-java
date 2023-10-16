package seatsio.events;

import com.google.gson.JsonObject;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class CreateEventParams extends EventParams<CreateEventParams> {

    public List<Channel> channels;
    public ForSaleConfigParams forSaleConfigParams;

    public CreateEventParams withChannels(List<Channel> channels) {
        this.channels = channels;
        return this;
    }

    public CreateEventParams withForSaleConfigParams(ForSaleConfigParams forSaleConfigParams) {
        this.forSaleConfigParams = forSaleConfigParams;
        return this;
    }

    public List<JsonObject> getChannelsAsJson() {
        if (channels == null) {
            return null;
        }
        return channels.stream().map(Channel::toJson).collect(toList());
    }

    public JsonObject getForSaleConfigAsJson() {
        if (forSaleConfigParams == null) {
            return null;
        }
        return forSaleConfigParams.toJson();
    }
}
