package seatsio.eventLog;

import seatsio.util.Page;
import seatsio.util.PageFetcher;
import seatsio.util.ParameterizedLister;
import seatsio.util.UnirestWrapper;

import java.util.Map;
import java.util.stream.Stream;

public class EventLog {

    private final String baseUrl;
    private final UnirestWrapper unirest;

    public EventLog(String baseUrl, UnirestWrapper unirest) {
        this.baseUrl = baseUrl;
        this.unirest = unirest;
    }

    public Stream<EventLogItem> listAll() {
        return list().all(Map.of());
    }

    public Page<EventLogItem> listFirstPage(Integer pageSize) {
        return list().firstPage(Map.of(), pageSize);
    }

    public Page<EventLogItem> listPageAfter(long id, Integer pageSize) {
        return list().pageAfter(id, Map.of(), pageSize);
    }

    public Page<EventLogItem> listPageBefore(long id, Integer pageSize) {
        return list().pageBefore(id, Map.of(), pageSize);
    }

    private ParameterizedLister<EventLogItem> list() {
        return new ParameterizedLister<>(new PageFetcher<>(baseUrl, "/event-log", unirest, EventLogItem.class));
    }
}
