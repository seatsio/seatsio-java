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
    public Set<String> disabledSeats;
    public Set<String> enabledSeats;

    SocialDistancingRuleset() {
    }

    public SocialDistancingRuleset(int index, String name, int numberOfDisabledSeatsToTheSides, boolean disableSeatsInFrontAndBehind, int numberOfDisabledAisleSeats, int maxGroupSize, Set<String> disabledSeats, Set<String> enabledSeats) {
        this.index = index;
        this.name = name;
        this.numberOfDisabledSeatsToTheSides = numberOfDisabledSeatsToTheSides;
        this.disableSeatsInFrontAndBehind = disableSeatsInFrontAndBehind;
        this.numberOfDisabledAisleSeats = numberOfDisabledAisleSeats;
        this.maxGroupSize = maxGroupSize;
        this.disabledSeats = disabledSeats;
        this.enabledSeats = enabledSeats;
    }

    public SocialDistancingRuleset(int index, String name) {
        this(index, name, 0, false, 0, 0, new HashSet<>(), new HashSet<>());
    }
}
