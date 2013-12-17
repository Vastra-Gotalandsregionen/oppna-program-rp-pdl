package se.vgregion.domain.pdl;

public enum InformationType {
    //ALT("Allt"),
    DIA("Diagnos"),
    FUN("Funktionsnedsättning"),
    LAK("Läkemedel Ordination/förskrivning"),
    LKM("Läkemedel Utlämning"),
    PAD("PADL"),
    PAT("Vård- och omsorgstagare"),
    UNR("Undersökningsresultat"),
    UPP("Uppmärksamhetsinformation"),
    VBE("Vårdbegäran"),
    VOT("Vård- och omsorgstjänst"),
    VKO("Vård- och omsorgskontakt"),
    VOO("Vård- och omsorgsdokument (ostrukturerad)"),
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
