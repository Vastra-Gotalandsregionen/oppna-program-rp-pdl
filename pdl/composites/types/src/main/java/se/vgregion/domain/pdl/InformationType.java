package se.vgregion.domain.pdl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public enum InformationType {
    //ALT("Allt"),
    DIA("Diagnos"),
    FUN("Funktionsnedsättning"),
    LAK("Läkemedel Ordination/förskrivning"),
    LKM("Läkemedel Utlämning"),
    PAD("PADL"),
    PAT("Vård- och omsorgstagare"),
    UND("Undersökningsresultat"),
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

    private static final Set<String> mapToLak =
            Collections.unmodifiableSet(new TreeSet<String>(Arrays.asList("LKO", "LKF")));

    public static InformationType valueOfWithFallback(String value) {
        if(mapToLak.contains(value)) {
            return LAK;
        }
        return InformationType.valueOf(value);
    }

    @Override
    public String toString() {
        return name() + "("+desc+")";
    }
}
