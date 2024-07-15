package seatsio.charts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChartListParams {

    private String filter;
    private String tag;
    private boolean expandEvents;
    private boolean expandValidation;
    private boolean expandVenueType;
    private boolean expandZones;

    public ChartListParams withFilter(String filter) {
        this.filter = filter;
        return this;
    }

    public ChartListParams withTag(String tag) {
        this.tag = tag;
        return this;
    }

    public ChartListParams withExpandEvents(boolean expandEvents) {
        this.expandEvents = expandEvents;
        return this;
    }

    public ChartListParams withExpandValidation(boolean expandValidation) {
        this.expandValidation = expandValidation;
        return this;
    }

    /**
     * @deprecated Use {@link #withExpandEvents(boolean)} instead
     */
    @Deprecated
    public ChartListParams withValidation(boolean validation) {
        return withExpandValidation(validation);
    }

    public ChartListParams withExpandVenueType(boolean expandVenueType) {
        this.expandVenueType = expandVenueType;
        return this;
    }

    public ChartListParams withExpandZones(boolean expandZones) {
        this.expandZones = expandZones;
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

        List<String> expand = expandParams();
        if (!expand.isEmpty()) {
            chartListParams.put("expand", expand);
        }

        return chartListParams;
    }

    private List<String> expandParams() {
        List<String> expandParams = new ArrayList<>();
        if (expandEvents) {
            expandParams.add("events");
        }
        if (expandValidation) {
            expandParams.add("validation");
        }
        if (expandVenueType) {
            expandParams.add("venueType");
        }
        if (expandZones) {
            expandParams.add("zones");
        }
        return expandParams;
    }
}
