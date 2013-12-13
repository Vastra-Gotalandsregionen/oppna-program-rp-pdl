package se.vgregion.domain.pdl;

public enum Visibility {
    SAME_CARE_UNIT,     // Visible for Assignments VoB and SFJ
    OTHER_CARE_UNIT,    // Visible for Assignments VoB
    OTHER_CARE_PROVIDER // Visible for Assignment SFJ
}
