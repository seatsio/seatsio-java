package seatsio.charts;

import seatsio.util.ValueObject;
import java.util.List;

public class ChartValidation extends ValueObject {
    public List<String> errors;
    public List<String> warnings;
}
