package seatsio.events;

import seatsio.util.ValueObject;

import java.util.List;
import java.util.Map;

public class BestAvailable extends ValueObject {

    private int number;
    private List<String> categories;
    private List<Map<String, Object>> extraData;

    public BestAvailable(int number) {
        this.number = number;
    }

    public BestAvailable(int number, List<String> categories) {
        this.number = number;
        this.categories = categories;
    }

    public BestAvailable(int number, List<String> categories, List<Map<String, Object>> extraData) {
        this.number = number;
        this.categories = categories;
        this.extraData = extraData;
    }

}
