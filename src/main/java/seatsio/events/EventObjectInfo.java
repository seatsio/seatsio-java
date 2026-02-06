package seatsio.events;

import java.util.Map;

public record EventObjectInfo(String status, String label, Labels labels, IDs ids, String categoryLabel,
                              String categoryKey, String ticketType, String holdToken, String objectType,
                              Boolean bookAsAWhole, String orderId, boolean forSale, String section, String entrance,
                              Integer capacity, Integer numBooked, Integer numFree, Integer numHeld, Integer numSeats,
                              Integer numNotForSale, Map<?, ?> extraData, Boolean isAccessible, Boolean isCompanionSeat,
                              Boolean hasLiftUpArmrests, Boolean isHearingImpaired, Boolean isSemiAmbulatorySeat,
                              Boolean hasSignLanguageInterpretation, Boolean isPlusSize, Boolean hasRestrictedView, String displayedObjectType,
                              String parentDisplayedObjectType, String leftNeighbour, String rightNeighbour, boolean isAvailable,
                              String availabilityReason, String channel, Double distanceToFocalPoint,
                              Map<String, Map<String, Integer>> holds, Boolean variableOccupancy, Integer minOccupancy,
                              Integer maxOccupancy, int seasonStatusOverriddenQuantity, String zone, Floor floor,
                              String resaleListingId) {

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

}