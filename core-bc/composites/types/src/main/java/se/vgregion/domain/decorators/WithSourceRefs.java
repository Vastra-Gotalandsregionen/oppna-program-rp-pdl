package se.vgregion.domain.decorators;

import se.vgregion.domain.source.SourceReferences;

import java.io.Serializable;

public class WithSourceRefs<T extends Serializable> implements Serializable  {
    private static final long serialVersionUID = 4879933908914535991L;

    public final SourceReferences visibility;
    public final T value;

    public WithSourceRefs(SourceReferences visibility, T value) {
        this.visibility = visibility;
        this.value = value;
    }

    public WithSourceRefs<T> mapVisibility(SourceReferences newRefs) {
        return new WithSourceRefs<T>(newRefs, value);
    }

    public <N extends Serializable> WithSourceRefs<N> mapValue(N newValue) {
        return new WithSourceRefs<N>(visibility, newValue);
    }

    public SourceReferences getVisibility() {
        return visibility;
    }

    public T getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "WithSourceRefs{" +
                "visibility=" + visibility +
                ", value=" + value +
                '}';
    }
}
