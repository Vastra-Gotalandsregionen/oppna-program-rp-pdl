package se.vgregion.domain.systems;

public enum CareSystemViewer {
    BFR("Bild- och funktionsregistret"),
    RRE("Nya Remissregistret"),
    OTH("Annat sjukvårdsystem");

    public final String displayName;

    CareSystemViewer(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return "CareSystemViewer{" +
                "displayName='" + displayName + '\'' +
                '}';
    }
}
