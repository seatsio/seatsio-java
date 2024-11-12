package seatsio.events;

public class UpdateEventParams extends EventParams<UpdateEventParams> {

    public Boolean isInThePast;

    public UpdateEventParams withIsInThePast(Boolean isInThePast) {
        this.isInThePast = isInThePast;
        return this;
    }
}
