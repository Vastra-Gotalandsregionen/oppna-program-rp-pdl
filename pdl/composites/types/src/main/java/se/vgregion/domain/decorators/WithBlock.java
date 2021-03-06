package se.vgregion.domain.decorators;

import java.io.Serializable;

/**
 * This class decorates a generic class with block information.
 * @param <T> Serializable decorator that should be decorated
 */
public class WithBlock<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = 42917220499451811L;

    public final T value;
    public final boolean initiallyBlocked;
    public final boolean blocked;

    private WithBlock(boolean blocked, T value) {
        this(blocked, blocked, value);
    }

    private WithBlock(boolean initiallyBlocked, boolean blocked, T value) {
        this.value = value;
        this.blocked = blocked;
        this.initiallyBlocked = initiallyBlocked;
    }

    public T getValue() {
        return value;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public WithBlock<T> mapBlocked(boolean newBlocked) {
        return new WithBlock<T>(initiallyBlocked, newBlocked, value);
    }

    public <N extends Serializable> WithBlock<N> mapValue(N newValue) {
        return new WithBlock<N>(initiallyBlocked, blocked, newValue);
    }

    public boolean isInitiallyBlocked() {
        return initiallyBlocked;
    }

    public static <F extends Serializable, F1 extends F> WithBlock<F> blocked(F1 value) {
        return new WithBlock<F>(true, value);

    }

    public static <S extends Serializable, S1 extends S> WithBlock<S> unblocked(S1 value) {
        return new WithBlock<S>(false, value);
    }

    @Override
    public String toString() {
        return "WithBlock{" +
                "value=" + value +
                ", initiallyBlocked=" + initiallyBlocked +
                ", blocked=" + blocked +
                '}';
    }
}


