package seatsio.events;

public class UpdateEventParams extends EventParams<UpdateEventParams> {

    public String chartKey;
    public Boolean isInThePast;

    public UpdateEventParams withChartKey(String chartKey) {
        this.chartKey = chartKey;
        return this;
    }

    public UpdateEventParams withIsInThePast(Boolean isInThePast) {
        this.isInThePast = isInThePast;
        return this;
    }
}
