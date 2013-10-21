package se.vgregion.portal.controller.pdl;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import se.vgregion.domain.pdl.CareSystemsReport;
import se.vgregion.domain.pdl.PatientWithEngagements;
import se.vgregion.domain.pdl.PdlContext;
import se.vgregion.domain.pdl.PdlReport;

/**
 * Putting things into session scope is arguably an evil thing to do...
 * This is the only mutable data structure.
 */
@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class PdlUserState {
    private PdlReport pdlReport;
    private CareSystemsReport csReport;
    private PatientWithEngagements pwe;
    private PdlContext ctx;
    private boolean showSameCareProvider = false;

    public PdlReport getPdlReport() {
        return pdlReport;
    }

    public void reset() {
        showSameCareProvider = false;
    }

    public PatientWithEngagements getPwe() {
        return pwe;
    }

    public PdlContext getCtx() {
        return ctx;
    }

    public void setPdlReport(PdlReport report) {
        this.pdlReport = report;
    }

    public CareSystemsReport getCsReport() {
        return csReport;
    }

    public void setCsReport(CareSystemsReport csReport) {
        this.csReport = csReport;
    }

    public void setPwe(PatientWithEngagements pwe) {
        this.pwe = pwe;
    }

    public void setCtx(PdlContext ctx) {
        this.ctx = ctx;
    }

    public boolean isShowSameCareProvider() {
        return showSameCareProvider;
    }

    public void setShowSameCareProvider(boolean showSameCareProvider) {
        this.showSameCareProvider = showSameCareProvider;
    }

    @Override
    public String toString() {
        return "PdlUserState{" +
                "pdlReport=" + pdlReport +
                ", csReport=" + csReport +
                ", pwe=" + pwe +
                ", ctx=" + ctx +
                ", showSameCareProvider=" + showSameCareProvider +
                '}';
    }

}
