package seatsio.events;

/**
 * Not a Java enum, for forward compatibility: the backend may return area types that this
 * version of the library doesn't know about yet.
 */
public final class AreaType {

    public static final String GENERAL_ADMISSION = "generalAdmission";
    public static final String FIXED_OCCUPANCY = "fixedOccupancy";
    public static final String VARIABLE_OCCUPANCY = "variableOccupancy";

    private AreaType() {
    }
}
