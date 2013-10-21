package se.vgregion.domain.pdl;

/**
 * This class is used to indicate if the containing value was produced from source systems
 * or via a fallback routine that sets a default value.
 */
public class WithFallback<T> {
    public final T value;
    public final boolean fallback;

    public WithFallback(T value, boolean isFallback) {
        this.value = value;
        fallback = isFallback;
    }

    public T getValue() {
        return value;
    }

    public boolean isFallback() {
        return fallback;
    }

    public static <F, F1 extends F> WithFallback<F> fallback(F1 value) {
        return new WithFallback<F>(value, true);
    }

    public static <S, S1 extends S> WithFallback<S> success(S1 value) {
        return new WithFallback<S>(value, false);
    }

    @Override
    public String toString() {
        return "WithFallback{" +
                "value=" + value +
                ", fallback=" + fallback +
                '}';
    }
}
