package se.vgregion.domain.pdl.logging;

import org.apache.commons.collections.BeanMap;

import java.util.Date;
import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: Claes Lundahl
 * Date: 2013-11-21
 * Time: 19:58
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "pdl_log")
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Date date;

    @Column(name = "user_hsa_id")
    private String userHsaId;

    @Column(name = "system_hsa_id")
    private String systemHsaId;

    // Personnummer?
    @Column(name = "patient_id")
    private String patientId;


    @Column(name = "care_unit_hsa_id")
    private String careUnitHsaId;

    @Column(name = "care_provider_hsa_id")
    private String careProviderHsaId;

    @Column(name = "user_action")
    private String userAction;

    public Log() {
        super();
        setDate(new Date());
    }


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserHsaId() {
        return userHsaId;
    }

    public void setUserHsaId(String userHsaId) {
        this.userHsaId = userHsaId;
    }

    public String getSystemHsaId() {
        return systemHsaId;
    }

    public void setSystemHsaId(String systemHsaId) {
        this.systemHsaId = systemHsaId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getCareUnitHsaId() {
        return careUnitHsaId;
    }

    public void setCareUnitHsaId(String careUnitHsaId) {
        this.careUnitHsaId = careUnitHsaId;
    }

    public String getCareProviderHsaId() {
        return careProviderHsaId;
    }

    public void setCareProviderHsaId(String careProviderHsaId) {
        this.careProviderHsaId = careProviderHsaId;
    }

    public String getUserAction() {
        return userAction;
    }

    public void setUserAction(String userAction) {
        this.userAction = userAction;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Log)) {
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
}
