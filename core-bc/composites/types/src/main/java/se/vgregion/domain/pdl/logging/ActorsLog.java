package se.vgregion.domain.pdl.logging;

import org.apache.commons.collections.BeanMap;

import javax.persistence.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Claes Lundahl
 * Date: 2013-11-21
 * Time: 19:58
 * To change this template use File | Settings | File Templates.
 */

@MappedSuperclass
public class ActorsLog {

    @Column(name = "employee_id")
    private String employeeId;

    @Column(name = "employee_display_name")
    private String employeeDisplayName;

    @Column(name = "patient_id")
    private String patientId; // Personnummer

    @Column(name = "patient_display_name")
    private String patientDisplayName;

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ActorsLog)) {
            return false;
        }
        BeanMap self = new BeanMap(this);
        BeanMap other = new BeanMap(obj);
        return self.equals(other);
    }

    @Override
    public int hashCode() {
        return new BeanMap(this).hashCode();
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeDisplayName() {
        return employeeDisplayName;
    }

    public void setEmployeeDisplayName(String employeeDisplayName) {
        this.employeeDisplayName = employeeDisplayName;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientDisplayName() {
        return patientDisplayName;
    }

    public void setPatientDisplayName(String patientDisplayName) {
        this.patientDisplayName = patientDisplayName;
    }

}
