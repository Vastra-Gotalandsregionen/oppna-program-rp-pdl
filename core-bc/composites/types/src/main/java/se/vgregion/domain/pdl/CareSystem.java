package se.vgregion.domain.pdl;

import java.io.Serializable;

public class CareSystem implements Serializable {
    private static final long serialVersionUID = -8149026774965825742L;

    public final String displayName;
    public final String careProviderHsaId;
    public final String careProviderDisplayName;
    public final String careUnitHsaId;
    public final String careUnitDisplayName;
    public final String displayId = java.util.UUID.randomUUID().toString(); // Used to keep track of Care System between requests.

    public CareSystem(
            String displayName,
            String careProviderHsaId,
            String careProviderDisplayName,
            String careUnitHsaId,
            String careUnitDisplayName
    ) {
        this.displayName = displayName;
        this.careProviderHsaId = careProviderHsaId;
        this.careProviderDisplayName = careProviderDisplayName;
        this.careUnitHsaId = careUnitHsaId;

        this.careUnitDisplayName = careUnitDisplayName;
    }

    public String getCareUnitHsaId() {
        return careUnitHsaId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCareProviderHsaId() {
        return careProviderHsaId;
    }

    public String getDisplayId() {
        return displayId;
    }

    public String getCareProviderDisplayName() {
        return careProviderDisplayName;
    }

    public String getCareUnitDisplayName() {
        return careUnitDisplayName;
    }

    @Override
    public String toString() {
        return "CareSystem{" +
                "displayName='" + displayName + '\'' +
                ", careProviderHsaId='" + careProviderHsaId + '\'' +
                ", careProviderDisplayName='" + careProviderDisplayName + '\'' +
                ", careUnitHsaId='" + careUnitHsaId + '\'' +
                ", careUnitDisplayName='" + careUnitDisplayName + '\'' +
                ", displayId='" + displayId + '\'' +
                '}';
    }
}
