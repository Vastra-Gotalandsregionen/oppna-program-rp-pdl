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
import se.vgregion.domain.pdl.PatientEngagement;
import se.vgregion.domain.pdl.PdlContext;
import se.vgregion.domain.pdl.PdlReport;
import se.vgregion.service.pdl.PatientEngagements;
import se.vgregion.service.pdl.PdlService;

import javax.portlet.ActionResponse;
import java.util.List;

@Controller
@RequestMapping(value = "VIEW")
public class PdlController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PdlController.class.getName());
    @Autowired
    private PatientEngagements lookup;
    @Autowired
    private PdlService pdl;

    @RenderMapping
    public String enterSearchPatient(final ModelMap model) {


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

        LOGGER.trace("Looking for patient {}.", ssn );
        List<PatientEngagement> engagements = lookup.forPatient(ssn
        );

        LOGGER.trace("Found {} engagements.", engagements.size());
        if( engagements.size() > 0) {
            PdlContext ctx = createContext(ssn, engagements);

            LOGGER.trace("Generating report with context {}.", ctx);
            PdlReport report = pdl.pdlReport(ctx);

            LOGGER.trace("Adding report to model {}.", report);
            model.addAttribute("report", report);
        }


        response.setRenderParameter("view","searchResult");
    }

    @RenderMapping(params = "view=searchResult")
    public String searchResult(final ModelMap model) {
        return "searchResult";
    }


    private PdlContext createContext(String patientId, List<PatientEngagement> engagements) {
        return new PdlContext(
                patientId,
                engagements,
                "careProviderHsaId",
                "careUnitHsaId",
                "employeeHsaId"
        );
    }
}
