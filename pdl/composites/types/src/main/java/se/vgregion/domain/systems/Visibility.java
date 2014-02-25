package se.vgregion.domain.systems;

public enum Visibility {
    NOT_VISIBLE,          // Not visible at all
    SAME_CARE_UNIT,       // Visible for Assignments VoB and SJF
    OTHER_CARE_UNIT,     // Visible for Assignments VoB
    OTHER_CARE_PROVIDER // Visible for Assignment SJF
}
