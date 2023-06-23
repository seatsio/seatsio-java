package seatsio.events;

public class UpdateEventParams extends EventParams<UpdateEventParams> {

    public String chartKey;

    public UpdateEventParams withChartKey(String chartKey) {
        this.chartKey = chartKey;
        return this;
    }
}
