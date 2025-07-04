package seatsio.ticketBuyers;

import seatsio.util.Lister;
import seatsio.util.PageFetcher;
import seatsio.util.UnirestWrapper;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static seatsio.json.JsonObjectBuilder.aJsonObject;
import static seatsio.json.SeatsioGson.gson;
import static seatsio.util.UnirestWrapper.delete;
import static seatsio.util.UnirestWrapper.post;

public class TicketBuyers {

    private final String baseUrl;
    private final UnirestWrapper unirest;

    public TicketBuyers(String baseUrl, UnirestWrapper unirest) {
        this.baseUrl = baseUrl;
        this.unirest = unirest;
    }

    public AddTicketBuyerIdsResponse add(UUID... uuids) {
        return this.add(Arrays.asList(uuids));
    }

    public AddTicketBuyerIdsResponse add(Collection<UUID> uuids) {
        String request = aJsonObject()
                .withProperty("ids", uuids.stream().filter(Objects::nonNull).map(UUID::toString).collect(toSet()))
                .buildAsString();
        String response = unirest.stringResponse(post(baseUrl + "/ticket-buyers").body(request));
        return gson().fromJson(response, AddTicketBuyerIdsResponse.class);
    }

    public RemoveTicketBuyerIdsResponse remove(UUID... uuids) {
        return this.remove(Arrays.asList(uuids));
    }

    public RemoveTicketBuyerIdsResponse remove(Collection<UUID> uuids) {
        String request = aJsonObject()
                .withProperty("ids", uuids.stream().filter(Objects::nonNull).map(UUID::toString).collect(toSet()))
                .buildAsString();
        String response = unirest.stringResponse(delete(baseUrl + "/ticket-buyers").body(request));
        return gson().fromJson(response, RemoveTicketBuyerIdsResponse.class);
    }

    public Stream<UUID> listAll() {
        return list().all();
    }

    private Lister<UUID> list() {
        return new Lister<>(new PageFetcher<>(baseUrl, "/ticket-buyers", unirest, UUID.class));
    }
}
