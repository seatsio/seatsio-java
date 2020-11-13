package seatsio.charts;

import seatsio.util.ValueObject;

import java.util.HashSet;
import java.util.Set;

public class SocialDistancingRuleset extends ValueObject {

    public int index;
    public String name;
    public int numberOfDisabledSeatsToTheSides;
    public boolean disableSeatsInFrontAndBehind;
    public boolean disableDiagonalSeatsInFrontAndBehind;
    public int numberOfDisabledAisleSeats;
    public int maxGroupSize;
    public int maxOccupancyAbsolute;
    public int maxOccupancyPercentage;
    public boolean oneGroupPerTable;
    public boolean fixedGroupLayout;
    public Set<String> disabledSeats = new HashSet<>();
    public Set<String> enabledSeats = new HashSet<>();

    public static RuleBasedSocialDistancingRulesetBuilder ruleBased(String name) {
        return new RuleBasedSocialDistancingRulesetBuilder(name);
    }

    public static FixedSocialDistancingRulesetBuilder fixed(String name) {
        return new FixedSocialDistancingRulesetBuilder(name);
    }

    public static class FixedSocialDistancingRulesetBuilder {

        public int index;
        public String name;
        public Set<String> disabledSeats = new HashSet<>();

        public FixedSocialDistancingRulesetBuilder(String name) {
            this.name = name;
        }

        public FixedSocialDistancingRulesetBuilder withIndex(int index) {
            this.index = index;
            return this;
        }

        public FixedSocialDistancingRulesetBuilder withDisabledSeats(Set<String> disabledSeats) {
            this.disabledSeats = disabledSeats;
            return this;
        }

        public SocialDistancingRuleset build() {
            SocialDistancingRuleset ruleset = new SocialDistancingRuleset();
            ruleset.index = index;
            ruleset.name = name;
            ruleset.fixedGroupLayout = true;
            ruleset.disabledSeats = disabledSeats;
            return ruleset;
        }
    }

    public static class RuleBasedSocialDistancingRulesetBuilder {

        public int index;
        public String name;
        public int numberOfDisabledSeatsToTheSides;
        public boolean disableSeatsInFrontAndBehind;
        public boolean disableDiagonalSeatsInFrontAndBehind;
        public int numberOfDisabledAisleSeats;
        public int maxGroupSize;
        public int maxOccupancyAbsolute;
        public int maxOccupancyPercentage;
        public boolean oneGroupPerTable;
        public Set<String> disabledSeats = new HashSet<>();
        public Set<String> enabledSeats = new HashSet<>();

        public RuleBasedSocialDistancingRulesetBuilder(String name) {
            this.name = name;
        }

        public RuleBasedSocialDistancingRulesetBuilder withIndex(int index) {
            this.index = index;
            return this;
        }

        public RuleBasedSocialDistancingRulesetBuilder withNumberOfDisabledSeatsToTheSides(int numberOfDisabledSeatsToTheSides) {
            this.numberOfDisabledSeatsToTheSides = numberOfDisabledSeatsToTheSides;
            return this;
        }

        public RuleBasedSocialDistancingRulesetBuilder withDisableSeatsInFrontAndBehind(boolean disableSeatsInFrontAndBehind) {
            this.disableSeatsInFrontAndBehind = disableSeatsInFrontAndBehind;
            return this;
        }

        public RuleBasedSocialDistancingRulesetBuilder withDisableDiagonalSeatsInFrontAndBehind(boolean disableDiagonalSeatsInFrontAndBehind) {
            this.disableDiagonalSeatsInFrontAndBehind = disableDiagonalSeatsInFrontAndBehind;
            return this;
        }

        public RuleBasedSocialDistancingRulesetBuilder withNumberOfDisabledAisleSeats(int numberOfDisabledAisleSeats) {
            this.numberOfDisabledAisleSeats = numberOfDisabledAisleSeats;
            return this;
        }

        public RuleBasedSocialDistancingRulesetBuilder withMaxGroupSize(int maxGroupSize) {
            this.maxGroupSize = maxGroupSize;
            return this;
        }

        public RuleBasedSocialDistancingRulesetBuilder withMaxOccupancyAbsolute(int maxOccupancyAbsolute) {
            this.maxOccupancyAbsolute = maxOccupancyAbsolute;
            return this;
        }

        public RuleBasedSocialDistancingRulesetBuilder withMaxOccupancyPercentage(int maxOccupancyPercentage) {
            this.maxOccupancyPercentage = maxOccupancyPercentage;
            return this;
        }

        public RuleBasedSocialDistancingRulesetBuilder withOneGroupPerTable(boolean oneGroupPerTable) {
            this.oneGroupPerTable = oneGroupPerTable;
            return this;
        }

        public RuleBasedSocialDistancingRulesetBuilder withDisabledSeats(Set<String> disabledSeats) {
            this.disabledSeats = disabledSeats;
            return this;
        }

        public RuleBasedSocialDistancingRulesetBuilder withEnabledSeats(Set<String> enabledSeats) {
            this.enabledSeats = enabledSeats;
            return this;
        }

        public SocialDistancingRuleset build() {
            SocialDistancingRuleset ruleset = new SocialDistancingRuleset();
            ruleset.index = index;
            ruleset.name = name;
            ruleset.numberOfDisabledSeatsToTheSides = numberOfDisabledSeatsToTheSides;
            ruleset.disableSeatsInFrontAndBehind = disableSeatsInFrontAndBehind;
            ruleset.disableDiagonalSeatsInFrontAndBehind = disableDiagonalSeatsInFrontAndBehind;
            ruleset.maxGroupSize = maxGroupSize;
            ruleset.maxOccupancyAbsolute = maxOccupancyAbsolute;
            ruleset.maxOccupancyPercentage = maxOccupancyPercentage;
            ruleset.oneGroupPerTable = oneGroupPerTable;
            ruleset.fixedGroupLayout = false;
            ruleset.disabledSeats = disabledSeats;
            ruleset.enabledSeats = enabledSeats;
            return ruleset;
        }
    }
}
