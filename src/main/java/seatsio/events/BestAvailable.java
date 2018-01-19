package seatsio.events;

import seatsio.util.ValueObject;

import java.util.List;

public class BestAvailable extends ValueObject {

    private int number;
    private List<String> categories;

    public BestAvailable(int number) {
        this.number = number;
    }

    public BestAvailable(int number, List<String> categories) {
        this.number = number;
        this.categories = categories;
    }

}
