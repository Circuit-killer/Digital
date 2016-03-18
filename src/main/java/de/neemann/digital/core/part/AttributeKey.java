package de.neemann.digital.core.part;

/**
 * @author hneemann
 */
public class AttributeKey<VALUE> {

    public static final AttributeKey<Integer> Bits = new AttributeKey<>("Bits", 1);
    public static final AttributeKey<Integer> InputCount = new AttributeKey<>("Inputs", 2);
    public static final AttributeKey<String> Label = new AttributeKey<>("Label", "");

    private final String name;
    private final VALUE def;

    public AttributeKey(String name, VALUE def) {
        this.name = name;
        if (def == null)
            throw new NullPointerException();
        this.def = def;
    }

    public String getName() {
        return name;
    }

    public VALUE getDefault() {
        return def;
    }

    public Class getValueClass() {
        return def.getClass();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AttributeKey<?> that = (AttributeKey<?>) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}