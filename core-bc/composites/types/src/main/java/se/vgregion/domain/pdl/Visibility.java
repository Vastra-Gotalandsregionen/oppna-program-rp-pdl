package se.vgregion.domain.pdl;

public enum Visibility {
    SAME_CARE_UNIT,       // Visible for Assignments VoB and SJF
    OTHER_CARE_UNIT,     // Visible for Assignments VoB
    OTHER_CARE_PROVIDER, // Visible for Assignment SJF
    NOT_VISIBLE          // Not visible at all
}
