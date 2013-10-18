package se.vgregion.domain.pdl;

public class CareSystem {
    public final String displayName;
    public final Engagement.InformationType informationType;
    public final String careProviderHsaId;
    public final String careUnitHsaId;

    public String getCareUnitHsaId() {
        return careUnitHsaId;
    }

    public CareSystem(String displayName, Engagement.InformationType informationType, String careProviderHsaId, String careUnitHsaId) {
        this.displayName = displayName;
        this.informationType = informationType;
        this.careProviderHsaId = careProviderHsaId;
        this.careUnitHsaId = careUnitHsaId;

    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCareProviderHsaId() {
        return careProviderHsaId;
    }

    public Engagement.InformationType getInformationType() {
        return informationType;
    }

    @Override
    public String toString() {
        return "CareSystem{" +
                "displayName='" + displayName + '\'' +
                ", informationType=" + informationType +
                ", careProviderHsaId='" + careProviderHsaId + '\'' +
                ", careUnitHsaId='" + careUnitHsaId + '\'' +
                '}';
    }
}
