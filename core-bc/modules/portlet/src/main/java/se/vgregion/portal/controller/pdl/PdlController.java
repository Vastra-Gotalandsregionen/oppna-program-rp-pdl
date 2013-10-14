package se.vgregion.portal.controller.pdl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import se.vgregion.domain.pdl.PatientEngagement;
import se.vgregion.domain.pdl.PdlContext;
import se.vgregion.domain.pdl.PdlReport;
import se.vgregion.service.pdl.PatientLookup;
import se.vgregion.service.pdl.PdlService;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.util.List;

@Controller
@RequestMapping(value = "VIEW")
public class PdlController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PdlController.class.getName());

    private final PatientLookup lookup;
    private final PdlService pdl;

    @Autowired
    public PdlController(PatientLookup lookup, PdlService pdl) {
        this.lookup = lookup;
        this.pdl = pdl;
    }

    @RenderMapping()
    public String showIdea(RenderRequest request, RenderResponse response, final ModelMap model) {

        List<PatientEngagement> engagements = lookup.findPatient("APABEPA");
        PdlContext ctx = new PdlContext(
                "patientHsaId",
                engagements,
                "careProviderHsaId",
                "careUnitHsaId",
                "employeeHsaId"
        );

        PdlReport report = pdl.pdlReport(ctx);

        System.out.println("Report: " + report);

        return "view";
    }
}
