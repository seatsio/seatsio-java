package seatsio.events;

import seatsio.util.ValueObject;

import java.util.List;
import java.util.Map;

public class BestAvailable extends ValueObject {

    private int number;
    private List<String> categories;
    private List<Map<String, Object>> extraData;
    private List<String> ticketTypes;

    public BestAvailable(int number) {
        this.number = number;
    }

    public BestAvailable(int number, List<String> categories) {
        this.number = number;
        this.categories = categories;
    }

    public BestAvailable(int number, List<String> categories, List<Map<String, Object>> extraData, List<String> ticketTypes) {
        this.number = number;
        this.categories = categories;
        this.extraData = extraData;
        this.ticketTypes = ticketTypes;
    }

}
