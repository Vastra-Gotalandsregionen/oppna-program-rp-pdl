package se.vgregion.portal.controller.pdl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import se.vgregion.domain.pdl.*;
import se.vgregion.domain.pdl.decorators.WithInfoType;
import se.vgregion.domain.pdl.logging.PdlEventLog;
import se.vgregion.domain.pdl.logging.UserAction;
import se.vgregion.service.pdl.CareSystems;
import se.vgregion.service.pdl.ObjectRepo;
import se.vgregion.service.pdl.PatientRepository;
import se.vgregion.service.pdl.PdlService;

import javax.portlet.ActionResponse;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
    private CareSystems systems;
    @Autowired
    private PatientRepository patients;
    @Autowired
    private ObjectRepo objectRepo;

    @ModelAttribute("state")
    public PdlUserState initState() {
        if (state.getCtx() == null) {
            PdlContext ctx = currentContext();
            state.setCtx(ctx);
        }
        return state;
    }

    // Matches personnummer: 900313-1245, 990313+1245 (100yrs+)
    // Matches samordningsnummer: 910572-3453
    // Will not verify last control sum number
    //private final static String personSamordningsNummerRegex =
    //        "[0-9]{2}(0[0-9]|1[0-2])(0[0-9]|1[0-9]|2[0-9]|3[0-1]|6[0-9]|7[0-9]|8[0-9]|9[0-1])[-+][0-9]{4}";

    //@RequestMapping(value = "/searchPatient/{ssn:"+personSamordningsNummerRegex+"}", method = RequestMethod.GET)
    //@PathVariable("ssn") String ssn

    @RenderMapping
    public String enterSearchPatient() {
        state.reset(); // Make sure state is reset when user navigates to the start page.
        if (state.getCtx() == null) {
            PdlContext ctx = currentContext();
            state.setCtx(ctx);
        }
        return "view";
    }

    PdlEventLog newPdlEventLog() {
        PdlEventLog log = new PdlEventLog();
        Patient patient = state.getPatient();
        log.setPatientDisplayName(patient.getPatientDisplayName());
        log.setPatientId(patient.getPatientId());
        log.setEmployeeDisplayName(state.getCtx().employeeDisplayName);
        log.setEmployeeId(state.getCtx().employeeHsaId);
        log.setAssignmentId(state.getCurrentAssignment());
        log.setCareProviderDisplayName(state.getCtx().getCareProviderDisplayName());
        log.setCareProviderId(state.getCtx().getCareProviderHsaId());
        log.setCareUnitDisplayName(state.getCtx().getCareUnitDisplayName());
        log.setCareUnitId(state.getCtx().getCareUnitHsaId());
        log.setCreationTime(new Date());
        log.setSearchSession(state.getSearchSession());
        log.setSystemId("Regionportalen");
        return log;
    }

    @ActionMapping("searchPatient")
    public void searchPatientInformation(
            @RequestParam String patientId,
            @RequestParam String currentAssignment,
            @RequestParam boolean reset,
            ActionResponse response
    ) {
        if(reset){ // Support for going back without redoing the search
            state.reset(); // Make sure reset is called here when user uses back button and submits again.
            PdlProgress now = state.getCurrentProgress();
            state.setCurrentProgress(now.nextStep());
            state.setCurrentAssignment(currentAssignment);

            LOGGER.trace("Searching for patient {} in care systems.", patientId);

            state.setPatient(patients.byPatientId(patientId));

            List<WithInfoType<CareSystem>> careSystems = systems.byPatientId(state.getCtx(), patientId);

            //TODO 2013-11-18 : Magnus Andersson > Only do this if there are care systems!
            //TODO 2013-11-22 : Magnus Andersson > Should handle WithAccess and filter out unavailable systems.
            PdlReport pdlReport = pdl.pdlReport(state.getCtx(), state.getPatient(), careSystems);

            log(UserAction.SEARCH);

            // Reformat systems list into a format that we can display
            CareSystemsReport csReport = new CareSystemsReport(state.getCtx(), state.getCurrentAssignment(), pdlReport);

            state.setPdlReport(pdlReport);
            state.setCsReport(csReport);
        } else {
            state.setCurrentProgress(PdlProgress.firstStep().nextStep());
        }
        response.setRenderParameter("view", "pickInfoResource");
    }

    @ActionMapping("establishRelationConsent")
    public void establishRelationConsent(
            ActionResponse response,
            @RequestParam boolean emergency,
            @RequestParam boolean confirmed
    ) {
        if(state.getCurrentProgress().equals(PdlProgress.firstStep())) {
            response.setRenderParameter("view", "view");
        } else if(confirmed) {
            LOGGER.trace(
                    "Request to create both consent and relationship between employee {} and patient {}.",
                    state.getCtx().employeeHsaId,
                    state.getPatient().patientId
            );


            establishConsent(response, emergency, confirmed);
            establishRelationship(response, confirmed);

            state.setShowOtherCareUnits(true);
            state.setShowOtherCareProviders(true);
            response.setRenderParameter("view", "pickInfoResource");
        } else {
            state.setConfirmConsent(true);
            state.setConfirmRelation(true);
            response.setRenderParameter("view", "pickInfoResource");
        }
    }

    @ActionMapping("establishRelation")
    public void establishRelationship(
            ActionResponse response,
            @RequestParam boolean confirmed
    ) {
        if(state.getCurrentProgress().equals(PdlProgress.firstStep())) {
            response.setRenderParameter("view", "view");
        } else if(confirmed) {
            LOGGER.trace(
                "Request to create relationship between employee {} and patient {}.",
                state.getCtx().employeeHsaId,
                state.getPatient().patientId
            );

            PdlReport newReport = pdl.patientRelationship(
                state.getCtx(),
                state.getCurrentAssignment(),
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
        if(state.getCurrentProgress().equals(PdlProgress.firstStep())) {
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
        if(state.getCurrentProgress().equals(PdlProgress.firstStep())) {
            response.setRenderParameter("view", "view");
        } else if (confirmed) {
            LOGGER.trace(
                "Request to create consent between employee {} and patient {}.",
                state.getCtx().employeeHsaId,
                state.getPatient().patientId
            );

            // FIXME 2013-10-21 : Magnus Andersson > Should choose between consent or emergency. Also add possiblility to be represented by someone?
            state.setPdlReport(
                pdl.patientConsent(
                    state.getCtx(),
                    state.getCurrentAssignment(),
                    state.getPdlReport(),
                    state.getPatient().patientId,
                    "Reason",
                    1,
                    RoundedTimeUnit.NEAREST_HALF_HOUR,
                    ( emergency ) ? PdlReport.ConsentType.Emergency : PdlReport.ConsentType.Consent
                )
            );

            state.setConfirmConsent(false);
            state.setShowOtherCareProviders(true);

            log(UserAction.CONSENT);

            response.setRenderParameter("view", "pickInfoResource");
        } else {
            state.setConfirmConsent(true);
            state.setConfirmEmergency(emergency);
            response.setRenderParameter("view", "pickInfoResource");
        }
    }

    void log(UserAction action) {
        PdlEventLog log = newPdlEventLog();
        log.setUserAction(action);
        objectRepo.persist(log);
    }

    @ActionMapping("showOtherCareUnits")
    public void showOtherCareUnits(ActionResponse response) {
        if(state.getCurrentProgress().equals(PdlProgress.firstStep())) {
            response.setRenderParameter("view", "view");
        } else {
            LOGGER.trace(
                    "Request to show more information within same care giver for employee {} and patient {}.",
                    state.getCtx().employeeHsaId,
                    state.getPatient().patientId
            );

            state.setShowOtherCareUnits(true);

            log(UserAction.OTHER_CARE_UNITS);

            response.setRenderParameter("view", "pickInfoResource");
        }
    }

    @ActionMapping("selectInfoResource")
    public void selectInfoResource(
            @RequestParam String id,
            ActionResponse response
    ) {
        if(state.getCurrentProgress().equals(PdlProgress.firstStep())) {
            response.setRenderParameter("view", "view");
        } else {
           LOGGER.trace(
                   "Request to select information type with id {} for patient {}",
                   id,
                   state.getPatient().patientId
           );

            CareSystemsReport newCsReport =
                    state.getCsReport().selectInfoResource(id);

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
        if(state.getCurrentProgress().equals(PdlProgress.firstStep())) {
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

    @ActionMapping("showBlockedInformationTypes")
    public void showBlockedInformationTypes(
            @RequestParam Visibility visibility,
                ActionResponse response
    ) {
        if(state.getCurrentProgress().equals(PdlProgress.firstStep())) {
            response.setRenderParameter("view", "view");
        } else {
            LOGGER.trace(
                    "Request to show blocked information types for visibility {}",
                    visibility
            );

            CareSystemsReport newCsReport =
                    state.getCsReport().showBlocksForInfoType(visibility);

            state.setCsReport(newCsReport);

            log(UserAction.SHOW_BLOCKED_INFORMATION);

            response.setRenderParameter("view", "pickInfoResource");
        }
    }

    @ActionMapping("toggleInformation")
    public void toggleInformation(
            @RequestParam String id,
            @RequestParam boolean blocked,
            @RequestParam boolean revokeEmergency,
            ActionResponse response
    ) {
        if(state.getCurrentProgress().equals(PdlProgress.firstStep())) {
            response.setRenderParameter("view", "view");
        } else {
            LOGGER.trace(
                    "Request to select information type with id {} for patient {}",
                    id,
                    state.getPatient().patientId
            );

            CareSystemsReport newCsReport =
                    state.getCsReport().toggleInformation(id, blocked);

            state.setCsReport(newCsReport);

            if(revokeEmergency && blocked) {
                log(UserAction.BLOCK);
            } else if (!revokeEmergency && blocked) {
                log(UserAction.BLOCK_EMERGENCY);
            } else {
                log(UserAction.INFORMATION_CHOICE);
            }

            response.setRenderParameter("view", "pickInfoResource");
        }
    }

    @ActionMapping("showOtherCareProviders")
    public void showOtherCareProviders(ActionResponse response) {
        if(state.getCurrentProgress().equals(PdlProgress.firstStep())) {
            response.setRenderParameter("view", "view");
        } else {
            LOGGER.trace(
                "Request to show more information within same care giver for employee {} and patient {}.",
                state.getCtx().employeeHsaId,
                state.getPatient().patientId
            );


            state.setShowOtherCareProviders(true);

            log(UserAction.OTHER_CARE_PROVIDERS);

            response.setRenderParameter("view", "pickInfoResource");
        }
    }

    @ActionMapping("goToSummary")
    public void goToSummary(ActionResponse response) {
        if(state.getCurrentProgress().equals(PdlProgress.firstStep())) {
            response.setRenderParameter("view", "view");
        } else {
            state.setCurrentProgress(state.getCurrentProgress().nextStep());

            LOGGER.trace(
                    "Request to show summary employee {} and patient {}.",
                    state.getCtx().employeeHsaId,
                    state.getPatient().patientId
            );

            response.setRenderParameter("view", "showSummary");
        }
    }


    @RenderMapping(params = "view=pickInfoResource")
    public String searchResult(final ModelMap model) {
        return "pickInfoResource";
    }

    private PdlContext currentContext() {

        HashMap<String, AssignmentAccess> assignments = new HashMap<String, AssignmentAccess>();
        List<Access> otherProviders = Arrays.asList(Access.otherProvider("SE2321000131-E000000000001"));
        List<Access> sameProviders = Arrays.asList(Access.sameProvider("SE2321000131-S000000010252"), Access.sameProvider("SE2321000131-S000000010251"));
        assignments.put("SE2321000131-S000000010452", new AssignmentAccess("Sammanhållen Journalföring", otherProviders) );
        assignments.put("SE2321000131-S000000020452", new AssignmentAccess("Vård och behandling", sameProviders) );

        return new PdlContext(
                    "VGR",
                    "SE2321000131-E000000000001",
                    "Sahlgrenska, Radiologi 32",
                    "SE2321000131-S000000010252",
                    "Ludvig Läkare",
                    "SE2321000131-P000000069215",
                    assignments
                );
    }
}
