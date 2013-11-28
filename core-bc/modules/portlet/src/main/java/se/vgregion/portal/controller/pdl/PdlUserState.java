package se.vgregion.portal.controller.pdl;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import se.vgregion.domain.pdl.CareSystemsReport;
import se.vgregion.domain.pdl.Patient;
import se.vgregion.domain.pdl.PdlContext;
import se.vgregion.domain.pdl.PdlReport;
import se.vgregion.domain.pdl.decorators.WithAccess;

import java.io.Serializable;

/**
 * Putting things into session scope is arguably an evil thing to do...
 * This is the only mutable data structure.
 */
@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class PdlUserState implements Serializable {

    private static final long serialVersionUID = 3716591084727230106L;

    private PdlReport pdlReport;
    private CareSystemsReport csReport;
    private Patient patient;
    private WithAccess<PdlContext> ctx;
    private boolean showOtherCareUnits = false;
    private boolean showOtherCareProviders = false;

    public PdlReport getPdlReport() {
        return pdlReport;
    }

    public void reset() {
        showOtherCareUnits = false;
        showOtherCareProviders = false;
        pdlReport = null;
        csReport = null;
    }

    public Patient getPatient() {
        return patient;
    }

    public WithAccess<PdlContext> getCtx() {
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

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public void setCtx(WithAccess<PdlContext> ctx) {
        this.ctx = ctx;
    }

    public boolean isShowOtherCareUnits() {
        return showOtherCareUnits;
    }

    public void setShowOtherCareUnits(boolean showOtherCareUnits) {
        this.showOtherCareUnits = showOtherCareUnits;
    }

    public boolean isShowOtherCareProviders() {
        return showOtherCareProviders;
    }

    public void setShowOtherCareProviders(boolean showOtherCareProviders) {
        this.showOtherCareProviders = showOtherCareProviders;
    }

    @Override
    public String toString() {
        return "PdlUserState{" +
                "pdlReport=" + pdlReport +
                ", csReport=" + csReport +
                ", patient=" + patient +
                ", ctx=" + ctx +
                ", showOtherCareUnits=" + showOtherCareUnits +
                ", showOtherCareProviders=" + showOtherCareProviders +
                '}';
    }

}
