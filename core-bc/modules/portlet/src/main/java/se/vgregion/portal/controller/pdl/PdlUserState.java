package se.vgregion.portal.controller.pdl;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import se.vgregion.domain.pdl.CaregiverSystemDescription;
import se.vgregion.domain.pdl.PatientWithEngagements;
import se.vgregion.domain.pdl.PdlContext;
import se.vgregion.domain.pdl.PdlReport;

import java.util.List;

/**
 * Putting things into session scope is arguably an evil thing to do...
 * This is the only mutable data structure.
 */
@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class PdlUserState {
    private PdlReport report;
    private PatientWithEngagements pwe;
    private PdlContext ctx;
    private List<CaregiverSystemDescription> caregiverSystems;

    public PdlReport getReport() {
        return report;
    }

    public PatientWithEngagements getPwe() {
        return pwe;
    }

    public PdlContext getCtx() {
        return ctx;
    }

    public void setReport(PdlReport report) {
        this.report = report;
    }

    public void setPwe(PatientWithEngagements pwe) {
        this.pwe = pwe;
    }

    public void setCtx(PdlContext ctx) {
        this.ctx = ctx;
    }

    public void setCaregiverSystems(List<CaregiverSystemDescription> caregiverSystems) {
        this.caregiverSystems = caregiverSystems;
    }

    public List<CaregiverSystemDescription> getCaregiverSystems() {
        return caregiverSystems;
    }

    @Override
    public String toString() {
        return "PdlUserState{" +
                "report=" + report +
                ", pwe=" + pwe +
                ", ctx=" + ctx +
                ", caregiverSystems=" + caregiverSystems +
                '}';
    }

}
