package se.vgregion.domain.pdl;

import java.io.Serializable;

public class WithBlocks<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = 42917220499451811L;

    public final T value;
    public final boolean blocked;

    public WithBlocks(T value, boolean blocked) {
        this.value = value;
        this.blocked = blocked;
    }

    public static <F extends Serializable, F1 extends F> WithBlocks<F> blocked(F1 value) {
        return new WithBlocks<F>(value, true);
    }

    public static <S extends Serializable, S1 extends S> WithBlocks<S> success(S1 value) {
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


