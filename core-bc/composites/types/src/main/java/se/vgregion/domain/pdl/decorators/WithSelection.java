package se.vgregion.domain.pdl.decorators;

import java.io.Serializable;

public class WithSelection<T extends Serializable> implements Serializable, Comparable<WithSelection<T>> {
    private static final long serialVersionUID = -6925415052903676453L;

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

    public WithSelection<T> select() {
        return new WithSelection<T>(id, true, value);
    }

    public WithSelection<T> deselect() {
        return new WithSelection<T>(id, true, value);
    }

    public WithSelection<T> toggle() {
        return (selected) ? this.deselect() : this.select();
    }

    public static <N extends Serializable, N1 extends N> WithSelection<N> getDeselected(N1 value) {
        return new WithSelection<N>(false, value);
    }

    @Override
    public String toString() {
        return "WithSelection{" +
                "id='" + id + '\'' +
                ", selected=" + selected +
                ", value=" + value +
                '}';
    }

    public WithSelection<T> mapSelection(boolean newSelection) {
        return new WithSelection<T>(id, newSelection, value);
    }

    public WithSelection<T> mapValue(T newValue) {
        return new WithSelection<T>(id, selected, newValue);
    }

    public T getValue() {
        return value;
    }

    public String getId() {
        return id;
    }

    public boolean isSelected() {
        return selected;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof WithSelection)) return false;

        WithSelection that = (WithSelection) o;

        if (selected != that.selected) return false;
        if (!id.equals(that.id)) return false;
        if (!value.equals(that.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (selected ? 1 : 0);
        result = 31 * result + value.hashCode();
        return result;
    }

    @Override
    public int compareTo(WithSelection<T> o) {
        return id.compareTo(o.id);
    }
}
