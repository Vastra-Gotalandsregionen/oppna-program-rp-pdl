package se.vgregion.domain.pdl.decorators;

import java.io.Serializable;

public class WithChoise<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = 8898748571929625380L;

    public final String id;
    public final boolean chosen;
    public final T value;

    private WithChoise(boolean chosen, T value) {
        this.chosen = chosen;
        this.value = value;
        this.id = java.util.UUID.randomUUID().toString();
    }

    private WithChoise(String id, boolean chosen, T value) {
        this.chosen = chosen;
        this.value = value;
        this.id = id;
    }

    public static<V extends Serializable, V1 extends V> WithChoise<V> choosen(V1 value) {
        return new WithChoise<V>(true, value);
    }

    public static<V extends Serializable, V1 extends V> WithChoise<V> notChosen(V1 value) {
        return new WithChoise<V>(false, value);
    }

    public static<V extends Serializable, V1 extends V> WithChoise<V> getNotChosen(V1 value) {
        return new WithChoise<V>(false, value);
    }

    public WithChoise<T> mapChoise(boolean newChoise) {
        return new WithChoise<T>(newChoise, value);
    }

    public <N extends Serializable> WithChoise<N> mapValue(N newValue) {
        return new WithChoise<N>(chosen, newValue);
    }

    public String getId() {
        return id;
    }

    public boolean isChosen() {
        return chosen;
    }

    public T getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "WithChoise{" +
                "id='" + id + '\'' +
                ", chosen=" + chosen +
                ", value=" + value +
                '}';
    }
}
