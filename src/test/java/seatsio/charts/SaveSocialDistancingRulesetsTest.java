package seatsio.charts;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;

import java.util.Map;

import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;

public class SaveSocialDistancingRulesetsTest extends SeatsioClientTest {

    @Test
    public void saveRulesets() {
        Chart chart = client.charts.create();
        SocialDistancingRuleset ruleset1 = SocialDistancingRuleset.ruleBased("My first ruleset")
                .withIndex(0)
                .withNumberOfDisabledSeatsToTheSides(1)
                .withDisableSeatsInFrontAndBehind(true)
                .withDisableDiagonalSeatsInFrontAndBehind(true)
                .withNumberOfDisabledAisleSeats(2)
                .withMaxGroupSize(1)
                .withMaxOccupancyAbsolute(10)
                .withOneGroupPerTable(true)
                .withDisabledSeats(newHashSet("A-1"))
                .withEnabledSeats(newHashSet("A-2"))
                .build();
        SocialDistancingRuleset ruleset2 = SocialDistancingRuleset.fixed("My second ruleset")
                .withIndex(1)
                .withDisabledSeats(newHashSet("A-1"))
                .build();
        Map<String, SocialDistancingRuleset> rulesets = ImmutableMap.of(
                "ruleset1", ruleset1,
                "ruleset2", ruleset2
        );

        client.charts.saveSocialDistancingRulesets(chart.key, rulesets);

        Chart retrievedChart = client.charts.retrieve(chart.key);
        assertThat(retrievedChart.socialDistancingRulesets).isEqualTo(rulesets);
    }

}
