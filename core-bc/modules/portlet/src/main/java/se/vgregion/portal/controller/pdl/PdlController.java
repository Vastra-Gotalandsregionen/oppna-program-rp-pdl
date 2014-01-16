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
import se.vgregion.domain.decorators.WithInfoType;
import se.vgregion.domain.decorators.WithOutcome;
import se.vgregion.domain.logging.PdlEventLog;
import se.vgregion.domain.logging.UserAction;
import se.vgregion.domain.pdl.*;
import se.vgregion.service.pdl.*;

import javax.portlet.ActionResponse;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

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
    @Qualifier("CareSystemsImpl")
    private CareSystems systems;
    @Autowired
    private PatientRepository patients;
    @Autowired
    private ObjectRepo objectRepo;
    @Autowired
    @Qualifier("MockAccessControl")
    private AccessControl accessControl;

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

    PdlEventLog newPdlEventLog() {
        PdlEventLog log = new PdlEventLog();
        Patient patient = state.getPatient();
        PdlContext ctx = state.getCtx().value;

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
        log.setSystemId("Regionportalen");

        TreeSet viewedData = new TreeSet();

        // This code must be changed whenever the view code is. So that it logs relevant information, such as what
        // information is being displayed to the user.

        // TODO 2013-12-20 : Magnus Andersson > Commenting out while refactoring
//        Set<Map.Entry<InfoTypeState<InformationType>, ArrayList<SystemState<CareSystem>>>> entries = state.getCsReport().getAggregatedSystems().getValue().entrySet();
//        for (Map.Entry<InfoTypeState<InformationType>, ArrayList<SystemState<CareSystem>>> entry : entries) {
//            if (state.getShouldBeVisible().get(entry.getKey().lowestVisibility) // state.shouldBeVisible[infoSelection.key.lowestVisibility] &&
//                    && (entry.getKey().containsOnlyBlocked.get(state.getCurrentVisibility()) // (infoSelection.key.containsOnlyBlocked[state.currentVisibility]
//                    && entry.getKey().viewBlocked // && infoSelection.key.viewBlocked ||
//                    || entry.getKey().containsOnlyBlocked.get(state.getCurrentVisibility()) //!infoSelection.key.containsOnlyBlocked[state.currentVisibility])
//            )) {
//                ArrayList<SystemState<CareSystem>> items = entry.getValue();
//                // {system.value.careProviderDisplayName} - ${system.value.careUnitDisplayName}
//                for (SystemState<CareSystem> system : items) {
//                    String providerHsaIdId = system.value.careProviderHsaId;
//                    String unitHsaId = system.value.careUnitHsaId;
//                    viewedData.add(providerHsaIdId + "/" + unitHsaId);
//                }
//            }
//        }

        log.setLogText(viewedData.toString());

        return log;
    }

    void log(UserAction action) {
        // TODO 2013-12-20 : Magnus Andersson > Commented out because it causes null pointers at runtime.
//        PdlEventLog log = newPdlEventLog();
//        log.setUserAction(action);
//        objectRepo.persist(log);
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

            state.setPatient(patients.byPatientId(patientId));

            List<WithInfoType<CareSystem>> careSystems = systems.byPatientId(ctx, patientId);

            if(careSystems.size() > 0) {

                boolean availablePatient = AvailablePatient.check(ctx, careSystems);

                PdlReport pdlReport = pdl.pdlReport(
                        ctx,
                        state.getPatient(),
                        careSystems
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

                CareSystemsReport csReport = new CareSystemsReport(ctx.currentAssignment, newReport);

                state.setPdlReport(newReport);
                state.setCsReport(csReport);
                state.setCurrentAssignment(currentAssignment);
            }

            log(UserAction.SEARCH);
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

            state.setConfirmRelation(false);
            state.setPdlReport(newReport);

            log(UserAction.RELATION);

            response.setRenderParameter("view", "pickInfoResource");
        } else {
            state.setConfirmRelation(true);
            response.setRenderParameter("view", "pickInfoResource");
        }
    }

    @ActionMapping("cancelConfirmation")
    public void cancelConfirmation(
            ActionResponse response
    ) {
        if (state.getCurrentProgress().equals(PdlProgress.firstStep())) {
            response.setRenderParameter("view", "view");
        } else {
            LOGGER.trace(
                    "Request to cancel confirmation. Relation confirmation {} and consent confirmation {}.",
                    state.isConfirmRelation(),
                    state.isConfirmConsent()
            );

            state.setConfirmConsent(false);
            state.setConfirmRelation(true);
            state.setConfirmEmergency(false);

            response.setRenderParameter("view", "pickInfoResource");
        }
    }

    @ActionMapping("establishConsent")
    public void establishConsent(
            ActionResponse response,
            @RequestParam boolean emergency,
            @RequestParam boolean confirmed
    ) {
        if (state.getCurrentProgress().equals(PdlProgress.firstStep())) {
            response.setRenderParameter("view", "view");
        } else if (confirmed) {
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

            state.setConfirmConsent(false);

            log(UserAction.CONSENT);

            response.setRenderParameter("view", "pickInfoResource");
        } else {
            state.setConfirmConsent(true);
            state.setConfirmRelation(false);
            state.setConfirmEmergency(emergency);
            response.setRenderParameter("view", "pickInfoResource");
        }
    }

    @ActionMapping("showOtherCareUnits")
    public void showOtherCareUnits(ActionResponse response) {
        if (state.getCurrentProgress().equals(PdlProgress.firstStep())) {
            response.setRenderParameter("view", "view");
        } else {
            PdlContext ctx = state.getCtx().value;

            LOGGER.trace(
                    "Request to show more information within same care giver for employee {} and patient {}.",
                    ctx.employeeHsaId,
                    state.getPatient().patientId
            );

            log(UserAction.OTHER_CARE_UNITS);

            response.setRenderParameter("view", "pickInfoResource");
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
                    "Request to select information type with id {} for patient {}",
                    id,
                    state.getPatient().patientId
            );

            CareSystemsReport newCsReport =
                    state.getCsReport().selectInfoResource(id);

            if(state.getCtx().value.currentAssignment.otherUnits) {
                state.setCurrentVisibility(Visibility.OTHER_CARE_UNIT);
            } else if(state.getCtx().value.currentAssignment.otherProviders) {
                state.setCurrentVisibility(Visibility.OTHER_CARE_PROVIDER);
            }

            state.setCsReport(newCsReport);

            log(UserAction.INFORMATION_CHOICE);

            response.setRenderParameter("view", "pickInfoResource");
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
                    "Request to show blocked information categorized with information type with id {} for patient {}",
                    id,
                    state.getPatient().patientId
            );

            CareSystemsReport newCsReport =
                    state.getCsReport().showBlocksForInfoResource(id);

            state.setCsReport(newCsReport);

            log(UserAction.SHOW_BLOCKED_INFORMATION);

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
                    "Request to select information type with id {} for patient {}",
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
                    "Request to select information type with id {} for patient {}",
                    id,
                    state.getPatient().patientId
            );

            CareSystemsReport newCsReport =
                    state.getCsReport().toggleInformation(id, confirmed);

            state.setCsReport(newCsReport);

            if (revokeEmergency && confirmed) {
                log(UserAction.BLOCK);
            } else if (!revokeEmergency && confirmed) {
                log(UserAction.BLOCK_EMERGENCY);
            } else {
                log(UserAction.INFORMATION_CHOICE);
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

            response.setRenderParameter("view", "showSummary");
        }
    }

    @RenderMapping(params = "view=view")
    public String start(final ModelMap model) {
        return "view";
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

}
