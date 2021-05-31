package seatsio.reports.events;

import seatsio.events.IDs;
import seatsio.events.Labels;
import seatsio.util.ValueObject;

import java.util.Map;

public class EventReportItem extends ValueObject {

    public static final String SELECTABLE = "selectable";
    public static final String NOT_SELECTABLE = "not_selectable";

    public static final String NO_CHANNEL = "NO_CHANNEL";
    public static final String NO_SECTION = "NO_SECTION";
    public static final String NO_ORDER_ID = "NO_ORDER_ID";

    public String status;
    public String label;
    public Labels labels;
    public IDs ids;
    public String categoryLabel;
    public String categoryKey;
    public String ticketType;
    public String holdToken;
    public String objectType;
    public Boolean bookAsAWhole;
    public String orderId;
    public boolean forSale;
    public String section;
    public String entrance;
    public Integer capacity;
    public Integer numBooked;
    public Integer numFree;
    public Integer numHeld;
    public Map<?, ?> extraData;
    public Boolean isAccessible;
    public Boolean isCompanionSeat;
    public Boolean hasRestrictedView;
    public String displayedObjectType;
    public String leftNeighbour;
    public String rightNeighbour;
    public boolean isSelectable;
    public Boolean isDisabledBySocialDistancing;
    public String channel;
    public Double distanceToFocalPoint;

}
