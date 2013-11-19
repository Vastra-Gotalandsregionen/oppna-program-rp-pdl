package se.vgregion.domain.pdl;

import java.io.Serializable;

/**
 * This class is used to indicate if the containing value was produced from source systems
 * or via a fallback routine that sets a default value.
 */
public class WithFallback<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = 4546387536561257956L;

    public final T value;
    public final boolean fallback;

    private WithFallback(boolean isFallback, T value) {
        this.value = value;
        fallback = isFallback;
    }

    public T getValue() {
        return value;
    }

    public boolean isFallback() {
        return fallback;
    }

    public WithFallback<T> mapFallback(boolean newFallback) {
        return new WithFallback<T>(newFallback, value);
    }

    public <N extends Serializable> WithFallback<N> mapValue(N newValue) {
        return new WithFallback<N>(fallback, newValue);
    }

    public static <F extends Serializable, F1 extends F> WithFallback<F> fallback(F1 value) {
        return new WithFallback<F>(true, value);
    }

    public static <S extends Serializable, S1 extends S> WithFallback<S> success(S1 value) {
        return new WithFallback<S>(false, value);
    }

    @Override
    public String toString() {
        return "WithFallback{" +
                "value=" + value +
                ", fallback=" + fallback +
                '}';
    }
}
