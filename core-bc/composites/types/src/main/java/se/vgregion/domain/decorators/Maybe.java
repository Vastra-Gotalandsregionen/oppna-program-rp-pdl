package se.vgregion.domain.decorators;

import java.io.Serializable;

public class Maybe<T extends Serializable> {
    public final T value;
    public final boolean isEmtpy;
    public final boolean success;

    private Maybe(T value, boolean success) {
        this.success = success && value != null;
        isEmtpy = !success;
        this.value = value;
    }

    public static <S extends Serializable, S1 extends S> Maybe<S> some(S1 value) {
        return new Maybe<S>(value, true);
    }

    public static <S extends Serializable, S1 extends S> Maybe<S> none() {
        return new Maybe<S>(null, false);
    }

    @Override
    public String toString() {
        return "Maybe{" +
                "value=" + value +
                ", isEmtpy=" + isEmtpy +
                ", success=" + success +
                '}';
    }
}
