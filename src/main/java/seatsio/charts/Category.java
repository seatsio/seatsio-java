package seatsio.charts;

import seatsio.util.ValueObject;

public class Category extends ValueObject {

    private final int key;
    private final String label;
    private final String color;

    public Category(int key, String label, String color) {
        this.key = key;
        this.label = label;
        this.color = color;
    }
}
