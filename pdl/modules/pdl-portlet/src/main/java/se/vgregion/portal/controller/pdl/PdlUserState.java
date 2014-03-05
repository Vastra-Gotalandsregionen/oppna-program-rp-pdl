package se.vgregion.portal.controller.pdl;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import se.vgregion.domain.pdl.*;
import se.vgregion.domain.decorators.WithOutcome;
import se.vgregion.domain.systems.CareSystemsReport;
import se.vgregion.domain.systems.SummaryReport;
import se.vgregion.domain.systems.Visibility;
import se.vgregion.events.context.Patient;
import se.vgregion.domain.pdl.PdlContext;

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
    private WithOutcome<PdlContext> ctx;
    private boolean patientInformationExist = false;
    private String searchSession = java.util.UUID.randomUUID().toString();
    private Visibility currentVisibility = Visibility.SAME_CARE_UNIT;
    private final Map<String, Boolean> shouldBeVisible = new HashMap<String, Boolean>();
    private PdlProgress currentProgress = PdlProgress.firstStep();
    private String consentInformationTypeId = null;
    private boolean sourcesNonSuccessOutcome = false;
    private boolean missingResults = false;
    private boolean invalid = false;

    private void calcVisibility() {
        shouldBeVisible.clear();
        shouldBeVisible.put(
                Visibility.SAME_CARE_UNIT.name(),
                pdlReport.hasRelationship.value);

        shouldBeVisible.put(
                Visibility.OTHER_CARE_UNIT.name(),
                ctx.value.currentAssignment.isOtherUnits() &&
                pdlReport.hasRelationship.value);

        shouldBeVisible.put(
                Visibility.OTHER_CARE_PROVIDER.name(),
                ctx.value.currentAssignment.isOtherProviders() &&
                pdlReport.hasRelationship.value);

        patientInformationExist = csReport.availablePatientInformation;
    }

    public PdlReport getPdlReport() {
        return pdlReport;
    }

    public void reset() {
        patientInformationExist = false;
        sourcesNonSuccessOutcome = false;
        invalid = false;
        missingResults = false;
        pdlReport = null;
        csReport = null;
        sumReport = null;
        consentInformationTypeId = null;

        searchSession = java.util.UUID.randomUUID().toString();
        currentVisibility = Visibility.SAME_CARE_UNIT;
        shouldBeVisible.clear();
        currentProgress = PdlProgress.firstStep();
    }

    public SummaryReport getSumReport() {
        return sumReport;
    }

    public String getConsentInformationTypeId() {
        return consentInformationTypeId;
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

    public void setConsentInformationTypeId(String consentInformationTypeId) {
        this.consentInformationTypeId = consentInformationTypeId;
    }

    public Visibility getCurrentVisibility() {
        return currentVisibility;
    }

    public Patient getPatient() {
        return patient;
    }

    public WithOutcome<PdlContext> getCtx() {
        return ctx;
    }

    public void setPdlReport(PdlReport report) {
        this.pdlReport = report;
    }

    public CareSystemsReport getCsReport() {
        return csReport;
    }

    public boolean isPatientInformationExist() {
        return patientInformationExist;
    }

    public void setPatientInformationExist(boolean patientInformationExist) {
        this.patientInformationExist = patientInformationExist;
    }

    public boolean isSourcesNonSuccessOutcome() {
        return sourcesNonSuccessOutcome;
    }

    public void setSourcesNonSuccessOutcome(boolean sourcesNonSuccessOutcome) {
        this.sourcesNonSuccessOutcome = sourcesNonSuccessOutcome;
    }

    public void setCsReport(CareSystemsReport csReport) {
        this.csReport = csReport;
        calcVisibility();
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public void setCtx(WithOutcome<PdlContext> ctx) {
        this.ctx = ctx;
    }

    public Map<String, Boolean> getShouldBeVisible() {
        return shouldBeVisible;
    }

    public void setCurrentProgress(PdlProgress currentProgress) {
        this.currentProgress = currentProgress;
    }

    public void setCurrentVisibility(Visibility currentVisibility) {
        this.currentVisibility = currentVisibility;
    }

    public void setCurrentAssignment(String currentAssignment) {
        PdlContext newCtx = this.ctx.value.changeAssignment(currentAssignment);
        this.ctx = (this.ctx.mapValue(newCtx));
        calcVisibility();
    }

    public boolean isMissingResults() {
        return missingResults;
    }

    public void setMissingResults(boolean missingResults) {
        this.missingResults = missingResults;
    }

    public boolean isInvalid() {
        return invalid;
    }

    public void setInvalid(boolean invalid) {
        this.invalid = invalid;
    }

    @Override
    public String toString() {
        return "PdlUserState{" +
                "pdlReport=" + pdlReport +
                ", csReport=" + csReport +
                ", sumReport=" + sumReport +
                ", patient=" + patient +
                ", ctx=" + ctx +
                ", patientInformationExist=" + patientInformationExist +
                ", searchSession='" + searchSession + '\'' +
                ", currentVisibility=" + currentVisibility +
                ", shouldBeVisible=" + shouldBeVisible +
                ", currentProgress=" + currentProgress +
                ", consentInformationTypeId='" + consentInformationTypeId + '\'' +
                ", sourcesNonSuccessOutcome=" + sourcesNonSuccessOutcome +
                ", missingResults=" + missingResults +
                ", invalid=" + invalid +
                '}';
    }
}
