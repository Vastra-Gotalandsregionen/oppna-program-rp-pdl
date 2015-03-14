package se.vgregion.portal.controller.pdl.admin;

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
import se.vgregion.portal.controller.pdl.AvailablePatient;
import se.vgregion.portal.controller.pdl.PdlControllerBase;
import se.vgregion.portal.controller.pdl.production.PdlController;
import se.vgregion.portal.controller.pdl.PdlLogger;
import se.vgregion.portal.controller.pdl.PdlProgress;
import se.vgregion.portal.controller.pdl.common.PdlUserState;
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
public class PdlAdminController extends PdlControllerBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(PdlController.class.getName());

    @Autowired
    @Qualifier("MockAccessControl")
    protected AccessControl accessControl;

    @RenderMapping
    public String enterSearchPatient(PortletRequest request) {
        state.reset(); // Make sure state is reset when user navigates to the start page.
        if (state.getCtx() == null) {
            WithOutcome<PdlContext> ctx = currentContext(request);
            state.setCtx(ctx);
        }
        return "admin/view";
    }

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
        searchPatientInformationCommon(patientIdType, currentAssignment, reset, request, response, model, patientId);
    }

    @Override
    protected AccessControl getAccessControl() {
        return accessControl;
    }
}
