package se.vgregion.domain.pdl;

public enum InformationType {
    //ALT("Allt"),
    DIG("Diagnos"),
    FUN("Funktionsnedsättning"),
    LAK("Läkemedel Förskrivning"),
    LUT("Läkemedel Utlämning"),
    LAO("Läkemedel Ordination"),
    PAD("PADL"),
    VOT("Vård- och omsorgstagare"),
    UNR("Undersökningsresultat"),
    UPP("Uppmärksamhetssignal"),
    VBR("Vårdbegäran"),
    VOK("Vård- och omsorgskontakt"),
    VOD("Vård- och omsorgsdokument (ostrukturerad)"),
    VOP("Vård- och omsorgsplan (ostrukturerad)");

    private final String desc;

    InformationType( String desc ) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return name() + "("+desc+")";
    }
}
