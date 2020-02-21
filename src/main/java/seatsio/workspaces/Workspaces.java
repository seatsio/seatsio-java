package seatsio.workspaces;

import com.mashape.unirest.http.HttpResponse;
import seatsio.json.JsonObjectBuilder;
import seatsio.util.Lister;
import seatsio.util.Page;
import seatsio.util.PageFetcher;
import seatsio.util.UnirestUtil;

import java.util.stream.Stream;

import static seatsio.json.JsonObjectBuilder.aJsonObject;
import static seatsio.json.SeatsioGson.gson;
import static seatsio.util.UnirestUtil.stringResponse;

public class Workspaces {

    private final String secretKey;
    private final String baseUrl;

    public Workspaces(String secretKey, String baseUrl) {
        this.secretKey = secretKey;
        this.baseUrl = baseUrl;
    }

    public Workspace create(String name) {
        return create(name, false);
    }

    public Workspace create(String name, Boolean isTest) {
        JsonObjectBuilder request = aJsonObject()
                .withProperty("name", name)
                .withPropertyIfNotNull("isTest", isTest);

        HttpResponse<String> response = stringResponse(UnirestUtil.post(baseUrl + "/workspaces", secretKey)
                .body(request.build().toString()));

        return gson().fromJson(response.getBody(), Workspace.class);
    }

    public void update(String key, String name) {
        JsonObjectBuilder request = aJsonObject()
                .withProperty("name", name);

        stringResponse(UnirestUtil.post(baseUrl + "/workspaces/{key}", secretKey)
                .routeParam("key", key)
                .body(request.build().toString()));
    }

    public Workspace retrieve(String key) {
        HttpResponse<String> response = stringResponse(UnirestUtil.get(baseUrl + "/workspaces/{key}", secretKey)
                .routeParam("key", key));
        return gson().fromJson(response.getBody(), Workspace.class);
    }

    public Stream<Workspace> listAll() {
        return list().all();
    }

    public Page<Workspace> listFirstPage() {
        return listFirstPage(null);
    }

    public Page<Workspace> listFirstPage(Integer pageSize) {
        return list().firstPage(pageSize);
    }

    public Page<Workspace> listPageAfter(long id) {
        return listPageAfter(id, null);
    }

    public Page<Workspace> listPageAfter(long id, Integer pageSize) {
        return list().pageAfter(id, pageSize);
    }

    public Page<Workspace> listPageBefore(long id) {
        return listPageBefore(id, null);
    }

    public Page<Workspace> listPageBefore(long id, Integer pageSize) {
        return list().pageBefore(id, pageSize);
    }

    private Lister<Workspace> list() {
        return new Lister<>(new PageFetcher<>(baseUrl, "/workspaces", secretKey, Workspace.class));
    }

}
