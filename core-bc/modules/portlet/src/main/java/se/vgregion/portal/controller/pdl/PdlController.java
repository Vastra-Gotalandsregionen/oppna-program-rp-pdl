package se.vgregion.portal.controller.pdl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import se.vgregion.domain.pdl.PatientWithEngagements;
import se.vgregion.domain.pdl.PdlContext;
import se.vgregion.domain.pdl.PdlReport;
import se.vgregion.service.pdl.PatientEngagements;
import se.vgregion.service.pdl.PdlService;

import javax.portlet.ActionResponse;

@Controller
@RequestMapping(value = "VIEW")
public class PdlController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PdlController.class.getName());
    @Autowired
    private PatientEngagements patientEngagements;
    @Autowired
    private PdlService pdl;

    @RenderMapping
    public String enterSearchPatient(final ModelMap model) {
        PdlContext ctx = createContext();
        model.addAttribute("assignment", "Sammanhållen Journalföring");

        return "view";
    }

    // Matches personnummer: 900313-1245, 990313+1245 (100yrs+)
    // Matches samordningsnummer: 910572-3453
    // Will not verify last control sum number
    //private final static String personSamordningsNummerRegex =
    //        "[0-9]{2}(0[0-9]|1[0-2])(0[0-9]|1[0-9]|2[0-9]|3[0-1]|6[0-9]|7[0-9]|8[0-9]|9[0-1])[-+][0-9]{4}";

    //@RequestMapping(value = "/searchPatient/{ssn:"+personSamordningsNummerRegex+"}", method = RequestMethod.GET)
    //@PathVariable("ssn") String ssn

    @ActionMapping("searchPatient")
    public void searchPatientInformation(
            @RequestParam String ssn,
            ModelMap model,
            ActionResponse response
    ) {
        // TODO: 2013-10-15: Magnus Andersson > Validate ssn. Existing libs available?

        LOGGER.trace("Looking for patient {}.", ssn);
        PatientWithEngagements pwe = patientEngagements.forPatient(ssn);

        PdlContext ctx = createContext(); // FIXME: 2013-10-16: Magnus Andersson > This should come from ENV.

        LOGGER.trace("Found {} engagements.", pwe.engagements.size());
        if( pwe.engagements.size() > 0) {

            LOGGER.trace("Generating report with context {} and patient with engagements {}.", ctx, pwe);
            PdlReport report = pdl.pdlReport(ctx, pwe);

            LOGGER.trace("Adding PatientWithEngagement to model {}.", pwe);
            model.addAttribute("patientWithEngagement", pwe);

            LOGGER.trace("Adding report to model {}.", report);
            model.addAttribute("report", report);
        }

        response.setRenderParameter("view","searchResult");
    }

    @RenderMapping(params = "view=searchResult")
    public String searchResult(final ModelMap model) {
        return "searchResult";
    }


    private PdlContext createContext() {
        return new PdlContext(
                "careProviderHsaId",
                "careUnitHsaId",
                "employeeHsaId"
        );
    }
}
