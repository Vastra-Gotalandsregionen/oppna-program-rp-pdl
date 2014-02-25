package se.vgregion.domain.decorators;

import se.vgregion.domain.systems.Visibility;

import java.io.Serializable;
import java.util.Map;

public class InfoTypeState<T extends Serializable> implements Serializable, Comparable<InfoTypeState> {

    private static final long serialVersionUID = -5813956332644812360L;

    public final Visibility lowestVisibility;
    public final boolean showSameCareUnit;
    public final boolean selected;  // Shows that the user have actively chosen to view the information associated with this information decorator.
    public final Map<Visibility, Boolean> containsBlocked;
    public final boolean containsOtherUnits;
    public final boolean containsOtherProviders;
    public final boolean viewBlocked;
    public final String id;
    public final T value;

    private InfoTypeState(
            Visibility lowestVisibility,
            boolean selected,
            Map<Visibility, Boolean> containsBlocked,
            boolean viewBlocked,
            T value
    ) {
        this(
            lowestVisibility,
            false, // Defaults to false
            selected,
            containsBlocked,
            viewBlocked,
            java.util.UUID.randomUUID().toString(),
            false,
            false,
            value
        );
    }

    private InfoTypeState(
            Visibility lowestVisibility,
            boolean selected,
            Map<Visibility, Boolean> containsBlocked,
            boolean viewBlocked,
            String id,
            boolean containsOtherUnits,
            boolean containsOtherProviders,
            T value
    ) {
        this.lowestVisibility = lowestVisibility;
        this.showSameCareUnit = false;
        this.containsOtherUnits = containsOtherUnits;
        this.containsOtherProviders = containsOtherProviders;
        this.selected = selected;
        this.containsBlocked = containsBlocked;
        this.viewBlocked = viewBlocked;
        this.id = id;
        this.value = value;
    }

    private InfoTypeState(
            Visibility lowestVisibility,
            boolean showSameCareUnit,
            boolean selected,
            Map<Visibility, Boolean> containsBlocked,
            boolean viewBlocked,
            String id,
            boolean containsOtherUnits,
            boolean containsOtherProviders,
            T value
    ) {
        this.lowestVisibility = lowestVisibility;
        this.showSameCareUnit = showSameCareUnit;
        this.selected = selected;
        this.containsBlocked = containsBlocked;
        this.containsOtherUnits = containsOtherUnits;
        this.containsOtherProviders = containsOtherProviders;
        this.viewBlocked = viewBlocked;
        this.id = id;
        this.value = value;
    }

    public InfoTypeState<T> select() {
       return new InfoTypeState<T>(
           lowestVisibility,
           showSameCareUnit,
           true,
           containsBlocked,
           viewBlocked,
           id,
           containsOtherUnits,
           containsOtherProviders,
           value
       );
    }

    public InfoTypeState<T> showSameCareUnit() {
        return new InfoTypeState<T>(
            lowestVisibility,
            true,
            selected,
            containsBlocked,
            viewBlocked,
            id,
            containsOtherUnits,
            containsOtherProviders,
            value
        );
    }


    public InfoTypeState<T> viewBlocked() {
        return new InfoTypeState<T>(
            lowestVisibility,
            showSameCareUnit,
            selected,
            containsBlocked,
            true,
            id,
            containsOtherUnits,
            containsOtherProviders,
            value
        );
    }

    public static <N extends Serializable, N1 extends N> InfoTypeState<N> deselected(
            Visibility visibility,
            Map<Visibility, Boolean> containsBlocked,
            N1 value
    ) {
        return new InfoTypeState<N>(
                visibility,
                false,
                containsBlocked,
                false,
                value
        );
    }

    public static <N extends Serializable, N1 extends N> InfoTypeState<N> selected(
            Visibility visibility,
            Map<Visibility, Boolean>  containsBlocked,
            N1 value
    ) {
        return new InfoTypeState<N>(
                visibility,
                true,
                containsBlocked,
                false,
                value
        );
    }

    public InfoTypeState<T> mapContains(
            boolean newContainsOtherUnits,
            boolean newContainsOtherProviders
    ) {
        return new InfoTypeState<T>(
                lowestVisibility,
                showSameCareUnit,
                selected,
                containsBlocked,
                viewBlocked,
                id,
                newContainsOtherUnits,
                newContainsOtherProviders,
                value
        );
    }

    public Visibility getLowestVisibility() {
        return lowestVisibility;
    }

    public boolean isSelected() {
        return selected;
    }

    public Map<Visibility, Boolean>  getContainsBlocked() {
        return containsBlocked;
    }

    public String getId() {
        return id;
    }

    public boolean isViewBlocked() {
        return viewBlocked;
    }

    public T getValue() {
        return value;
    }

    public boolean isShowSameCareUnit() {
        return showSameCareUnit;
    }

    @Override
    public int compareTo(InfoTypeState o) {
        return id.compareTo(o.id);
    }

    @Override
    public String toString() {
        return "InfoTypeState{" +
                "lowestVisibility=" + lowestVisibility +
                ", showSameCareUnit=" + showSameCareUnit +
                ", selected=" + selected +
                ", containsBlocked=" + containsBlocked +
                ", viewBlocked=" + viewBlocked +
                ", id='" + id + '\'' +
                ", value=" + value +
                '}';
    }

}
