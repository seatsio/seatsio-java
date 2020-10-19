package seatsio.charts;

import seatsio.util.ValueObject;

import java.util.HashSet;
import java.util.Set;

public class SocialDistancingRuleset extends ValueObject {

    public int index;
    public String name;
    public int numberOfDisabledSeatsToTheSides;
    public boolean disableSeatsInFrontAndBehind;
    public int numberOfDisabledAisleSeats;
    public int maxGroupSize;
    public int maxOccupancyAbsolute;
    public int maxOccupancyPercentage;
    public boolean oneGroupPerTable;
    public boolean fixedGroupLayout;
    public Set<String> disabledSeats;
    public Set<String> enabledSeats;

    SocialDistancingRuleset() {
    }

    public SocialDistancingRuleset(int index, String name, int numberOfDisabledSeatsToTheSides, boolean disableSeatsInFrontAndBehind, int numberOfDisabledAisleSeats, int maxGroupSize, int maxOccupancyAbsolute, int maxOccupancyPercentage, boolean oneGroupPerTable, boolean fixedGroupLayout, Set<String> disabledSeats, Set<String> enabledSeats) {
        this.index = index;
        this.name = name;
        this.numberOfDisabledSeatsToTheSides = numberOfDisabledSeatsToTheSides;
        this.disableSeatsInFrontAndBehind = disableSeatsInFrontAndBehind;
        this.numberOfDisabledAisleSeats = numberOfDisabledAisleSeats;
        this.maxGroupSize = maxGroupSize;
        this.maxOccupancyAbsolute = maxOccupancyAbsolute;
        this.maxOccupancyPercentage = maxOccupancyPercentage;
        this.oneGroupPerTable = oneGroupPerTable;
        this.fixedGroupLayout = fixedGroupLayout;
        this.disabledSeats = disabledSeats;
        this.enabledSeats = enabledSeats;
    }

    public SocialDistancingRuleset(int index, String name) {
        this(index, name, 0, false, 0, 0, 0, 0, false, false, new HashSet<>(), new HashSet<>());
    }

    public static SocialDistancingRuleset ruleBased(int index, String name, int numberOfDisabledSeatsToTheSides, boolean disableSeatsInFrontAndBehind, int numberOfDisabledAisleSeats, int maxGroupSize, int maxOccupancyAbsolute, int maxOccupancyPercentage, boolean oneGroupPerTable, Set<String> disabledSeats, Set<String> enabledSeats) {
        return new SocialDistancingRuleset(index, name, numberOfDisabledSeatsToTheSides, disableSeatsInFrontAndBehind, numberOfDisabledAisleSeats, maxGroupSize, maxOccupancyAbsolute, maxOccupancyPercentage, oneGroupPerTable, false, disabledSeats, enabledSeats);
    }

    public static SocialDistancingRuleset fixed(int index, String name, Set<String> disabledSeats) {
        return new SocialDistancingRuleset(index, name, 0, false, 0, 0, 0, 0, false, true, disabledSeats, new HashSet<>());
    }
}
