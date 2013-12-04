package se.vgregion.portal.controller.pdl;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import se.vgregion.domain.pdl.*;
import se.vgregion.domain.pdl.decorators.WithAccess;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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
    private String searchSession = java.util.UUID.randomUUID().toString();
    private Visibility currentVisibility = Visibility.SAME_CARE_UNIT;
    private final Map<Visibility, Boolean> shouldBeVisible = new HashMap<Visibility, Boolean>();

    private void calcVisibility() {
        shouldBeVisible.clear();
        shouldBeVisible.put(Visibility.SAME_CARE_UNIT, pdlReport.hasRelationship.value);
        shouldBeVisible.put(Visibility.OTHER_CARE_UNIT, showOtherCareUnits && pdlReport.hasRelationship.value);
        shouldBeVisible.put(Visibility.OTHER_CARE_PROVIDER, showOtherCareUnits && showOtherCareProviders && pdlReport.consent.value.hasConsent && pdlReport.hasRelationship.value);

        if(pdlReport.hasRelationship.value) {
            currentVisibility = Visibility.SAME_CARE_UNIT;

            if(showOtherCareUnits) {
                currentVisibility = Visibility.OTHER_CARE_UNIT;

                if(
                    showOtherCareProviders &&
                    pdlReport.consent.value.hasConsent
                ) {
                    currentVisibility = Visibility.OTHER_CARE_PROVIDER;
                }
            }
        }

    }

    public PdlReport getPdlReport() {
        return pdlReport;
    }

    public void reset() {
        showOtherCareUnits = false;
        showOtherCareProviders = false;
        pdlReport = null;
        csReport = null;
        searchSession = java.util.UUID.randomUUID().toString();
        currentVisibility = Visibility.SAME_CARE_UNIT;
        shouldBeVisible.clear();
    }

    public String getSearchSession() {
        return searchSession;
    }

    public Visibility getCurrentVisibility() {
        return currentVisibility;
    }

    public Patient getPatient() {
        return patient;
    }

    public WithAccess<PdlContext> getCtx() {
        return ctx;
    }

    public void setPdlReport(PdlReport report) {
        this.pdlReport = report;
        calcVisibility();
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
        calcVisibility();
    }

    public Map<Visibility, Boolean> getShouldBeVisible() {
        return shouldBeVisible;
    }

    public boolean isShowOtherCareProviders() {
        return showOtherCareProviders;
    }

    public void setShowOtherCareProviders(boolean showOtherCareProviders) {
        this.showOtherCareProviders = showOtherCareProviders;
        calcVisibility();
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
                ", searchSession='" + searchSession + '\'' +
                ", currentVisibility=" + currentVisibility +
                ", shouldBeVisible=" + shouldBeVisible +
                '}';
    }

}
