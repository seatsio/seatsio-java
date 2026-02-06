package seatsio.charts;

import seatsio.events.Floor;
import seatsio.events.IDs;
import seatsio.events.Labels;

public record ChartObjectInfo(String label, Labels labels, IDs ids, String categoryLabel, String categoryKey,
                              String objectType, Boolean bookAsAWhole, String section, String entrance,
                              Integer capacity, String leftNeighbour, String rightNeighbour,
                              Double distanceToFocalPoint, Integer numSeats, Boolean isAccessible,
                              Boolean isCompanionSeat, Boolean hasLiftUpArmrests, Boolean isHearingImpaired,
                              Boolean isSemiAmbulatorySeat, Boolean hasSignLanguageInterpretation,
                              Boolean isPlusSize, Boolean hasRestrictedView, String zone, Floor floor) {

}
