package se.vgregion.pdl.domain;

public class PatientEngagement {
    public final String careProviderHsaId;
    public final String careUnitHsaId;
    public final String employeeHsaId;
    public final InformationType informationType;

    public PatientEngagement(
            String careProviderHsaId,
            String careUnitHsaId,
            String employeeHsaId,
            InformationType informationType) {
        this.careProviderHsaId = careProviderHsaId;
        this.careUnitHsaId = careUnitHsaId;
        this.employeeHsaId = employeeHsaId;
        this.informationType = informationType;
    }
}
