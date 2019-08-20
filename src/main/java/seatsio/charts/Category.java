package seatsio.charts;

import seatsio.util.ValueObject;

public class Category extends ValueObject {

    private final CategoryKey key;
    private final String label;
    private final String color;

    public Category(CategoryKey key, String label, String color) {
        this.key = key;
        this.label = label;
        this.color = color;
    }
}
