package seatsio.util;

import com.google.gson.*;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequest;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static seatsio.json.SeatsioGson.gson;
import static seatsio.util.UnirestUtil.unirest;

public class PageFetcher<T> {

    private final String baseUrl;
    private final String url;
    private final String secretKey;
    private final Class<T> clazz;
    private Integer pageSize;
    private Map<String, Object> queryParams = new HashMap<>();

    public PageFetcher(String baseUrl, String url, String secretKey, Class<T> clazz) {
        this.baseUrl = baseUrl;
        this.url = url;
        this.secretKey = secretKey;
        this.clazz = clazz;
    }

    public Page<T> fetchFirstPage() {
        return fetch(buildRequest());
    }

    public Page<T> fetchAfter(long id) {
        HttpRequest request = buildRequest();
        request.queryString("start_after_id", id);
        return fetch(request);
    }

    public Page<T> fetchBefore(long id) {
        HttpRequest request = buildRequest();
        request.queryString("end_before_id", id);
        return fetch(request);
    }

    private HttpRequest buildRequest() {
        HttpRequest request = Unirest
                .get(baseUrl + "/" + url)
                .queryString(queryParams)
                .basicAuth(secretKey, null);
        if (pageSize != null) {
            request.queryString("limit", pageSize);
        }
        return request;
    }

    private Page<T> fetch(HttpRequest request) {
        HttpResponse<String> response = unirest(request::asString);
        JsonObject responseJson = new JsonParser().parse(response.getBody()).getAsJsonObject();
        List<T> items = asList(responseJson.getAsJsonArray("items"), clazz);
        return new Page<T>(
                items,
                optionalLong(responseJson.get("next_page_starts_after")),
                optionalLong(responseJson.get("previous_page_ends_before"))
        );
    }

    private Optional<Long> optionalLong(JsonElement element) {
        return element != null ? Optional.of(element.getAsJsonPrimitive().getAsLong()) : Optional.empty();
    }

    private static <T> List<T> asList(JsonArray jsonArray, Class<T> clazz) {
        Iterator<JsonElement> itemsIterator = jsonArray.iterator();
        Spliterator<JsonElement> itemsSpliterator = Spliterators.spliteratorUnknownSize(itemsIterator, Spliterator.ORDERED);
        return StreamSupport.stream(itemsSpliterator, false)
                .map(itemJson -> gson().fromJson(itemJson, clazz))
                .collect(Collectors.toList());
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    protected void setQueryParam(String name, String value) {
        this.queryParams.put(name, value);
    }
}
