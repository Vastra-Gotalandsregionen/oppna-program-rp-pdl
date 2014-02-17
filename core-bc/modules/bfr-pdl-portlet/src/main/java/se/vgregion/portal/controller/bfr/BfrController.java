package se.vgregion.portal.controller.bfr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.portlet.bind.annotation.EventMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import se.vgregion.events.context.PatientEvent;
import se.vgregion.service.search.CareSystems;

import javax.portlet.Event;
import javax.portlet.EventRequest;
import javax.portlet.RenderRequest;

@Controller
@RequestMapping(value = "VIEW")
@SessionAttributes("state")
public class BfrController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BfrController.class.getName());

    @Autowired
    @Qualifier("CareSystemsProxy")
    private CareSystems systems;

    @Autowired
    private BfrState state;

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

    @EventMapping("{http://pdl.portalen.vgregion.se/events}pctx.change")
    public void changeListener(EventRequest request) {
        Event event = request.getEvent();
        PatientEvent patient = (PatientEvent) event.getValue();

        LOGGER.debug("Got Patient event change. {}", patient);

        state.setTicket(patient.ticket);
    }

}
