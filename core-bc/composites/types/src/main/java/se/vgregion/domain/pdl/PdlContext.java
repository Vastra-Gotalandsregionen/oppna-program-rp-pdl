package se.vgregion.domain.pdl;

public class PdlContext {
    public final String careProviderHsaId;
    public final String careUnitHsaId;
    public final String employeeHsaId;
    public final String assignment;

    public PdlContext(String careProviderHsaId, String careUnitHsaId, String employeeHsaId, String assignment) {
        this.careProviderHsaId = careProviderHsaId;
        this.careUnitHsaId = careUnitHsaId;
        this.employeeHsaId = employeeHsaId;
        this.assignment = assignment;
    }

    public String getCareProviderHsaId() {
        return careProviderHsaId;
    }

    public String getCareUnitHsaId() {
        return careUnitHsaId;
    }

    public String getEmployeeHsaId() {
        return employeeHsaId;
    }

    public String getAssignment() {
        return assignment;
    }

    @Override
    public String toString() {
        return "PdlContext{" +
                "careProviderHsaId='" + careProviderHsaId + '\'' +
                ", careUnitHsaId='" + careUnitHsaId + '\'' +
                ", employeeHsaId='" + employeeHsaId + '\'' +
                ", assignment='" + assignment + '\'' +
                '}';
    }
}
