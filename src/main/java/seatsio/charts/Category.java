package seatsio.charts;

import seatsio.util.ValueObject;

public class Category extends ValueObject {

    private final CategoryKey key;
    private final String label;
    private final String color;
    private final Boolean accessible;

    public Category(CategoryKey key, String label, String color) {
        this.key = key;
        this.label = label;
        this.color = color;
        this.accessible = false;
    }

    public Category(CategoryKey key, String label, String color, Boolean accessible) {
        this.key = key;
        this.label = label;
        this.color = color;
        this.accessible = accessible;
    }
}
