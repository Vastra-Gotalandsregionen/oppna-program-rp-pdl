package se.vgregion.portal.controller.pdl;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import se.vgregion.domain.pdl.*;

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
    private SummaryReport sumReport;

    private Patient patient;

    private PdlContext ctx;
    private String currentAssignment;
    private boolean showOtherCareUnits = false;
    private boolean showOtherCareProviders = false;
    private boolean confirmConsent = false;
    private boolean confirmRelation = true;
    private boolean confirmEmergency = false;
    private String searchSession = java.util.UUID.randomUUID().toString();
    private Visibility currentVisibility = Visibility.SAME_CARE_UNIT;
    private final Map<Visibility, Boolean> shouldBeVisible = new HashMap<Visibility, Boolean>();
    private PdlProgress currentProgress = PdlProgress.firstStep();

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

    public boolean isConfirmEmergency() {
        return confirmEmergency;
    }

    public void setConfirmEmergency(boolean confirmEmergency) {
        this.confirmEmergency = confirmEmergency;
    }

    public void reset() {
        showOtherCareUnits = false;
        showOtherCareProviders = false;
        confirmConsent = false;
        confirmRelation = true;
        confirmEmergency = false;
        pdlReport = null;
        csReport = null;
        sumReport = null;
        searchSession = java.util.UUID.randomUUID().toString();
        currentVisibility = Visibility.SAME_CARE_UNIT;
        shouldBeVisible.clear();
        currentProgress = PdlProgress.firstStep();
    }

    public SummaryReport getSumReport() {
        return sumReport;
    }

    public void setSumReport(SummaryReport sumReport) {
        this.sumReport = sumReport;
    }

    public String getSearchSession() {
        return searchSession;
    }

    public PdlProgress getCurrentProgress() {
        return currentProgress;
    }

    public Visibility getCurrentVisibility() {
        return currentVisibility;
    }

    public Patient getPatient() {
        return patient;
    }

    public PdlContext getCtx() {
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

    public void setCtx(PdlContext ctx) {
        this.ctx = ctx;
        Map<String, AssignmentAccess> assign = ctx.getAssignments();
        if(assign.size() > 0) {
            String[] keys = assign.keySet().toArray(new String[]{});
            this.currentAssignment = keys[0];
        }
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

    public void setCurrentProgress(PdlProgress currentProgress) {
        this.currentProgress = currentProgress;
    }

    public boolean isConfirmConsent() {
        return confirmConsent;
    }

    public void setConfirmConsent(boolean confirmConsent) {
        this.confirmConsent = confirmConsent;
    }

    public boolean isConfirmRelation() {
        return confirmRelation;
    }

    public void setConfirmRelation(boolean confirmRelation) {
        this.confirmRelation = confirmRelation;
    }

    public String getCurrentAssignment() {
        return currentAssignment;
    }

    public void setCurrentAssignment(String currentAssignment) {
        this.currentAssignment = currentAssignment;
        if(this.ctx.assignments.get(currentAssignment).isOtherProviders()) {
            this.showOtherCareUnits = true; // Skip other care units question for SJF
        }
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
                ", confirmConsent=" + confirmConsent +
                ", confirmRelation=" + confirmRelation +
                ", confirmEmergency=" + confirmEmergency +
                ", searchSession='" + searchSession + '\'' +
                ", currentVisibility=" + currentVisibility +
                ", shouldBeVisible=" + shouldBeVisible +
                ", currentProgress=" + currentProgress +
                ", currentAssignment='" + currentAssignment + '\'' +
                '}';
    }
}