package se.vgregion.domain.pdl;

public class PdlContext {
    public final String careProviderHsaId;
    public final String careUnitHsaId;
    public final String employeeHsaId;

    public PdlContext(String careProviderHsaId, String careUnitHsaId, String employeeHsaId) {
        this.careProviderHsaId = careProviderHsaId;
        this.careUnitHsaId = careUnitHsaId;
        this.employeeHsaId = employeeHsaId;
    }

    @Override
    public String toString() {
        return "PdlContext{" +
                "careProviderHsaId='" + careProviderHsaId + '\'' +
                ", careUnitHsaId='" + careUnitHsaId + '\'' +
                ", employeeHsaId='" + employeeHsaId + '\'' +
                '}';
    }
}
