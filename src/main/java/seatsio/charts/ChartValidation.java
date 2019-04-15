package seatsio.charts;

import seatsio.util.ValueObject;
import java.util.List;

class ChartValidationItem extends ValueObject {
    public String level;
    public String validatorKey;
}

public class ChartValidation extends ValueObject {
    public List<ChartValidationItem> errors;
    public List<ChartValidationItem> warnings;
}
