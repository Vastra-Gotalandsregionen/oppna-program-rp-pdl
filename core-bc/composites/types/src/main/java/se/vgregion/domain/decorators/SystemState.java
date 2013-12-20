package se.vgregion.domain.decorators;

import se.vgregion.domain.pdl.Visibility;

import java.io.Serializable;

public class SystemState<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = 4032223817595569992L;

    // InitiallyBlocked + Selected == true => System has been unblocked and is now selected was blocked but is now successed
    // blocked == false CareGiver+CareUnit has been unblocked for this information type

    public final String id;
    public final boolean initiallyBlocked;
    public final boolean blocked;
    public final boolean selected;
    public final Visibility visibility;
    public final T value;
    public final boolean needConfirmation;

    private SystemState(
            String id,
            boolean initiallyBlocked,
            boolean blocked,
            boolean needConfirmation,
            boolean selected,
            Visibility visibility,
            T value
    ) {
        this.id = id;
        this.initiallyBlocked = initiallyBlocked;
        this.blocked = blocked;
        this.needConfirmation = needConfirmation;
        this.selected = selected;
        this.visibility = visibility;
        this.value = value;
    }

    public static <N extends Serializable, N1 extends N> SystemState<N> flattenAddSelection(WithVisibility<WithBlock<N1>> hierarchy) {
        return new SystemState<N>(
            java.util.UUID.randomUUID().toString(),
            hierarchy.value.initiallyBlocked,
            hierarchy.value.blocked,
            false,
            false,
            hierarchy.visibility,
            hierarchy.value.value
        );
    }

    public SystemState<T> select() {
        return new SystemState<T>(
            id,
            initiallyBlocked,
            blocked,
            needConfirmation,
            true,
            visibility,
            value
        );
    }

    public SystemState<T> deselect() {
        return new SystemState<T>(
                id,
                initiallyBlocked,
                blocked,
                needConfirmation,
                false,
                visibility,
                value
        );
    }

    public SystemState<T> unblock() {
        return new SystemState<T>(
                id,
                initiallyBlocked,
                false,
                needConfirmation,
                true,
                visibility,
                value
        );
    }

    public SystemState<T> needConfirmation() {
        return new SystemState<T>(
                id,
                initiallyBlocked,
                blocked,
                true,
                false,
                visibility,
                value
        );
    }

    public SystemState<T> cancelConfirmation() {
        return new SystemState<T>(
                id,
                initiallyBlocked,
                true,
                false,
                false,
                visibility,
                value
        );
    }

    public boolean isNeedConfirmation() {
        return needConfirmation;
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
        return "SystemState{" +
                "id='" + id + '\'' +
                ", initiallyBlocked=" + initiallyBlocked +
                ", blocked=" + blocked +
                ", selected=" + selected +
                ", visibility=" + visibility +
                ", value=" + value +
                ", needConfirmation=" + needConfirmation +
                '}';
    }
}
