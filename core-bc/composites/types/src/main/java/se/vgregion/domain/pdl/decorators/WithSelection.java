package se.vgregion.domain.pdl.decorators;

import java.io.Serializable;

public class WithSelection<T extends Serializable> implements Serializable {
    public final String id;
    public final boolean selected;
    public final T value;

    private WithSelection(boolean selected, T value) {
        this(java.util.UUID.randomUUID().toString(), selected, value);
    }

    private WithSelection(String id, boolean selected, T value) {
        this.id = id;
        this.selected = selected;
        this.value = value;
    }

    public WithSelection<T> select(T newValue) {
        return new WithSelection<T>(id, true, newValue);
    }

    public WithSelection<T> deselect(T newValue) {
        return new WithSelection<T>(id, false, newValue);
    }

    public WithSelection<T> mapSelection(boolean newSelection) {
        return new WithSelection<T>(id, newSelection, value);
    }

    public WithSelection<T> mapValue(T newValue) {
        return new WithSelection<T>(id, selected, newValue);
    }

}
