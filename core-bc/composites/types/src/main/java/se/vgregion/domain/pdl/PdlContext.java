package se.vgregion.domain.pdl;

import se.vgregion.domain.pdl.logging.LogThisField;
import se.vgregion.domain.assignment.Assignment;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

public class PdlContext implements Serializable {
    private static final long serialVersionUID = -2298228544035658452L;
    public final String employeeHsaId;
    public final String employeeDisplayName;
    public final Assignment currentAssignment;
    public final TreeMap<String, Assignment> assignments;

    public PdlContext(
        String employeeDisplayName,
        String employeeHsaId,
        TreeMap<String, Assignment> assignments
    ) {
        this.employeeHsaId = employeeHsaId;
        this.employeeDisplayName = employeeDisplayName;
        this.assignments = assignments;
        if(assignments.size() > 0) {
            this.currentAssignment = assignments.firstEntry().getValue();
        } else {
            this.currentAssignment = null; // This class should be wrapped in WithOutcome.
        }
    }

    private PdlContext(
        String employeeDisplayName,
        String employeeHsaId,
        Assignment currentAssignment,
        TreeMap<String, Assignment> assignments
    ) {
        this.employeeHsaId = employeeHsaId;
        this.employeeDisplayName = employeeDisplayName;
        this.assignments = assignments;
        this.currentAssignment = currentAssignment;
    }

    public String getEmployeeHsaId() {
        return employeeHsaId;
    }

    public Assignment getCurrentAssignment() {
        return currentAssignment;
    }

    public Map<String, Assignment> getAssignments() {
        return assignments;
    }

    public String getEmployeeDisplayName() {
        return employeeDisplayName;
    }

    public PdlContext changeAssignment(String newCurrentAssignment) {
        if(assignments.containsKey(newCurrentAssignment)) {
        return new PdlContext(
                employeeDisplayName,
                employeeHsaId,
                assignments.get(newCurrentAssignment),
                assignments
            );
        } else {
            throw new IllegalArgumentException("The assignment associated with "+newCurrentAssignment+" does not exist in the list of available assignments.");
        }
    }

    @Override
    public String toString() {
        return "PdlContext{" +
                "employeeHsaId='" + employeeHsaId + '\'' +
                ", employeeDisplayName='" + employeeDisplayName + '\'' +
                ", currentAssignment='" + currentAssignment + '\'' +
                ", assignments=" + assignments +
                '}';
    }

}
