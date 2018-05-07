package seatsio.charts;

import java.util.HashMap;
import java.util.Map;

public class ChartListParams {

    private String filter;
    private String tag;
    private Boolean expandEvents;

    public ChartListParams withFilter(String filter) {
        this.filter = filter;
        return this;
    }

    public ChartListParams withTag(String tag) {
        this.tag = tag;
        return this;
    }

    public ChartListParams withExpandEvents(Boolean expandEvents) {
        this.expandEvents = expandEvents;
        return this;
    }

    public Map<String, Object> asMap() {
        Map<String, Object> chartListParams = new HashMap<>();

        if (filter != null) {
            chartListParams.put("filter", filter);
        }

        if (tag != null) {
            chartListParams.put("tag", tag);
        }

        if (expandEvents != null && expandEvents) {
            chartListParams.put("expand", "events");
        }

        return chartListParams;
    }

}
