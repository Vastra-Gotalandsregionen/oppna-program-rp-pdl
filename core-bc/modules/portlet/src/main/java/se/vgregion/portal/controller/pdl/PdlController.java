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
import se.vgregion.domain.pdl.decorators.WithAccess;
import se.vgregion.domain.pdl.decorators.WithInfoType;
import se.vgregion.domain.pdl.logging.PdlEventLog;
import se.vgregion.domain.pdl.logging.UserAction;
import se.vgregion.service.pdl.AccessControl;
import se.vgregion.service.pdl.CareSystems;
import se.vgregion.service.pdl.ObjectRepo;
import se.vgregion.service.pdl.PatientRepository;
import se.vgregion.service.pdl.PdlService;

import javax.portlet.ActionResponse;
import java.util.*;

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
    @Autowired
    private PatientRepository patients;
    @Autowired
    private AccessControl access;

    @ModelAttribute("state")
    public PdlUserState initState() {
        if (state.getCtx() == null) {
            WithAccess<PdlContext> ctx = access.authorize(currentContext());
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
        return "view";
    }

    PdlEventLog getPdlEventLog() {
        PdlEventLog log = new PdlEventLog();
        Patient patient = state.getPatient();
        log.setPatientDisplayName(patient.getPatientDisplayName());
        log.setPatientId(patient.getPatientId());
        log.setEmployeeId(state.getCtx().employeeHsaId);
        log.setAssignmentId(state.getCtx().assignmentHsaId);
        log.setCareProviderId(state.getCtx().getCareProviderHsaId());
        log.setCareUnitId(state.getCtx().getCareUnitHsaId());
        return log;
    }

    @ActionMapping("searchPatient")
    public void searchPatientInformation(
            @RequestParam String patientId,
            ActionResponse response
    ) {
        state.reset(); // Make sure reset is called here when user uses back button and submits again.
        LOGGER.trace("Searching for patient {} in care systems.", patientId);

        state.setPatient(patients.byPatientId(patientId));

        List<WithInfoType<CareSystem>> careSystems = systems.byPatientId(state.getCtx().value, patientId);

        //TODO 2013-11-18 : Magnus Andersson > Only do this if there are care systems!
        //TODO 2013-11-22 : Magnus Andersson > Should handle WithAccess and filter out unavailable systems.
        PdlReport pdlReport = pdl.pdlReport(state.getCtx().value, state.getPatient(), careSystems);

        log(UserAction.SEARCH);

        // Reformat systems list into a format that we can display
        CareSystemsReport csReport = new CareSystemsReport(state.getCtx(), pdlReport);

        state.setPdlReport(pdlReport);
        state.setCsReport(csReport);

        response.setRenderParameter("view", "pickInfoResource");
    }

    @ActionMapping("establishRelationConsent")
    public void establishRelationConsent(
            ActionResponse response,
            @RequestParam String emergency
    ) {
        LOGGER.trace(
            "Request to create both consent and relationship between employee {} and patient {}.",
            state.getCtx().value.employeeHsaId,
            state.getPatient().patientId
        );

        establishConsent(response);
        establishRelationship(response);

        response.setRenderParameter("view", "pickInfoResource");
    }

    @ActionMapping("establishRelation")
    public void establishRelationship(ActionResponse response) {
        LOGGER.trace(
            "Request to create relationship between employee {} and patient {}.",
            state.getCtx().value.employeeHsaId,
            state.getPatient().patientId
        );

        PdlReport newReport = pdl.patientRelationship(
            state.getCtx().value,
            state.getPdlReport(),
            state.getPatient().patientId,
            "Reason",
            1,
            RoundedTimeUnit.NEAREST_HALF_HOUR
        );

        state.setPdlReport(newReport);

        log(UserAction.RELATION);

        response.setRenderParameter("view", "pickInfoResource");
    }

    @ActionMapping("establishConsent")
    public void establishConsent(ActionResponse response) {
        LOGGER.trace(
            "Request to create consent between employee {} and patient {}.",
            state.getCtx().value.employeeHsaId,
            state.getPatient().patientId
        );

        // FIXME 2013-10-21 : Magnus Andersson > Should choose between consent or emergency. Also add possiblility to be represented by someone?
        state.setPdlReport(
            pdl.patientConsent(
                state.getCtx().value,
                state.getPdlReport(),
                state.getPatient().patientId,
                "Reason",
                1,
                RoundedTimeUnit.NEAREST_HALF_HOUR,
                PdlReport.ConsentType.Consent
            )
        );

        log(UserAction.CONSENT);

        response.setRenderParameter("view", "pickInfoResource");
    }

    void log(UserAction action) {
        PdlEventLog log = getPdlEventLog();
        log.setUserAction(action);
        objectRepo.persist(log);
    }

    @ActionMapping("showOtherCareUnits")
    public void showOtherCareUnits(ActionResponse response) {
        LOGGER.trace(
                "Request to show more information within same care giver for employee {} and patient {}.",
                state.getCtx().value.employeeHsaId,
                state.getPatient().patientId
        );

        // LOG SERVICE CALL

        state.setShowOtherCareUnits(true);

        log(UserAction.OTHER_CARE_UNITS);

        response.setRenderParameter("view", "pickInfoResource");
    }

    @ActionMapping("showOtherCareProviders")
    public void showOtherCareProviders(ActionResponse response) {
        LOGGER.trace(
            "Request to show more information within same care giver for employee {} and patient {}.",
            state.getCtx().value.employeeHsaId,
            state.getPatient().patientId
        );

        // LOG SERVICE CALL

        state.setShowOtherCareProviders(true);

        log(UserAction.OTHER_CARE_PROVIDERS);

        response.setRenderParameter("view", "pickInfoResource");
    }



    @RenderMapping(params = "view=pickInfoResource")
    public String searchResult(final ModelMap model) {
        return "pickInfoResource";
    }

    private PdlContext currentContext() {
        return new PdlContext(
                "SE2321000131-E000000000001",
                "SE2321000131-S000000010252",
                "SE2321000131-P000000069215",
                "Sammanhållen Journalföring",
                "SE2321000131-S000000010452");
    }
}
