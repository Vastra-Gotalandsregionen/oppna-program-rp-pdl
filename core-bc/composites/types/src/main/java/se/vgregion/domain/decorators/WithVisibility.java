package se.vgregion.domain.decorators;

import se.vgregion.domain.pdl.Visibility;

import java.io.Serializable;

public class WithVisibility<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = -8251396162088104487L;

    public final Visibility visibility;
    public final T value;

    public WithVisibility(Visibility visibility, T value) {
        this.visibility = visibility;
        this.value = value;
    }

    public WithVisibility<T> mapVisibility(Visibility newVisibility) {
        return new WithVisibility<T>(newVisibility, value);
    }

    public <N extends Serializable> WithVisibility<N> mapValue(N newValue) {
        return new WithVisibility<N>(visibility, newValue);
    }

    // TODO: 2013-11-27 : Magnus Andersson > Add static methods to describe ENUM

    public Visibility getVisibility() {
        return visibility;
    }

    public T getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "WithVisibility{" +
                "lowestVisibility=" + visibility +
                ", value=" + value +
                '}';
    }
}
