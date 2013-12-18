package se.vgregion.domain.pdl.logging;

import org.apache.commons.collections.BeanMap;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "vgr_pdl_event_log")
public class PdlEventLog extends ActorsLog {

    public PdlEventLog() {
        super();
    }

    public PdlEventLog(ActorsLog actorsLog) {
        super();
        BeanMap self = new BeanMap(this);
        BeanMap other = new BeanMap(actorsLog);
        self.putAllWriteable(other);
    }

    @Id
    @Column(name = "id", updatable = false)
    private String uuid = java.util.UUID.randomUUID().toString();

    @Column(name = "creation_time", updatable = false, columnDefinition = "timestamp with time zone not null")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationTime;

    @Column(name = "care_unit_id", updatable = false)
    private String careUnitId; // HsaId

    @Column(name = "care_unit_display_name", updatable = false)
    private String careUnitDisplayName;

    @Column(name = "care_provider_id", updatable = false)
    private String careProviderId; // HsaId

    @Column(name = "care_provider_display_name", updatable = false)
    private String careProviderDisplayName;

    @Column(name = "system_id", updatable = false)
    private String systemId; // HsaId

    @Column(name = "assignment_id", updatable = false)
    private String assignmentId; // HsaId

    @Column(name = "user_action", updatable = false)
    @Enumerated(EnumType.STRING)
    private UserAction userAction;

    @Lob
    @Column(name = "log_text", updatable = false)

    private String logText;

    public String getUuid() {
        return uuid;
    }

    private void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCareUnitId() {
        return careUnitId;
    }

    public void setCareUnitId(String careUnitId) {
        this.careUnitId = careUnitId;
    }

    public String getCareUnitDisplayName() {
        return careUnitDisplayName;
    }

    public void setCareUnitDisplayName(String careUnitDisplayName) {
        this.careUnitDisplayName = careUnitDisplayName;
    }

    public String getCareProviderId() {
        return careProviderId;
    }

    public void setCareProviderId(String careProviderId) {
        this.careProviderId = careProviderId;
    }

    public String getCareProviderDisplayName() {
        return careProviderDisplayName;
    }

    public void setCareProviderDisplayName(String careProviderDisplayName) {
        this.careProviderDisplayName = careProviderDisplayName;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(String assignmentId) {
        this.assignmentId = assignmentId;
    }

    public UserAction getUserAction() {
        return userAction;
    }

    public void setUserAction(UserAction userAction) {
        this.userAction = userAction;
    }

    public String getLogText() {
        return logText;
    }

    public void setLogText(String logText) {
        this.logText = logText;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

}
