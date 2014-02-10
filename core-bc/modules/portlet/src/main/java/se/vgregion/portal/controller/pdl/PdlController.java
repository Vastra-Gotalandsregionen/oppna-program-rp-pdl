package se.vgregion.portal.controller.pdl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import se.vgregion.domain.decorators.*;
import se.vgregion.domain.logging.PdlEventLog;
import se.vgregion.domain.logging.UserAction;
import se.vgregion.domain.pdl.*;
import se.vgregion.domain.systems.CareSystem;
import se.vgregion.domain.systems.CareSystemsReport;
import se.vgregion.domain.systems.SummaryReport;
import se.vgregion.domain.systems.Visibility;
import se.vgregion.service.log.LogRepo;
import se.vgregion.service.search.AccessControl;
import se.vgregion.service.search.CareAgreement;
import se.vgregion.service.search.CareSystems;
import se.vgregion.service.search.PdlService;

import javax.portlet.ActionResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

@Controller
@RequestMapping(value = "VIEW")
@SessionAttributes("state")
public class PdlController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PdlController.class.getName());

    @Autowired
    private PdlService pdl;
    @Autowired
    private PdlUserState state;
    @Autowired
    @Qualifier("CareSystemsProxy")
    private CareSystems systems;
    @Autowired
    private LogRepo logRepo;
    @Autowired
    @Qualifier("MockAccessControl")
    private AccessControl accessControl;
    @Autowired
    private CareAgreement careAgreement;


    @ModelAttribute("state")
    public PdlUserState initState() {
        if (state.getCtx() == null) {
            WithOutcome<PdlContext> ctx = currentContext();
            state.setCtx(ctx);
        }
        return state;
    }

    @RenderMapping
    public String enterSearchPatient() {
        state.reset(); // Make sure state is reset when user navigates to the start page.
        if (state.getCtx() == null) {
            WithOutcome<PdlContext> ctx = currentContext();
            state.setCtx(ctx);
        }
        return "view";
    }

    PdlEventLog newPdlEventLog(UserAction action) {
        PdlEventLog log = new PdlEventLog();
        Patient patient = state.getPatient();
        PdlContext ctx = state.getCtx().value;

        log.setUserAction(action);
        log.setPatientDisplayName(patient.getPatientDisplayName());
        log.setPatientId(patient.getPatientId());
        log.setEmployeeDisplayName(ctx.employeeDisplayName);
        log.setEmployeeId(ctx.employeeHsaId);
        log.setAssignmentId(ctx.currentAssignment.assignmentHsaId);
        log.setCareProviderDisplayName(ctx.currentAssignment.careProviderDisplayName);
        log.setCareProviderId(ctx.currentAssignment.careProviderHsaId);
        log.setCareUnitDisplayName(ctx.currentAssignment.careUnitDisplayName);
        log.setCareUnitId(ctx.currentAssignment.careUnitHsaId);
        log.setCreationTime(new Date());
        log.setSearchSession(state.getSearchSession());
        log.setSystemId("Regionportalen - Sök Patient PDL");

        boolean notAttest = action != UserAction.ATTEST_RELATION;
        if(notAttest) {
            Maybe<String> maybeText = viewedSystemsLog();
            String text = maybeText.success ? maybeText.value : "";
            log.setLogText(text);
        }

        return log;
    }

    private Maybe<String> viewedSystemsLog() {
        StringBuilder viewedData = new StringBuilder();
        if(state.getCsReport() != null) {
            TreeMap<InfoTypeState<InformationType>, ArrayList<SystemState<CareSystem>>> careSystems =
                    state.getCsReport().aggregatedSystems.value;

            for(InfoTypeState<InformationType> key : careSystems.keySet()) {

                viewedData.
                        append("=== ").
                        append(key.value.getDesc().toUpperCase()).
                        append(" ===\n");

                if(key.isSelected()) {
                    for(SystemState<CareSystem> sys : careSystems.get(key)) {
                        if(sys.selected) {
                            viewedData.append("[X] ");
                        } else if(sys.needConfirmation){
                            viewedData.append("[B] ");
                        } else {
                            viewedData.append("[-] ");
                        }

                        viewedData.
                                append(sys.value.careProviderDisplayName).
                                append(" - ").
                                append(sys.value.careUnitDisplayName);

                        if(sys.blocked && sys.initiallyBlocked) {
                            viewedData.append(" (Spärrad information)");
                        } else if(!sys.blocked && sys.initiallyBlocked) {
                            viewedData.append(" (Passerad spärr)");
                        }
                        viewedData.append("\n");
                    }
                    viewedData.append("\n");
                }
            }
            return Maybe.some(viewedData.toString());
        }
        return Maybe.none();
    }

    void log(UserAction action) {
        PdlEventLog log = newPdlEventLog(action);
        logRepo.persist(log);
    }


    private final static String personSamordningsNummerRegex =
            "[1-2][0-9][0-9]{2}(0[0-9]|1[0-2])(0[0-9]|1[0-9]|2[0-9]|3[0-1]|6[0-9]|7[0-9]|8[0-9]|9[0-1])[0-9]{4}";

    @ActionMapping("searchPatient")
    public void searchPatientInformation(
            @RequestParam String patientId,
            @RequestParam String currentAssignment,
            @RequestParam boolean reset,
            ActionResponse response
    ) {
        if(patientId == null) {
            // Login has timed out and the user had to login, thus loosing the parameter for search.
            state.setCurrentProgress(PdlProgress.firstStep());
            response.setRenderParameter("view", "view");
        } else if(reset){ // Support for going back without redoing the search
            state.reset(); // Make sure reset is called here when user uses back button and submits again.
            PdlProgress now = state.getCurrentProgress();
            state.setCurrentProgress(now.nextStep());

            PdlContext ctx = state.getCtx().value;

            LOGGER.trace("Searching for patient {} in care systems.", patientId);


            WithOutcome<WithPatient<ArrayList<WithInfoType<CareSystem>>>> patientCareSystems = systems.byPatientId(ctx, patientId);

            // Extract patient
            state.setPatient(patientCareSystems.value.patient);

            WithOutcome<ArrayList<WithInfoType<CareSystem>>> careSystems =
                    patientCareSystems.mapValue(patientCareSystems.value.value);

            state.setSourcesNonSuccessOutcome(
                    !careSystems.success &&
                    careSystems.outcome != Outcome.UNFULFILLED_FAILURE
            );

            state.setMissingResults(careSystems.outcome == Outcome.UNFULFILLED_FAILURE);

            if(careSystems.value.size() > 0) {

                boolean availablePatient = AvailablePatient.check(ctx, careSystems.value);

                PdlReport pdlReport = pdl.pdlReport(
                        ctx,
                        state.getPatient(),
                        careSystems.value
                );

                PdlReport newReport = pdlReport;

                // TGP equivalent, create patient relationship
                if (availablePatient && pdlReport.hasPatientInformation && !pdlReport.hasRelationship.value) {
                    newReport = pdl.patientRelationship(
                        ctx,
                        pdlReport,
                        state.getPatient().patientId,
                        "Automatiskt skapad patientrelation: Patient finns tillgänglig sedan innan hos egen vårdenhet.",
                        1,
                        RoundedTimeUnit.NEAREST_HALF_HOUR
                    );
                }

                CareSystemsReport csReport = new CareSystemsReport(ctx.assignments.get(currentAssignment), newReport);

                state.setPdlReport(newReport);
                state.setCsReport(csReport);
                state.setCurrentAssignment(currentAssignment); // Must be here or null pointer exception since it calls calcVisibility
            }

            log(UserAction.SEARCH_PATIENT);
            response.setRenderParameter("view", "pickInfoResource");
        } else {
            state.setCurrentProgress(PdlProgress.firstStep().nextStep());
            response.setRenderParameter("view", "pickInfoResource");
        }
    }

    @ActionMapping("establishRelation")
    public void establishRelationship(
            ActionResponse response,
            @RequestParam boolean confirmed
    ) {
        if (state.getCurrentProgress().equals(PdlProgress.firstStep())) {
            response.setRenderParameter("view", "view");
        } else if(confirmed) {
            PdlContext ctx = state.getCtx().value;

            LOGGER.trace(
                "Request to create relationship between employee {} and patient {}.",
                ctx.employeeHsaId,
                state.getPatient().patientId
            );

            PdlReport newReport = pdl.patientRelationship(
                ctx,
                state.getPdlReport(),
                state.getPatient().patientId,
                "Reason",
                1,
                RoundedTimeUnit.NEAREST_HALF_HOUR
            );

            state.setPdlReport(newReport);

            log(UserAction.ATTEST_RELATION);

            response.setRenderParameter("view", "pickInfoResource");
        } else {
            response.setRenderParameter("view", "pickInfoResource");
        }
    }

    @ActionMapping("establishConsent")
    public void establishConsent(
            ActionResponse response,
            @RequestParam boolean emergency
    ) {
        if (state.getCurrentProgress().equals(PdlProgress.firstStep())) {
            response.setRenderParameter("view", "view");
        } else {
            PdlContext ctx = state.getCtx().value;

            LOGGER.trace(
                "Request to create consent between employee {} and patient {}.",
                ctx.employeeHsaId,
                state.getPatient().patientId
            );

            // FIXME 2013-10-21 : Magnus Andersson > Should choose between consent or emergency. Also add possiblility to be represented by someone?
            state.setPdlReport(
                pdl.patientConsent(
                    ctx,
                    state.getPdlReport(),
                    state.getPatient().patientId,
                    "Reason",
                    1,
                    RoundedTimeUnit.NEAREST_HALF_HOUR,
                    ( emergency ) ? PdlReport.ConsentType.Emergency : PdlReport.ConsentType.Consent
                )
            );

            if(emergency) {
                log(UserAction.EMERGENCY_CONSENT);
            } else {
                log(UserAction.ATTEST_CONSENT);
            }

            // Hand over to select info resource again. Provide the stashed information decorator id
            selectInfoResource(state.getConsentInformationTypeId(), response);
        }
    }


    @ActionMapping("selectInfoResource")
    public void selectInfoResource(
            @RequestParam String id,
            ActionResponse response
    ) {
        if (state.getCurrentProgress().equals(PdlProgress.firstStep())) {
            response.setRenderParameter("view", "view");
        } else {
            LOGGER.trace(
                    "Request to select information decorator with id {} for patient {}",
                    id,
                    state.getPatient().patientId
            );
            state.setConsentInformationTypeId(null); // Reset left over id

            boolean otherProviders =
                    state.getCtx().value.currentAssignment.isOtherProviders() &&
                    state.getPdlReport().consent.value.hasConsent;

            boolean sameProvider = !state.getCtx().value.currentAssignment.isOtherProviders();

            if(sameProvider || otherProviders) {
                CareSystemsReport newCsReport =
                        state.getCsReport().selectInfoResource(id);

                if(state.getCtx().value.currentAssignment.otherUnits) {
                    state.setCurrentVisibility(Visibility.OTHER_CARE_UNIT);
                } else if(state.getCtx().value.currentAssignment.otherProviders) {
                    state.setCurrentVisibility(Visibility.OTHER_CARE_PROVIDER);
                }

                state.setCsReport(newCsReport);

                if(sameProvider) {
                    log(UserAction.REVEAL_OTHER_UNITS);
                } else if(sameProvider) {
                    log(UserAction.REVEAL_OTHER_PROVIDER);
                }

                response.setRenderParameter("view", "pickInfoResource");
            } else {
                state.setConsentInformationTypeId(id); // Save the id until after consent has been established
                response.setRenderParameter("view", "establishConsent");
            }
        }
    }

    @ActionMapping("showBlockedInformation")
    public void showBlockedInformation(
            @RequestParam String id,
            ActionResponse response
    ) {
        if (state.getCurrentProgress().equals(PdlProgress.firstStep())) {
            response.setRenderParameter("view", "view");
        } else {
            LOGGER.trace(
                    "Request to show blocked information categorized with information decorator with id {} for patient {}",
                    id,
                    state.getPatient().patientId
            );

            CareSystemsReport newCsReport =
                    state.getCsReport().showBlocksForInfoResource(id);

            state.setCsReport(newCsReport);

            log(UserAction.REVEAL_BLOCKED);

            response.setRenderParameter("view", "pickInfoResource");
        }
    }

     @ActionMapping("cancelRevokeConfirmation")
    public void cancelRevokeConfirmation(
            @RequestParam String id,
            ActionResponse response
    ) {
        if (state.getCurrentProgress().equals(PdlProgress.firstStep())) {
            response.setRenderParameter("view", "view");
        } else {
            LOGGER.trace(
                    "Request to select information decorator with id {} for patient {}",
                    id,
                    state.getPatient().patientId
            );

            CareSystemsReport newCsReport =
                    state.getCsReport().toggleInformation(id, false);

            state.setCsReport(newCsReport);

            response.setRenderParameter("view", "pickInfoResource");
        }
    }

    @ActionMapping("toggleInformation")
    public void toggleInformation(
            @RequestParam String id,
            @RequestParam boolean confirmed,
            @RequestParam boolean revokeEmergency,
            ActionResponse response
    ) {
        if (state.getCurrentProgress().equals(PdlProgress.firstStep())) {
            response.setRenderParameter("view", "view");
        } else {
            LOGGER.trace(
                    "Request to select information decorator with id {} for patient {}",
                    id,
                    state.getPatient().patientId
            );

            CareSystemsReport newCsReport =
                    state.getCsReport().toggleInformation(id, confirmed);

            state.setCsReport(newCsReport);

            if (revokeEmergency && confirmed) {
                log(UserAction.PASS_BLOCKED);
            } else if (!revokeEmergency && confirmed) {
                log(UserAction.EMERGENCY_PASS_BLOCKED);
            }

            response.setRenderParameter("view", "pickInfoResource");
        }
    }

    @ActionMapping("goToSummary")
    public void goToSummary(ActionResponse response) {
        if (state.getCurrentProgress().equals(PdlProgress.firstStep())) {
            response.setRenderParameter("view", "view");
        } else {
            state.setCurrentProgress(state.getCurrentProgress().nextStep());
            PdlContext ctx = state.getCtx().value;

            LOGGER.trace(
                    "Request to show summary employee {} and patient {}.",
                    ctx.employeeHsaId,
                    state.getPatient().patientId
            );

            SummaryReport sumReport = new SummaryReport(state.getCsReport().aggregatedSystems.value);
            state.setSumReport(sumReport);

            log(UserAction.SUMMARY_SYSTEMS);

            response.setRenderParameter("view", "showSummary");
        }
    }

    @RenderMapping(params = "view=view")
    public String start(final ModelMap model) {
        return "view";
    }

    @RenderMapping(params = "view=establishConsent")
    public String establishConsent(final ModelMap model) {
        return "establishConsent";
    }

    @RenderMapping(params = "view=pickInfoResource")
    public String searchResult(final ModelMap model) {
        return "pickInfoResource";
    }

    @RenderMapping(params = "view=showSummary")
    public String showSummary(final ModelMap model) {
        return "showSummary";
    }

    private WithOutcome<PdlContext> currentContext() {
        return accessControl.getContextByEmployeeId("SE2321000131-P000000000977");
    }

    @Override
    public String toString() {
        return "PdlController{" +
                "pdl=" + pdl +
                ", state=" + state +
                ", systems=" + systems +
                ", logRepo=" + logRepo +
                ", accessControl=" + accessControl +
                ", careAgreement=" + careAgreement +
                '}';
    }
}
