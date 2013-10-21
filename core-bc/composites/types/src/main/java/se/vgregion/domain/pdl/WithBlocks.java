package se.vgregion.domain.pdl;

public class WithBlocks<T> {

    public final T value;
    public final boolean blocked;

    public WithBlocks(T value, boolean blocked) {
        this.value = value;
        this.blocked = blocked;
    }

    public static <F, F1 extends F> WithBlocks<F> blocked(F1 value) {
        return new WithBlocks<F>(value, true);
    }

    public static <S, S1 extends S> WithBlocks<S> success(S1 value) {
        return new WithBlocks<S>(value, false);
    }

    public T getValue() {
        return value;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public WithBlocks<T> withBlocked() {
        return withBlocked(true);
    }

    public WithBlocks<T> withBlocked(boolean newBlocked) {
        return new WithBlocks<T>(value, blocked);
    }

    @Override
    public String toString() {
        return "WithBlocks{" +
                "value=" + value +
                ", blocked=" + blocked +
                '}';
    }
}


