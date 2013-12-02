package se.vgregion.domain.pdl.decorators;

import se.vgregion.domain.pdl.Visibility;

import java.io.Serializable;

public class UserInteractionState<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = 4032223817595569992L;

    public final String id;
    public final boolean initiallyBlocked;
    public final boolean blocked;
    public final boolean selected;
    public final Visibility visibility;
    public final T value;

    private UserInteractionState(String id, boolean initiallyBlocked, boolean blocked, boolean selected, Visibility visibility, T value) {
        this.id = id;
        this.initiallyBlocked = initiallyBlocked;
        this.blocked = blocked;
        this.selected = selected;
        this.visibility = visibility;
        this.value = value;
    }

    public static <N extends Serializable, N1 extends N> UserInteractionState<N> flattenAddSelection(WithVisibility<WithBlock<N1>> hierarchy) {
        WithSelection<N1> ws = WithSelection.getDeselected(hierarchy.value.value);
        WithBlock<WithSelection<N1>> wb = hierarchy.value.mapValue(ws);
        WithVisibility<WithBlock<WithSelection<N1>>> vs = hierarchy.mapValue(wb);

        return new UserInteractionState<N>(
            vs.value.value.id,
            vs.value.initiallyBlocked,
            vs.value.blocked,
            vs.value.value.selected,
            vs.visibility,
            vs.value.value.value
        );
    }

    public UserInteractionState<T> select() {
        return new UserInteractionState<T>(
            id,
            initiallyBlocked,
            blocked,
            true,
            visibility,
            value
        );
    }

    public UserInteractionState<T> deselect() {
        return new UserInteractionState<T>(
                id,
                initiallyBlocked,
                blocked,
                false,
                visibility,
                value
        );
    }

    public UserInteractionState<T> unblock() {
        return new UserInteractionState<T>(
                id,
                initiallyBlocked,
                false,
                true,
                visibility,
                value
        );
    }

    public String getId() {
        return id;
    }

    public boolean isInitiallyBlocked() {
        return initiallyBlocked;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public boolean isSelected() {
        return selected;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public T getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "UserInteractionState{" +
                "id='" + id + '\'' +
                ", initiallyBlocked=" + initiallyBlocked +
                ", blocked=" + blocked +
                ", selected=" + selected +
                ", visibility=" + visibility +
                ", value=" + value +
                '}';
    }
}
