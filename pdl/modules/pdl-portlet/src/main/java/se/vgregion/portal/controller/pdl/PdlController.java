package se.vgregion.portal.controller.pdl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import se.vgregion.domain.assignment.Assignment;
import se.vgregion.domain.decorators.*;
import se.vgregion.domain.logging.UserAction;
import se.vgregion.domain.pdl.InformationType;
import se.vgregion.domain.pdl.PdlContext;
import se.vgregion.domain.pdl.PdlReport;
import se.vgregion.domain.pdl.RoundedTimeUnit;
import se.vgregion.domain.systems.*;
import se.vgregion.events.PersonIdUtil;
import se.vgregion.events.context.PatientEvent;
import se.vgregion.events.context.PdlTicket;
import se.vgregion.events.context.UserContext;
import se.vgregion.portal.bfr.infobroker.domain.InfobrokerPersonIdType;
import se.vgregion.portal.util.UserUtil;
import se.vgregion.repo.log.LogRepo;
import se.vgregion.service.search.*;import se.vgregion.service.search.AccessControl;
import se.vgregion.service.search.CareAgreement;
import se.vgregion.service.search.CareSystems;
import se.vgregion.service.search.PdlService;
import se.vgregion.service.sources.CareSystemUrls;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.xml.namespace.QName;
import java.io.IOException;
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
    @Qualifier("CareSystemsProxy")
    private CareSystems systems;
    @Autowired
    private LogRepo logRepo;
    @Autowired
    @Qualifier("MockAccessControl")
    private AccessControl accessControl;
    @Autowired
    private CareAgreement careAgreement;
    @Autowired
    private CareSystemUrls careSystemUrls;
    @Autowired
    private LdapService ldapService;

    private UserUtil userUtil = new UserUtil();

    @ModelAttribute("state")
    public PdlUserState initState(PortletRequest request) {
        if (state == null || state.getCtx() == null) {
            WithOutcome<PdlContext> ctx = currentContext(request);
            state.setCtx(ctx);
        }
        return state;
    }

    @ModelAttribute(value = "infobrokerPersonIdTypeList")
    public List<InfobrokerPersonIdType> getInfobrokerPersonIdTypeList() {
        return Arrays.asList(InfobrokerPersonIdType.values());
    }

    @RenderMapping
    public String enterSearchPatient(PortletRequest request) {
        state.reset(); // Make sure state is reset when user navigates to the start page.
        if (state.getCtx() == null) {
            WithOutcome<PdlContext> ctx = currentContext(request);
            state.setCtx(ctx);
        }
        return "view";
    }

    private final static String personSamordningsNummerRegex =
            "[1-2][0-9][0-9]{2}(0[0-9]|1[0-2])(0[0-9]|1[0-9]|2[0-9]|3[0-1]|6[0-9]|7[0-9]|8[0-9]|9[0-1])[0-9]{4}";

    @ActionMapping("searchPatient")
    public void searchPatientInformation(
            @RequestParam String patientId,
            @RequestParam String patientIdType,
            @RequestParam String currentAssignment,
            @RequestParam boolean reset,
            ActionRequest request,
            ActionResponse response,
            Model model
    ) throws IOException {
        if(patientId == null) {
            // Login has timed out and the user had to login, thus loosing the parameter for search.
            state.setCurrentProgress(PdlProgress.firstStep());
            response.setRenderParameter("view", "view");
        } else if(reset) { // Support for going back without redoing the search
            state.reset(); // Make sure reset is called here when user uses back button and submits again.
            PdlProgress now = state.getCurrentProgress();
            state.setCurrentProgress(now.nextStep());

            PdlContext ctx = state.getCtx().value;

            String patientIdTrimmed = trimmedPatientId(patientId);

            LOGGER.trace("Searching for patient {} - {} in care systems.", patientIdTrimmed, patientIdType);

            InfobrokerPersonIdType pidtype = InfobrokerPersonIdType.valueOf(patientIdType);

            boolean isValidPatientId = (pidtype == InfobrokerPersonIdType.PAT_PERS_NR || pidtype == InfobrokerPersonIdType.PAT_SAMO_NR) ?
                patientIdTrimmed.length() == 12 && PersonIdUtil.personIdIsValid(patientIdTrimmed) : true;

            if(isValidPatientId) {

                WithOutcome<WithPatient<ArrayList<WithInfoType<CareSystem>>>> patientCareSystems =
                        systems.byPatientId(ctx, patientIdTrimmed, pidtype);

                // Extract patient
                state.setPatient(patientCareSystems.value.patient);

                WithOutcome<ArrayList<WithInfoType<CareSystem>>> careSystems =
                        patientCareSystems.mapValue(patientCareSystems.value.value);

                state.setSourcesNonSuccessOutcome(
                        !careSystems.success &&
                        careSystems.outcome != Outcome.UNFULFILLED_FAILURE
                );

                state.setMissingResults(careSystems.outcome == Outcome.UNFULFILLED_FAILURE);

                boolean hasCareSystems = careSystems.value.size() > 0;

                if(hasCareSystems) {

                    PdlReport newReport = null;

                    PortletPreferences prefs = request.getPreferences();
                    int timeUnits = Integer.parseInt(prefs.getValue("establishRelationDuration", "1"));
                    RoundedTimeUnit duration = RoundedTimeUnit.valueOf(prefs.getValue("establishRelationTimeUnits", RoundedTimeUnit.NEAREST_DAY.toString()));

                    // Security Services only supports Social Security Number or Samordningsnummer.
                    if(pidtype == InfobrokerPersonIdType.PAT_PERS_NR || pidtype == InfobrokerPersonIdType.PAT_SAMO_NR) {
                        newReport = fetchPdlReport(ctx, careSystems, timeUnits, duration);
                    } else {
                        newReport = PdlReport.defaultReport(careSystems);
                    }

                    CareSystemsReport csReport = new CareSystemsReport(ctx.assignments.get(currentAssignment), newReport);
                    state.setPdlReport(newReport);
                    state.setCsReport(csReport);
                    state.setCurrentAssignment(currentAssignment); // Must be here or null pointer exception since it calls calcVisibility


                }


                PdlLogger.log(UserAction.SEARCH_PATIENT, logRepo, state);

                // Reset patient in other views
                QName qname = new QName("http://pdl.portalen.vgregion.se/events", "pctx.reset");
                response.setEvent(qname, new PatientEvent(null, null));

                if (hasCareSystems &&
                    state.getPdlReport().hasRelationship.value &&
                    onlySameCareUnit(state))
                {
                    shortcutDirectlyToViewer(request, response, model);
                } else {
                    response.setRenderParameter("view", "pickInfoResource");
                }
            } else {
                state.setInvalid(true);
                response.setRenderParameter("view", "view");
            }
        } else {

            state.setCurrentProgress(PdlProgress.firstStep().nextStep());
            response.setRenderParameter("view", "pickInfoResource");
        }
    }

    private void shortcutDirectlyToViewer(ActionRequest request, ActionResponse response, Model model) throws IOException {
        for (InfoTypeState<InformationType> key : state.getCsReport().aggregatedSystems.value.keySet()) {
            toggleAllInfoTypes(key.getId());
        }
        goToSummary(model, request, response);
    }

    private boolean onlySameCareUnit(PdlUserState state) {

        TreeMap<InfoTypeState<InformationType>, ArrayList<SystemState<CareSystem>>> systems =
                state.getCsReport().aggregatedSystems.value;
        for (InfoTypeState<InformationType> key : systems.keySet()) {
            if (key.containsOtherUnits || key.containsOtherProviders) {
                return false;
            }
        }
        return true;
    }

    private String trimmedPatientId(String patientId) {
        return patientId.
                replace("-", "").
                replace(" ", "").
                trim();
    }

    private PdlReport fetchPdlReport(
            PdlContext ctx,
            WithOutcome<ArrayList<WithInfoType<CareSystem>>> careSystems,
            int establishRelationTimeUnits,
            RoundedTimeUnit establishRelationDuration) {
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
                "VGR Portal PDL Service. Patient finns tillgänglig sedan innan hos egen vårdenhet.",
                establishRelationTimeUnits,
                establishRelationDuration
            );
        }
        return newReport;
    }

    @ActionMapping("establishRelation")
    public void establishRelationship(
            ActionRequest request,
            ActionResponse response,
            Model model,
            @RequestParam boolean confirmed
    ) throws IOException {
        if (state.getCurrentProgress().equals(PdlProgress.firstStep())) {
            response.setRenderParameter("view", "view");
        } else if(confirmed) {
            PdlContext ctx = state.getCtx().value;

            LOGGER.trace(
                "Request to create relationship between employee {} and patient {}.",
                ctx.employeeHsaId,
                state.getPatient().patientId
            );

            PortletPreferences prefs = request.getPreferences();
            int timeUnits = Integer.parseInt(prefs.getValue("establishRelationDuration", "1"));
            RoundedTimeUnit duration = RoundedTimeUnit.valueOf(prefs.getValue("establishRelationTimeUnits", RoundedTimeUnit.NEAREST_DAY.toString()));

            PdlReport newReport = pdl.patientRelationship(
                ctx,
                state.getPdlReport(),
                state.getPatient().patientId,
                "VGR Portal PDL Service",
                timeUnits,
                duration
            );

            state.setPdlReport(newReport);

            PdlLogger.log(UserAction.ATTEST_RELATION, logRepo, state);

            if (state.getPdlReport().hasRelationship.value &&
                onlySameCareUnit(state))
            {
                shortcutDirectlyToViewer(request, response, model);
            } else {
                response.setRenderParameter("view", "pickInfoResource");
            }
        } else {
            response.setRenderParameter("view", "pickInfoResource");
        }
    }

    @ActionMapping("establishConsent")
    public void establishConsent(
            ActionRequest request,
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

            PortletPreferences prefs = request.getPreferences();
            int timeUnits = Integer.parseInt(prefs.getValue("establishConsentDuration", "7"));
            RoundedTimeUnit duration = RoundedTimeUnit.valueOf(prefs.getValue("establishConsentTimeUnits", RoundedTimeUnit.NEAREST_DAY.toString()));

            // FIXME 2013-10-21 : Magnus Andersson > Add possiblility to be represented by someone?
            state.setPdlReport(
                pdl.patientConsent(
                    ctx,
                    state.getPdlReport(),
                    state.getPatient().patientId,
                    "VGR Portal PDL Service",
                    timeUnits,
                    duration,
                    ( emergency ) ? PdlReport.ConsentType.Emergency : PdlReport.ConsentType.Consent
                )
            );

            if(emergency) {
                PdlLogger.log(UserAction.EMERGENCY_CONSENT, logRepo, state);
            } else {
                PdlLogger.log(UserAction.ATTEST_CONSENT, logRepo, state);
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
                    PdlLogger.log(UserAction.REVEAL_OTHER_UNITS, logRepo, state);
                } else if(sameProvider) {
                    PdlLogger.log(UserAction.REVEAL_OTHER_PROVIDER, logRepo, state);
                }

                response.setRenderParameter("view", "pickInfoResource");
            } else {
                state.setConsentInformationTypeId(id); // Save the id until after consent has been established
                response.setRenderParameter("view", "establishConsent");
            }
        }
    }

    @ActionMapping("toggleAllCheckboxes")
    public void toggleAllInfoResource(
            @RequestParam String id,
            ActionResponse response
    ) {
        if (state.getCurrentProgress().equals(PdlProgress.firstStep())) {
            response.setRenderParameter("view", "view");
        } else {

            toggleAllInfoTypes(id);

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
                    "Request to show blocked information categorized with information decorator with id {} for patient {}",
                    id,
                    state.getPatient().patientId
            );

            CareSystemsReport newCsReport =
                    state.getCsReport().showBlocksForInfoResource(id);

            state.setCsReport(newCsReport);

            PdlLogger.log(UserAction.REVEAL_BLOCKED, logRepo, state);

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

            toggleOneInfoType(id, confirmed, revokeEmergency);

            response.setRenderParameter("view", "pickInfoResource");
        }
    }

    private void toggleAllInfoTypes(String id) {
        for(InfoTypeState<InformationType> key: state.getCsReport().aggregatedSystems.value.keySet()) {
            if(key.id.equals(id)) {
                for(SystemState<CareSystem> system : state.getCsReport().aggregatedSystems.value.get(key)) {
                    boolean selectable =
                            !system.isSelected() &&
                                    !system.blocked &&
                                    (system.visibility == Visibility.SAME_CARE_UNIT ||
                                            state.getShouldBeVisible().get(system.visibility.toString()));

                    if(selectable) {
                        toggleOneInfoType(system.id, false, false);
                    }
                }
            }
        }
    }

    private void toggleOneInfoType(String id, boolean confirmed, boolean revokeEmergency) {
        CareSystemsReport newCsReport =
                state.getCsReport().toggleInformation(id, confirmed);

        state.setCsReport(newCsReport);

        if (revokeEmergency && confirmed) {
            PdlLogger.log(UserAction.EMERGENCY_PASS_BLOCKED, logRepo, state);
        } else if (!revokeEmergency && confirmed) {
            PdlLogger.log(UserAction.PASS_BLOCKED, logRepo, state);
        }
    }

    @ActionMapping("goToSummary")
    public void goToSummary(Model model,
                            ActionRequest request,
                            ActionResponse response) throws IOException {
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

            PdlLogger.log(UserAction.SUMMARY_CARE_SYSTEMS, logRepo, state);


            // patient change event
            QName qname = new QName("http://pdl.portalen.vgregion.se/events", "pctx.change");
            PatientEvent patientEvent = new PatientEvent(
                    new PdlTicket(
                            state.getPatient(),
                            sumReport.referencesList,
                            new UserContext(
                                    state.getCtx().value.employeeDisplayName,
                                    state.getCtx().value.employeeHsaId
                            )
                    ),
                    ""
            );

            response.setEvent(qname, patientEvent);
            model.addAttribute("careSystemUrls", this.careSystemUrls.getUrls());

            if(sumReport.careSystems.size() == 1) {
                CareSystemViewer first = sumReport.careSystems.keySet().iterator().next();

                Maybe<String> redirect = this.careSystemUrls.getUrlForSystem(first.systemKey);

                if(redirect.success) {
                    response.sendRedirect(redirect.value);
                } else {
                    response.setRenderParameter("view", "showSummary");
                }
            } else {
                response.setRenderParameter("view", "showSummary");
            }
        }
    }

    @RenderMapping(params = "edit=edit")
    public String admin(final ModelMap model) {
        return "admin";
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

    private WithOutcome<PdlContext> currentContext(PortletRequest request) {
        String hsaIdFromRequest = userUtil.getHsaIdFromRequestHeader(request);

        WithOutcome<String> hsaId;
        if (hsaIdFromRequest != null && !"".equals(hsaIdFromRequest)) {
            hsaId = WithOutcome.success(hsaIdFromRequest);
        } else {
            // Try looking it up in ldap
            String vgrId = userUtil.getUserId(request);
            hsaId = ldapService.getHsaIdByVgrId(vgrId);
        }

        if (hsaId.success) {
            return accessControl.getContextByEmployeeId(hsaId.value);
        } else {
            return hsaId.mapValue(new PdlContext("", "", new TreeMap<String, Assignment>()));
        }
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
