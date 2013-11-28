package se.vgregion.domain.pdl;

public enum InformationType {
    LAK("Läkemedel - Ordination/förskrivning"),
    UPP("Uppmärksamhetsinformation"),
    //ALT("Allt"),
    DIG("Diagnos"),
    FUN("Funktionsnedsättning"),
    LFÖ("Läkemedel Förskrivning"),
    LUT("Läkemedel Utlämning"),
    LAO("Läkemedel Ordination"),
    PAD("PADL"),
    VOM("Vård- och omsorgstagare"),
    UNS("Undersökningsresultat"),
    UPS("Uppmärksamhetssignal"),
    VRD("Vårdbegäran"),
    VRK("Vård- och omsorgskontakt"),
    VRO("Vård- och omsorgsdokument (ostrukturerad)"),
    VPO("Vård- och omsorgsplan (ostrukturerad)");

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
