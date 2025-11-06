package seatsio.events;

public record ObjectAndQuantity(String object, Integer quantity) {

    public ObjectAndQuantity(String object) {
        this(object, null);
    }

}
