package se.vgregion.domain.pdl;

public enum CareSystemSource {
    BFR("Bild- och funktionsregistret"),
    RRE("Nya Remissregistret"),
    OTH("Annat sjukvårdsystem");

    public final String displayName;

    CareSystemSource(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return "CareSystemSource{" +
                "displayName='" + displayName + '\'' +
                '}';
    }
}
