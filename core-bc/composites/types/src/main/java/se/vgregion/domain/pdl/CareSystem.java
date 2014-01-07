package se.vgregion.domain.pdl;

import java.io.Serializable;

public class CareSystem implements Serializable {
    private static final long serialVersionUID = -8149026774965825742L;

    public final CareSystemSource source;
    public final String careProviderHsaId;
    public final String careProviderDisplayName;
    public final String careUnitHsaId;
    public final String careUnitDisplayName;
    public final String displayId = java.util.UUID.randomUUID().toString(); // Used to keep track of Care System between requests.

    public CareSystem(
            CareSystemSource source,
            String careProviderHsaId,
            String careProviderDisplayName,
            String careUnitHsaId,
            String careUnitDisplayName
    ) {
        this.source = source;
        this.careProviderHsaId = careProviderHsaId;
        this.careProviderDisplayName = careProviderDisplayName;
        this.careUnitHsaId = careUnitHsaId;

        this.careUnitDisplayName = careUnitDisplayName;
    }

    public String getCareUnitHsaId() {
        return careUnitHsaId;
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

    public CareSystemSource getSource() {
        return source;
    }

    @Override
    public String toString() {
        return "CareSystem{" +
                "source=" + source +
                ", careProviderHsaId='" + careProviderHsaId + '\'' +
                ", careProviderDisplayName='" + careProviderDisplayName + '\'' +
                ", careUnitHsaId='" + careUnitHsaId + '\'' +
                ", careUnitDisplayName='" + careUnitDisplayName + '\'' +
                ", displayId='" + displayId + '\'' +
                '}';
    }

}
