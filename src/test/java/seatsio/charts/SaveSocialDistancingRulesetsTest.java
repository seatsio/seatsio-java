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
        Map<String, SocialDistancingRuleset> rulesets = ImmutableMap.of(
                "ruleset1", new SocialDistancingRuleset(0, "My first ruleset", 1, true, 2, 1, 10, 0, true, false, newHashSet("A-1"), newHashSet("A-2")),
                "ruleset2", new SocialDistancingRuleset(1, "My second ruleset")
        );

        client.charts.saveSocialDistancingRulesets(chart.key, rulesets);

        Chart retrievedChart = client.charts.retrieve(chart.key);
        assertThat(retrievedChart.socialDistancingRulesets).isEqualTo(rulesets);
    }

    @Test
    public void saveFixedRuleset() {
        Chart chart = client.charts.create();
        Map<String, SocialDistancingRuleset> rulesets = ImmutableMap.of(
                "ruleset1", SocialDistancingRuleset.fixed(0, "My first ruleset", newHashSet("A-1"))
        );

        client.charts.saveSocialDistancingRulesets(chart.key, rulesets);

        Chart retrievedChart = client.charts.retrieve(chart.key);
        assertThat(retrievedChart.socialDistancingRulesets).isEqualTo(ImmutableMap.of("ruleset1", new SocialDistancingRuleset(0, "My first ruleset", 0, false, 0, 0, 0, 0, false, true, newHashSet("A-1"), newHashSet())));
    }

    @Test
    public void saveRuleBasedRuleset() {
        Chart chart = client.charts.create();
        Map<String, SocialDistancingRuleset> rulesets = ImmutableMap.of(
                "ruleset1", SocialDistancingRuleset.ruleBased(0, "My first ruleset", 1, true, 2, 1, 10, 0, true, newHashSet("A-1"), newHashSet("A-2"))
        );

        client.charts.saveSocialDistancingRulesets(chart.key, rulesets);

        Chart retrievedChart = client.charts.retrieve(chart.key);
        assertThat(retrievedChart.socialDistancingRulesets).isEqualTo(ImmutableMap.of("ruleset1", new SocialDistancingRuleset(0, "My first ruleset", 1, true, 2, 1, 10, 0, true, false, newHashSet("A-1"), newHashSet("A-2"))));
    }
}
