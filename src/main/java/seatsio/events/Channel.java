package seatsio.events;

import seatsio.util.ValueObject;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public class Channel extends ValueObject {

    public String key;
    public final String name;
    public final String color;
    public final int index;
    public Set<String> objects = newHashSet();

    public Channel(String name, String color, int index) {
        this(null, name, color, index);
    }

    public Channel(String key, String name, String color, int index) {
        this.key = key;
        this.name = name;
        this.color = color;
        this.index = index;
    }
}
