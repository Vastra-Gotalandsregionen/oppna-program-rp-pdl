package se.vgregion.domain.decorators;

import se.vgregion.domain.pdl.InformationType;

import java.io.Serializable;

/**
 * This class is used to categorize what information decorator the containing value belongs to.
 */
public class WithInfoType<T extends Serializable> implements Serializable, Comparable<WithInfoType<T>> {
    private static final long serialVersionUID = 6882959945764167896L;

    public final InformationType informationType;
    public final T value;

    public WithInfoType(InformationType informationType, T value) {
        this.informationType = informationType;
        this.value = value;
    }

    public <N extends Serializable> WithInfoType<N> mapValue(N newValue) {
        return new WithInfoType<N>(informationType, newValue);
    }

    public WithInfoType<T> mapInformationType(InformationType newInformationType) {
        return new WithInfoType<T>(newInformationType, value);
    }

    public InformationType getInformationType() {
        return informationType;
    }

    public T getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "WithInfoType{" +
                "informationType=" + informationType +
                ", value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WithInfoType)) return false;

        WithInfoType that = (WithInfoType) o;

        if (informationType != that.informationType) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return informationType.hashCode();
    }

    @Override
    public int compareTo(WithInfoType<T> o) {
        return informationType.compareTo(o.informationType);
    }
}
