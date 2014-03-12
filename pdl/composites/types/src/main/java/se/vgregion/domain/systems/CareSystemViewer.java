package se.vgregion.domain.systems;

public enum CareSystemViewer {
    BFR("Bild- och funktionsregistret", "bfr-pdl"),
    RRE("Nya Remissregistret", "ny-remiss"),
    OTH("Annat sjukvårdsystem", "an-sjuk");

    public final String displayName;
    public final String childPage;

    CareSystemViewer(String displayName, String childPage) {
        this.displayName = displayName;
        this.childPage = childPage;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getChildPage() {
        return childPage;
    }

    @Override
    public String toString() {
        return "CareSystemViewer{" +
                "displayName='" + displayName + '\'' +
                ", childPage='" + childPage + '\'' +
                '}';
    }
}
