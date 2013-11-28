package se.vgregion.domain.pdl.decorators;

import java.io.Serializable;

public class WithAccess<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = 2095996256275483120L;

    public final boolean otherProviders;
    public final T value;

    private WithAccess(boolean otherProviders, T value) {
        this.otherProviders = otherProviders;
        this.value = value;
    }

    public WithAccess<T> mapAccess(boolean newOtherProviders) {
        return new WithAccess<T>(newOtherProviders, value);
    }

    public <N extends Serializable> WithAccess<N> mapValue(N newValue) {
        return new WithAccess<N>(otherProviders, newValue);
    }

    public static <F extends Serializable, F1 extends F> WithAccess<F> withOtherProviders(F1 value) {
        return new WithAccess<F>(true, value);
    }

    public static <S extends Serializable, S1 extends S> WithAccess<S> sameProvider(S1 value) {
        return new WithAccess<S>(false, value);
    }

    public boolean isOtherProviders() {
        return otherProviders;
    }

    public T getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "WithAccess{" +
                "otherProviders=" + otherProviders +
                ", value=" + value +
                '}';
    }
}
