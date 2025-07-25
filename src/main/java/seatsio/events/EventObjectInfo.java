package seatsio.events;

import seatsio.util.ValueObject;

import java.util.Map;

public class EventObjectInfo extends ValueObject {

    public static final String AVAILABLE = "available";
    public static final String NOT_AVAILABLE = "not_available";

    public static final String NOT_FOR_SALE = "not_for_sale";
    public static final String DISABLED_BY_SOCIAL_DISTANCING = "disabled_by_social_distancing";

    public static final String NO_CHANNEL = "NO_CHANNEL";
    public static final String NO_SECTION = "NO_SECTION";
    public static final String NO_ZONE = "NO_ZONE";
    public static final String NO_ORDER_ID = "NO_ORDER_ID";

    public static final String FREE = "free";
    public static final String BOOKED = "booked";
    public static final String HELD = "reservedByToken";
    public static final String RESALE = "resale";

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
    public Integer numSeats;
    public Integer numNotForSale;
    public Map<?, ?> extraData;
    public Boolean isAccessible;
    public Boolean isCompanionSeat;
    public Boolean hasRestrictedView;
    public String displayedObjectType;
    public String parentDisplayedObjectType;
    public String leftNeighbour;
    public String rightNeighbour;
    public boolean isAvailable;
    public String availabilityReason;
    public String channel;
    public Double distanceToFocalPoint;
    public Map<String, Map<String, Integer>> holds;
    public Boolean variableOccupancy;
    public Integer minOccupancy;
    public Integer maxOccupancy;
    public int seasonStatusOverriddenQuantity;
    public String zone;
    public Floor floor;
    public String resaleListingId;
}
