package se.vgregion.domain.pdl;

import java.io.Serializable;
import java.util.List;

public class CareSystem implements Serializable {
    private static final long serialVersionUID = -8149026774965825742L;

    public final String displayName;
    public final List<Engagement.InformationType> informationTypes;
    public final String careProviderHsaId;
    public final String careUnitHsaId;

    public CareSystem(
            String displayName,
            List<Engagement.InformationType> informationTypes,
            String careProviderHsaId,
            String careUnitHsaId) {
        this.displayName = displayName;
        this.informationTypes = informationTypes;
        this.careProviderHsaId = careProviderHsaId;
        this.careUnitHsaId = careUnitHsaId;

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

    public List<Engagement.InformationType> getInformationTypes() {
        return informationTypes;
    }

    @Override
    public String toString() {
        return "CareSystem{" +
                "displayName='" + displayName + '\'' +
                ", informationTypes=" + informationTypes +
                ", careProviderHsaId='" + careProviderHsaId + '\'' +
                ", careUnitHsaId='" + careUnitHsaId + '\'' +
                '}';
    }
}
