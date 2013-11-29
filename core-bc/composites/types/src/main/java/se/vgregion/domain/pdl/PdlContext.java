package se.vgregion.domain.pdl;

import java.io.Serializable;

public class PdlContext implements Serializable {
    private static final long serialVersionUID = -2298228544035658452L;

    public final String careProviderHsaId;
    public final String careUnitHsaId;
    public final String employeeHsaId;
    public final String assignmentDisplayName;
    public final String assignmentHsaId;
    public final String careProviderDisplayName;
    public final String careUnitDisplayName;
    public final String employeeDisplayName;

    public PdlContext(
            String careProviderDisplayName,
            String careProviderHsaId,
            String careUnitDisplayName,
            String careUnitHsaId,
            String employeeDisplayName,
            String employeeHsaId,
            String assignmentDisplayName,
            String assignmentHsaId
    ) {
        this.careProviderHsaId = careProviderHsaId;
        this.careUnitHsaId = careUnitHsaId;
        this.employeeHsaId = employeeHsaId;
        this.assignmentDisplayName = assignmentDisplayName;
        this.assignmentHsaId = assignmentHsaId;

        this.careProviderDisplayName = careProviderDisplayName;
        this.careUnitDisplayName = careUnitDisplayName;
        this.employeeDisplayName = employeeDisplayName;
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

    public String getAssignmentHsaId() {
        return assignmentHsaId;
    }

    public String getAssignmentDisplayName() {
        return assignmentDisplayName;
    }

    public String getCareProviderDisplayName() {
        return careProviderDisplayName;
    }

    public String getCareUnitDisplayName() {
        return careUnitDisplayName;
    }

    public String getEmployeeDisplayName() {
        return employeeDisplayName;
    }

    @Override
    public String toString() {
        return "PdlContext{" +
                "careProviderHsaId='" + careProviderHsaId + '\'' +
                ", careUnitHsaId='" + careUnitHsaId + '\'' +
                ", employeeHsaId='" + employeeHsaId + '\'' +
                ", assignmentDisplayName='" + assignmentDisplayName + '\'' +
                ", assignmentHsaId='" + assignmentHsaId + '\'' +
                ", careProviderDisplayName='" + careProviderDisplayName + '\'' +
                ", careUnitDisplayName='" + careUnitDisplayName + '\'' +
                ", employeeDisplayName='" + employeeDisplayName + '\'' +
                '}';
    }
}
