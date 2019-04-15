package seatsio.charts;

import seatsio.util.ValueObject;
import java.util.List;

class ChartValidationChildren extends ValueObject {
    public String level;
    public String validatorKey;
}

public class ChartValidation extends ValueObject {
    public List<ChartValidationChildren> errors;
    public List<ChartValidationChildren> warnings;
}
