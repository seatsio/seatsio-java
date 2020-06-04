package seatsio.events;

import seatsio.util.ValueObject;

import java.util.Set;

public class Channel extends ValueObject {

    public String key;
    public final String name;
    public final String color;
    public final int index;
    public Set<String> objects;

    public Channel(String name, String color, int index) {
        this.name = name;
        this.color = color;
        this.index = index;
    }
}
