package se.vgregion.portal.controller.pdl.production;

import com.liferay.portal.util.Portal;
import com.liferay.portal.util.PortalUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import se.vgregion.domain.decorators.*;
import se.vgregion.domain.pdl.PdlContext;
import se.vgregion.portal.controller.pdl.PdlControllerBase;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
@RequestMapping(value = "VIEW")
@SessionAttributes({"state", "sessionPatientId"})
public class PdlController extends PdlControllerBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(PdlController.class.getName());

    private Portal portal;
    private static final String SESSION_PATIENT_ID = "SESSION_PATIENT_ID";

    public PdlController() {
        this.portal = PortalUtil.getPortal();
    }

    @ModelAttribute("sessionPatientId")
    public String getSessionPatientId(PortletRequest request, Model model) {
        String patientId = request.getParameter("patientId");
        if (patientId != null) {
            // PatientId is only allowed to be set with POST requests.
            validatePostRequest(request, model);

            model.addAttribute(SESSION_PATIENT_ID, patientId);
        }
        return patientId;
    }

    @RenderMapping
    public String enterSearchPatient(PortletRequest request, Model model) {
        state.reset(); // Make sure state is reset when user navigates to the start page.
        if (state.getCtx() == null) {
            WithOutcome<PdlContext> ctx = currentContext(request);
            state.setCtx(ctx);
        }

        String patientId = request.getParameter("patientId");
        if (patientId != null) {
            // PatientId is only allowed to be set with POST requests.
            validatePostRequest(request, model);

            model.addAttribute("sessionPatientId", patientId);
        }

        return "view";
    }

    @ActionMapping("searchPatient")
    public void searchPatientInformation(
            @RequestParam String patientIdType,
            @RequestParam String currentAssignment,
            @RequestParam boolean reset,
            ActionRequest request,
            ActionResponse response,
            Model model
    ) throws IOException {
        String patientId = (String) model.asMap().get("SESSION_PATIENT_ID");
        searchPatientInformationCommon(patientIdType, currentAssignment, reset, request, response, model, patientId);
    }

    private void validatePostRequest(PortletRequest request, Model model) {
        // Some validation
        HttpServletRequest httpServletRequest = portal.getHttpServletRequest(request);
        if (!httpServletRequest.getMethod().equalsIgnoreCase("POST")) {
            String logMessage = "Only POST requests are allowed when patientIt is sent.";
            LOGGER.error(logMessage);
            model.addAttribute("errorMessage", "Anropet var inte korrekt eller så har ett tekniskt fel uppstått.");
            throw new IllegalArgumentException(logMessage);
        }
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception exception, PortletRequest request) {

        LOGGER.error(exception.getMessage(), exception);

        if (request.getPortletSession() != null && request.getPortletSession().getAttribute(SESSION_PATIENT_ID) == null) {
            request.setAttribute("errorMessage", "Patientens personid saknas. Denna sida ska du komma till via uthopp från ditt journalsystem.");
        }

        return "errorPage";
    }

}
