package seatsio.events;

import seatsio.reports.events.EventReportItem;
import seatsio.util.ValueObject;

import java.util.List;
import java.util.Map;

public class BestAvailableResult extends ValueObject {

    public List<String> objects;
    public Map<String, EventReportItem> objectDetails;
    public boolean nextToEachOther;

}
