package se.vgregion.domain.decorators;

import se.vgregion.domain.pdl.Visibility;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

public class InfoTypeState<T extends Serializable> implements Serializable, Comparable<InfoTypeState> {

    private static final long serialVersionUID = -5813956332644812360L;

    public final Visibility lowestVisibility;
    public final boolean selected;  // Shows that the user have actively chosen to view the information associated with this information type.
    public final Map<Visibility, Boolean> containsBlocked;
    public final Map<Visibility, Boolean> containsOnlyBlocked;
    public final boolean viewBlocked;
    public final String id;
    public final T value;

    private InfoTypeState(
            Visibility lowestVisibility,
            boolean selected,
            Map<Visibility, Boolean> containsBlocked,
            Map<Visibility, Boolean> containsOnlyBlocked,
            boolean viewBlocked,
            T value
    ) {
        this(
            lowestVisibility,
            selected,
            containsBlocked,
            containsOnlyBlocked,
            viewBlocked,
            java.util.UUID.randomUUID().toString(),
            value
        );
    }


    private InfoTypeState(
            Visibility lowestVisibility,
            boolean selected,
            Map<Visibility, Boolean> containsBlocked,
            Map<Visibility, Boolean> containsOnlyBlocked,
            boolean viewBlocked,
            String id,
            T value
    ) {
        this.lowestVisibility = lowestVisibility;
        this.selected = selected;
        this.containsBlocked = containsBlocked;
        this.containsOnlyBlocked = containsOnlyBlocked;
        this.viewBlocked = viewBlocked;
        this.id = id;
        this.value = value;
    }

    public InfoTypeState<T> select() {
       return new InfoTypeState<T>(
               lowestVisibility,
               true,
               containsBlocked,
               containsOnlyBlocked,
               viewBlocked,
               id,
               value
       );
    }

    public InfoTypeState<T> viewBlocked() {
        return new InfoTypeState<T>(
                lowestVisibility,
                selected,
                containsBlocked,
                containsOnlyBlocked,
                true,
                id,
                value
        );
    }

    public InfoTypeState<T> showBlockedInfoType(
            Visibility visibility
    ) {

        Map<Visibility, Boolean> newContainsOnlyBlocked = new TreeMap<Visibility, Boolean>();
        for(Visibility v : containsOnlyBlocked.keySet()) {
            if(v == visibility) {
                newContainsOnlyBlocked.put(v, false);
            } else {
                newContainsOnlyBlocked.put(v, containsOnlyBlocked.get(v));
            }
        }

        return new InfoTypeState<T>(
                lowestVisibility,
                selected,
                containsBlocked,
                newContainsOnlyBlocked,
                true,
                id,
                value
        );
    }

    public static <N extends Serializable, N1 extends N> InfoTypeState<N> deselected(
            Visibility visibility,
            Map<Visibility, Boolean> containsBlocked,
            Map<Visibility, Boolean> containsOnlyBlocked,
            N1 value
    ) {
        return new InfoTypeState<N>(
                visibility,
                false,
                containsBlocked,
                containsOnlyBlocked,
                false,
                value
        );
    }

    public static <N extends Serializable, N1 extends N> InfoTypeState<N> selected(
            Visibility visibility,
            Map<Visibility, Boolean>  containsBlocked,
            Map<Visibility, Boolean> containsOnlyBlocked,
            N1 value
    ) {
        return new InfoTypeState<N>(
                visibility,
                true,
                containsBlocked,
                containsOnlyBlocked,
                false,
                value
        );
    }

    public Visibility getLowestVisibility() {
        return lowestVisibility;
    }

    public Map<Visibility, Boolean> getContainsOnlyBlocked() {
        return containsOnlyBlocked;
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

    @Override
    public int compareTo(InfoTypeState o) {
        return id.compareTo(o.id);
    }

    @Override
    public String toString() {
        return "InfoTypeState{" +
                "lowestVisibility=" + lowestVisibility +
                ", selected=" + selected +
                ", containsBlocked=" + containsBlocked +
                ", containsOnlyBlocked=" + containsOnlyBlocked +
                ", viewBlocked=" + viewBlocked +
                ", id='" + id + '\'' +
                ", value=" + value +
                '}';
    }
}
