package seatsio.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kong.unirest.HttpRequest;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static seatsio.json.SeatsioGson.gson;
import static seatsio.util.UnirestUtil.stringResponse;

public class PageFetcher<T> {

    private final String baseUrl;
    private final String workspaceKey;
    private final String url;
    private final Map<String, String> routeParams;
    private final Map<String, String> queryParams;
    private final String secretKey;
    private final Class<T> clazz;

    public PageFetcher(String baseUrl, String url, String secretKey, Class<T> clazz) {
        this.baseUrl = baseUrl;
        this.workspaceKey = null;
        this.url = url;
        this.routeParams = new HashMap<>();
        this.queryParams = new HashMap<>();
        this.secretKey = secretKey;
        this.clazz = clazz;
    }

    public PageFetcher(String baseUrl, String url, String secretKey, String workspaceKey, Class<T> clazz) {
        this.baseUrl = baseUrl;
        this.workspaceKey = workspaceKey;
        this.url = url;
        this.routeParams = new HashMap<>();
        this.queryParams = new HashMap<>();
        this.secretKey = secretKey;
        this.clazz = clazz;
    }

    public PageFetcher(String baseUrl, String url, Map<String, String> routeParams, String secretKey, String workspaceKey, Class<T> clazz) {
        this.baseUrl = baseUrl;
        this.workspaceKey = workspaceKey;
        this.url = url;
        this.routeParams = routeParams;
        this.queryParams = new HashMap<>();
        this.secretKey = secretKey;
        this.clazz = clazz;
    }

    public PageFetcher(String baseUrl, String url, Map<String, String> routeParams, Map<String, String> queryParams, String secretKey, String workspaceKey, Class<T> clazz) {
        this.baseUrl = baseUrl;
        this.workspaceKey = workspaceKey;
        this.url = url;
        this.queryParams = queryParams;
        this.routeParams = routeParams;
        this.secretKey = secretKey;
        this.clazz = clazz;
    }

    public Page<T> fetchFirstPage(Map<String, Object> parameters, Integer pageSize) {
        return fetch(buildRequest(parameters, pageSize));
    }

    public Page<T> fetchAfter(long id, Map<String, Object> parameters, Integer pageSize) {
        HttpRequest request = buildRequest(parameters, pageSize);
        request.queryString("start_after_id", id);
        return fetch(request);
    }

    public Page<T> fetchBefore(long id, Map<String, Object> parameters, Integer pageSize) {
        HttpRequest request = buildRequest(parameters, pageSize);
        request.queryString("end_before_id", id);
        return fetch(request);
    }

    private HttpRequest buildRequest(Map<String, Object> parameters, Integer pageSize) {
        HttpRequest request = UnirestUtil.get(baseUrl + "/" + url, secretKey, workspaceKey)
                .queryString(parameters);
        routeParams.forEach(request::routeParam);
        queryParams.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .forEach(entry -> request.queryString(entry.getKey(), entry.getValue()));
        if (pageSize != null) {
            request.queryString("limit", pageSize);
        }
        return request;
    }

    private Page<T> fetch(HttpRequest request) {
        String response = stringResponse(request);
        JsonObject responseJson = JsonParser.parseString(response).getAsJsonObject();
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

}
