package se.vgregion.domain.pdl;

import se.vgregion.domain.pdl.logging.LogThisField;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class PdlContext implements Serializable {
    private static final long serialVersionUID = -2298228544035658452L;

    // So far there are no deeper thought on the LogThisField-marking.
    @LogThisField()
    public final String careProviderHsaId;
    @LogThisField()
    public final String careUnitHsaId;
    @LogThisField()
    public final String employeeHsaId;
    @LogThisField()
    public final HashMap<String, AssignmentAccess> assignments;
    @LogThisField()
    public final String careProviderDisplayName;
    @LogThisField()
    public final String careUnitDisplayName;
    @LogThisField()
    public final String employeeDisplayName;

    public PdlContext(
            String careProviderDisplayName,
            String careProviderHsaId,
            String careUnitDisplayName,
            String careUnitHsaId,
            String employeeDisplayName,
            String employeeHsaId,
            HashMap<String, AssignmentAccess> assignments
    ) {
        this.careProviderHsaId = careProviderHsaId;
        this.careUnitHsaId = careUnitHsaId;
        this.employeeHsaId = employeeHsaId;
        this.assignments = assignments;
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

    public Map<String, AssignmentAccess> getAssignments() {
        return assignments;
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
                ", assignments=" + assignments +
                ", careProviderDisplayName='" + careProviderDisplayName + '\'' +
                ", careUnitDisplayName='" + careUnitDisplayName + '\'' +
                ", employeeDisplayName='" + employeeDisplayName + '\'' +
                '}';
    }
}
