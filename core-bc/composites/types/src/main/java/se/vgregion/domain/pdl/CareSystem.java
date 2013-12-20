package se.vgregion.domain.pdl;

import se.vgregion.domain.assignment.Assignment;

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

    public Visibility getVisibilityFor(
            String careProviderHsaId,
            String careUnitHsaId
    ) {
        boolean sameCareProvider = this.careProviderHsaId.equalsIgnoreCase(careProviderHsaId);
        boolean sameCareUnit = this.careUnitHsaId.equalsIgnoreCase(careUnitHsaId);
        if(!sameCareProvider) {
            return Visibility.OTHER_CARE_PROVIDER;
        } else if(sameCareUnit) {
            return Visibility.SAME_CARE_UNIT;
        } else {
            return Visibility.OTHER_CARE_UNIT;
        }
    }

    public Visibility getVisibilityFor(Assignment assignment) {
        return getVisibilityFor(assignment.careProviderHsaId, assignment.getCareUnitHsaId());
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
