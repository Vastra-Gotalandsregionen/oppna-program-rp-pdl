package se.vgregion.portal.controller.bfr;

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
import org.springframework.web.portlet.bind.annotation.EventMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import se.vgregion.domain.bfr.Referral;
import se.vgregion.domain.decorators.WithOutcome;
import se.vgregion.events.context.PatientEvent;
import se.vgregion.events.context.SourceReferences;
import se.vgregion.events.context.sources.radiology.RadiologySourceRefs;
import se.vgregion.service.bfr.RadiologySource;
import se.vgregion.service.bfr.ZeroFootPrintUrls;

import javax.portlet.ActionResponse;
import javax.portlet.Event;
import javax.portlet.EventRequest;
import javax.portlet.RenderRequest;
import java.util.ArrayList;
import java.util.Collections;

@Controller
@RequestMapping(value = "VIEW")
@SessionAttributes("state")
public class BfrController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BfrController.class.getName());

    @Autowired
    private BfrState state;

    @Autowired
    @Qualifier("BfrRadiologySource")
    RadiologySource radiologySource;

    @Autowired
    private ZeroFootPrintUrls zfpUrls;

    @ModelAttribute("state")
    public BfrState initState() {
        return state;
    }

    @RenderMapping
    public String enterBfr(RenderRequest request) {
        return "view";
    }

    @RenderMapping(params = "view=view")
    public String start(final ModelMap model) {
        return "view";
    }

    @ActionMapping("showReferral")
    public void showReferral(
            @RequestParam String requestId,
            ActionResponse response
    ) {
        if(state.getRefs().success) {
            RadiologySourceRefs ref = (RadiologySourceRefs) state.getTicket().value.getReferences().get(requestId);

            WithOutcome<Referral> referralDetails =
                    radiologySource.requestByBrokerId(ref.infoBrokerId);

            state.setCurrentReferral(
                    zfpUrls.addZfpUrls(
                        referralDetails,
                        state.getTicket().value.userContext.employeeHsaId
                    )
            );

            response.setRenderParameter("view", "view");
        }
    }

    @EventMapping("{http://pdl.portalen.vgregion.se/events}pctx.reset")
    public void resetListener(EventRequest request) {
        Event event = request.getEvent();
        PatientEvent patient = (PatientEvent) event.getValue();

        LOGGER.debug("Got Patient event change. {}", patient);

        state.setTicket(patient.ticket);
        state.setCurrentReferral(null);
    }

    @EventMapping("{http://pdl.portalen.vgregion.se/events}pctx.change")
    public void changeListener(EventRequest request) {
        Event event = request.getEvent();
        PatientEvent patientEvent = (PatientEvent) event.getValue();
        ArrayList<RadiologySourceRefs> filteredRefs = filterBfrReferences(patientEvent);
        LOGGER.debug("Got Patient event change. {}", filteredRefs);

        state.setRefs(filteredRefs);
        state.setTicket(patientEvent.ticket);
    }

    private ArrayList<RadiologySourceRefs> filterBfrReferences(PatientEvent patientEvent) {
        ArrayList<RadiologySourceRefs> filteredReferences = new ArrayList<RadiologySourceRefs>();

        for(String refKey : patientEvent.ticket.references.keySet()) {
            SourceReferences entry = patientEvent.ticket.references.get(refKey);

            if(entry.targetCareSystem().equals(RadiologySourceRefs.SYSTEM_ID) &&
               entry instanceof RadiologySourceRefs) {
                RadiologySourceRefs convEntry = (RadiologySourceRefs) entry;
                filteredReferences.add(convEntry);
            }
        }

        Collections.sort(filteredReferences, RadiologySourceRefs.dateDescComparator);
        return filteredReferences;
    }

}
