package se.vgregion.domain.pdl;

public class WithBlocksConsent<T> {

    public final T value;
    public final boolean blocked;
    public final boolean consent;

    public WithBlocksConsent(T value, boolean blocked, boolean consent) {
        this.value = value;
        this.blocked = blocked;
        this.consent = consent;
    }

    public static <F, F1 extends F> WithBlocksConsent<F> blocked(F1 value) {
        return new WithBlocksConsent<F>(value, true, false);
    }

    public static <S, S1 extends S> WithBlocksConsent<S> success(S1 value) {
        return new WithBlocksConsent<S>(value, false, false);
    }

    public T getValue() {
        return value;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public boolean isConsent() {
        return consent;
    }

    public WithBlocksConsent<T> withBlocked() {
        return withBlocked(true);
    }

    public WithBlocksConsent<T> withBlocked(boolean newBlocked) {
        return new WithBlocksConsent<T>(value, newBlocked, consent);
    }

    public WithBlocksConsent<T> withConsent() {
        return withConsent(true);
    }

    public WithBlocksConsent<T> withConsent(boolean newConsent) {
        return new WithBlocksConsent<T>(value, blocked, newConsent);
    }

    @Override
    public String toString() {
        return "WithBlocksConsent{" +
                "value=" + value +
                ", blocked=" + blocked +
                ", consent=" + consent +
                '}';
    }
}


