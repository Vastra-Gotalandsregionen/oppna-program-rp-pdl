package se.vgregion.domain.pdl.decorators;

import se.vgregion.domain.pdl.InformationType;

import java.io.Serializable;

/**
 * This class is used to categorize what information type the containing value belongs to.
 */
public class WithInfoType<T extends Serializable> implements Serializable {
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

    public WithInfoType<T> mapInfoType(InformationType newInfoType) {
        return new WithInfoType<T>(newInfoType, value);
    }

    @Override
    public String toString() {
        return "WithInfoType{" +
                "informationType=" + informationType +
                ", value=" + value +
                '}';
    }
}
