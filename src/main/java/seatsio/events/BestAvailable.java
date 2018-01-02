package seatsio.events;

import seatsio.util.ValueObject;

import java.util.List;

public class BestAvailable extends ValueObject {

    private int number;
    private List<String> categories;
    private boolean useObjectUuidsInsteadOfLabels;

    public BestAvailable(int number) {
        this.number = number;
    }

    public BestAvailable(int number, List<String> categories) {
        this.number = number;
        this.categories = categories;
    }

    public BestAvailable(int number, List<String> categories, boolean useObjectUuidsInsteadOfLabels) {
        this.number = number;
        this.categories = categories;
        this.useObjectUuidsInsteadOfLabels = useObjectUuidsInsteadOfLabels;
    }

    public BestAvailable(int number, boolean useObjectUuidsInsteadOfLabels) {
        this.number = number;
        this.useObjectUuidsInsteadOfLabels = useObjectUuidsInsteadOfLabels;
    }
}
