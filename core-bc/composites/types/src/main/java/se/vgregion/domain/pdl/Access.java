package se.vgregion.domain.pdl;

import java.io.Serializable;

public class Access implements Serializable {
    private static final long serialVersionUID = -5338072339084818427L;

    public final String hsaId;
    public final boolean sameProvider;

    private Access(String hsaId, boolean sameProvider) {
        this.hsaId = hsaId;
        this.sameProvider = sameProvider;
    }

    public static Access otherProvider(String careProviderHsaId) {
        return new Access(careProviderHsaId, false);
    }

    public static Access sameProvider(String careUnitHsaId) {
        return new Access(careUnitHsaId, true);
    }

    @Override
    public String toString() {
        return "Access{" +
                "hsaId='" + hsaId + '\'' +
                ", sameProvider=" + sameProvider +
                '}';
    }
}
