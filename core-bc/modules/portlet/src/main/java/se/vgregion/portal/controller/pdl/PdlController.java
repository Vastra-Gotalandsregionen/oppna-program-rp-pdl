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
import se.vgregion.service.pdl.CareSystems;
import se.vgregion.service.pdl.PatientEngagements;
import se.vgregion.service.pdl.PdlService;

import javax.portlet.ActionResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "VIEW")
@SessionAttributes("state")
public class PdlController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PdlController.class.getName());
    @Autowired
    private PatientEngagements patientEngagements;
    @Autowired
    private PdlService pdl;
    @Autowired
    private PdlUserState state;
    @Autowired
    private CareSystems systems;

    private static List<Engagement.InformationType> asInformationTypes(List<Engagement> engagements) {
        ArrayList<Engagement.InformationType> is = new ArrayList<Engagement.InformationType>();
        for (Engagement e : engagements) {
            is.add(e.informationType);
        }
        return is;
    }

    @ModelAttribute("state")
    public PdlUserState initState() {
        if (state.getCtx() == null) {
            state.setCtx(currentContext());
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

    @ActionMapping("searchPatient")
    public void searchPatientInformation(
            @RequestParam String ssn,
            ActionResponse response
    ) {
        state.reset(); // Make sure reset is called here when user uses back button and submits again.

        LOGGER.trace("Looking for patient {}.", ssn);
        PatientWithEngagements pwe = patientEngagements.forPatient(ssn);
        state.setPwe(pwe);

        PdlReport pdlReport = pdl.pdlReport(state.getCtx(), pwe);

        // Engagements belonging to patient.
        List<Engagement.InformationType> information = asInformationTypes(pwe.engagements);
        List<CareSystem> careSystems = systems.byInformationType(information);

        CareSystemsReport csReport = new CareSystemsReport(state.getCtx(), pdlReport, careSystems);

        state.setPdlReport(pdlReport);
        state.setCsReport(csReport);

        response.setRenderParameter("view", "searchResult");
    }

    @ActionMapping("establishRelationship")
    public void establishRelationship(ActionResponse response) {
        LOGGER.trace(
                "Request to create relationship between employee {} and patient {}.",
                state.getCtx().employeeHsaId,
                state.getPwe().patientId
        );

        PdlReport newReport = pdl.patientRelationship(
                state.getCtx(),
                state.getPdlReport(),
                state.getPwe().patientId,
                "Reason",
                1,
                RoundedTimeUnit.NEAREST_HALF_HOUR
        );

        state.setPdlReport(newReport);

        response.setRenderParameter("view", "searchResult");
    }

    @ActionMapping("establishConsent")
    public void establishConsent(ActionResponse response) {
        LOGGER.trace(
                "Request to create consent between employee {} and patient {}.",
                state.getCtx().employeeHsaId,
                state.getPwe().patientId
        );

        // FIXME 2013-10-21 : Magnus Andersson > Should choose between consent or emergency. Also add possiblility to be represented by someone?
        state.setPdlReport(
                pdl.patientConsent(
                        state.getCtx(),
                        state.getPdlReport(),
                        state.getPwe().patientId,
                        "Reason",
                        1,
                        RoundedTimeUnit.NEAREST_HALF_HOUR,
                        PdlReport.ConsentType.Consent
                )
        );

        response.setRenderParameter("view", "searchResult");
    }



    @ActionMapping("sameCareProvider")
    public void sameCareProvider(ActionResponse response) {
        LOGGER.trace(
                "Request to show more information within same care giver for employee {} and patient {}.",
                state.getCtx().employeeHsaId,
                state.getPwe().patientId
        );

        // LOG SERVICE CALL

        state.setShowSameCareProvider(true);

        response.setRenderParameter("view", "searchResult");
    }

    @ActionMapping("otherCareProvider")
    public void otherProvider(ActionResponse response) {
        LOGGER.trace(
                "Request to show more information within same care giver for employee {} and patient {}.",
                state.getCtx().employeeHsaId,
                state.getPwe().patientId
        );

        // LOG SERVICE CALL

        state.setShowOtherCareProvider(true);

        response.setRenderParameter("view", "searchResult");
    }



    @RenderMapping(params = "view=searchResult")
    public String searchResult(final ModelMap model) {
        return "searchResult";
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
