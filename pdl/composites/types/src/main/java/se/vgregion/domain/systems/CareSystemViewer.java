package se.vgregion.domain.systems;

public enum CareSystemViewer {
    BFR("Bild- och funktionsregistret", "bfr");

    public final String displayName;
    public final String systemKey;

    CareSystemViewer(String displayName, String systemKey) {
        this.displayName = displayName;
        this.systemKey = systemKey;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSystemKey() {
        return systemKey;
    }

    @Override
    public String toString() {
        return "CareSystemViewer{" +
                "displayName='" + displayName + '\'' +
                ", systemKey='" + systemKey + '\'' +
                '}';
    }
}
