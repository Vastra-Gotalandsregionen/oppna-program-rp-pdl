package se.vgregion.domain.pdl;

import java.io.Serializable;

public class CareProviderUnit implements Serializable {
    private static final long serialVersionUID = -8952966624539627461L;

    public final String careProviderHsaId;
    public final String careProviderDisplayName;
    public final String careUnitHsaId;
    public final String careUnitDisplayName;

    public CareProviderUnit(String careProviderHsaId, String careProviderDisplayName, String careUnitHsaId, String careUnitDisplayName) {
        this.careProviderHsaId = careProviderHsaId;
        this.careProviderDisplayName = careProviderDisplayName;
        this.careUnitHsaId = careUnitHsaId;
        this.careUnitDisplayName = careUnitDisplayName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CareProviderUnit)) return false;

        CareProviderUnit that = (CareProviderUnit) o;

        if (!careProviderDisplayName.equals(that.careProviderDisplayName)) return false;
        if (!careProviderHsaId.equals(that.careProviderHsaId)) return false;
        if (!careUnitDisplayName.equals(that.careUnitDisplayName)) return false;
        if (!careUnitHsaId.equals(that.careUnitHsaId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = careProviderHsaId.hashCode();
        result = 31 * result + careProviderDisplayName.hashCode();
        result = 31 * result + careUnitHsaId.hashCode();
        result = 31 * result + careUnitDisplayName.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "CareProviderUnit{" +
                "careProviderHsaId='" + careProviderHsaId + '\'' +
                ", careProviderDisplayName='" + careProviderDisplayName + '\'' +
                ", careUnitHsaId='" + careUnitHsaId + '\'' +
                ", careUnitDisplayName='" + careUnitDisplayName + '\'' +
                '}';
    }
}
