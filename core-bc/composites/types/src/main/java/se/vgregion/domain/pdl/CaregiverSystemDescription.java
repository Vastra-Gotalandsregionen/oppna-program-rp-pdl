package se.vgregion.domain.pdl;

public class CaregiverSystemDescription {
    public final String displayName;

    public CaregiverSystemDescription(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return "CaregiverSystemDescription{" +
                "displayName='" + displayName + '\'' +
                '}';
    }
}
