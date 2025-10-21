package seatsio.events;

public class ObjectAndQuantity {

    private String object;
    private Integer quantity;

    public ObjectAndQuantity(String object) {
        this.object = object;
    }

    public ObjectAndQuantity(String object, Integer quantity) {
        this.object = object;
        this.quantity = quantity;
    }
}
