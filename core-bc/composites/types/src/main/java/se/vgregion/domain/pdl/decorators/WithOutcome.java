package se.vgregion.domain.pdl.decorators;

import se.vgregion.domain.pdl.Outcome;

import java.io.Serializable;

/**
 * This class is used to indicate if the containing value was produced from source systems
 * or via a fallback routine that sets a default value.
 */
public class WithOutcome<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = 4546387536561257956L;

    public final T value;
    public final Outcome outcome;

    private WithOutcome(Outcome status, T value) {
        this.value = value;
        this.outcome = status;
    }

    public T getValue() {
        return value;
    }

    public Outcome getOutcome() {
        return outcome;
    }

    public WithOutcome<T> mapOutcome(Outcome newOutcome) {
        return new WithOutcome<T>(newOutcome, value);
    }

    public <N extends Serializable> WithOutcome<N> mapValue(N newValue) {
        return new WithOutcome<N>(outcome, newValue);
    }

    public static<V extends Serializable, V1 extends V> WithOutcome<V> success(V value) {
        return new WithOutcome<V>(Outcome.SUCCESS, value);
    }

    public static<V extends Serializable, V1 extends V> WithOutcome<V> remoteFailure(V value) {
        return new WithOutcome<V>(Outcome.REMOTE_FAILURE, value);
    }


    public static<V extends Serializable, V1 extends V> WithOutcome<V> commFailure(V value) {
        return new WithOutcome<V>(Outcome.COMMUNICATION_FAILURE, value);
    }

    public static<V extends Serializable, V1 extends V> WithOutcome<V> clientError(V value) {
        return new WithOutcome<V>(Outcome.CLIENT_FAILURE, value);
    }

    @Override
    public String toString() {
        return "WithOutcome{" +
                "value=" + value +
                ", outcome=" + outcome +
                '}';
    }
}
