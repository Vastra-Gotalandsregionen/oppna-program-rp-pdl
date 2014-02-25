package se.vgregion.domain.logging;

import org.apache.commons.beanutils.BeanMap;

import javax.persistence.*;
import java.util.TreeMap;

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

    @Column(name = "search_session")
    private String searchSession;

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

    @Override
    public String toString() {
        try {
            return new TreeMap(new BeanMap()).toString();
        } catch (Exception e) {
            System.out.println("Fel på to string i pdl-basklass!");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
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

    public String getSearchSession() {
        return searchSession;
    }

    public void setSearchSession(String searchSession) {
        this.searchSession = searchSession;
    }
}
