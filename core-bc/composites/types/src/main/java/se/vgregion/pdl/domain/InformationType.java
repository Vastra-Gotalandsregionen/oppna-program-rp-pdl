package se.vgregion.pdl.domain;

public enum InformationType {
    LAK("Läkemedel - Ordination/förskrivning"), UPP("Uppmärksamhetsinformation"),
    OTHR("Annan information");

    private final String desc;

    InformationType( String desc ) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName().toLowerCase();
    }
}
