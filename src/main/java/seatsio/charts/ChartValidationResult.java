package seatsio.charts;

import java.util.List;

public record ChartValidationResult(List<String> errors, List<String> warnings) {
}
