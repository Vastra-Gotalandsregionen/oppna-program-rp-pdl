package se.vgregion.domain.pdl.logging;

import javax.persistence.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: portaldev
 * Date: 2013-11-21
 * Time: 19:58
 * To change this template use File | Settings | File Templates.
 */
//@Entity
//@Table(name = "pdl_log_old")
public class LogOld {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Date date;

    private User user;

    private System system;

    private Patient patient;

    private CareUnit careUnit;

    private CareProvider careProvider;

    public LogOld() {
        super();
        setDate(new Date());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public System getSystem() {
        return system;
    }

    public void setSystem(System system) {
        this.system = system;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public CareUnit getCareUnit() {
        return careUnit;
    }

    public void setCareUnit(CareUnit careUnit) {
        this.careUnit = careUnit;
    }

    public CareProvider getCareProvider() {
        return careProvider;
    }

    public void setCareProvider(CareProvider careProvider) {
        this.careProvider = careProvider;
    }
}
