package se.vgregion.domain.decorators;

import java.io.Serializable;

public class Maybe<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = 3943760711697466919L;

    public final T value;
    public final boolean isEmtpy;
    public final boolean success;

    private Maybe(T value, boolean success) {
        this.success = success && value != null;
        isEmtpy = !success;
        this.value = value;
    }

    public static <S extends Serializable, S1 extends S> Maybe<S> some(S1 value) {
        boolean newSuccess = value != null;
        return new Maybe<S>(value, newSuccess);
    }

    public static <S extends Serializable, S1 extends S> Maybe<S> none() {
        return new Maybe<S>(null, false);
    }

    public <N extends Serializable, N1 extends N> Maybe<N> mapValue(N1 newValue) {
        boolean newSuccess = newValue != null;
        return new Maybe<N>(newValue, newSuccess);
    }

    public T getValue() {
        return value;
    }

    public boolean isEmtpy() {
        return isEmtpy;
    }

    public boolean isSuccess() {
        return success;
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
