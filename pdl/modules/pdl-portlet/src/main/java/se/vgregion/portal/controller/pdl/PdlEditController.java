package se.vgregion.portal.controller.pdl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import se.vgregion.domain.pdl.RoundedTimeUnit;

import javax.portlet.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping(value = "EDIT")
public class PdlEditController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PdlEditController.class);

    @ModelAttribute(value = "roundedTimeUnitList")
    public List<RoundedTimeUnit> getroundedTimeUnitList() {
        return Arrays.asList(RoundedTimeUnit.values());
    }

    @RenderMapping
    public String edit(Model model, RenderRequest request, RenderResponse response) {

        PortletPreferences prefs = request.getPreferences();
        String establishRelationDuration = prefs.getValue("establishRelationDuration", "1");
        String establishRelationTimeUnit = prefs.getValue("establishRelationTimeUnit", RoundedTimeUnit.NEAREST_DAY.toString());

        model.addAttribute("establishRelationDuration", establishRelationDuration);
        model.addAttribute("establishRelationTimeUnit", establishRelationTimeUnit);

        String establishConsentDuration = prefs.getValue("establishConsentDuration", "7");
        String establishConsentTimeUnit = prefs.getValue("establishConsentTimeUnit", RoundedTimeUnit.NEAREST_DAY.toString());

        model.addAttribute("establishConsentDuration", establishConsentDuration);
        model.addAttribute("establishConsentTimeUnit", establishConsentTimeUnit);

        return "edit";
    }

    @ActionMapping(params = "action=save")
    public void savePreferences(ActionRequest request,
                                @RequestParam("establishRelationDuration") String establishRelationDuration,
                                @RequestParam("establishRelationTimeUnit") String establishRelationTimeUnit,
                                @RequestParam("establishConsentDuration") String establishConsentDuration,
                                @RequestParam("establishConsentTimeUnit") String establishConsentTimeUnit) {
        try {
            PortletPreferences prefs = request.getPreferences();

            prefs.setValue("establishRelationDuration", establishRelationDuration);
            prefs.setValue("establishRelationTimeUnit", establishRelationTimeUnit);

            prefs.setValue("establishConsentDuration", establishConsentDuration);
            prefs.setValue("establishConsentTimeUnit", establishConsentTimeUnit);

            prefs.store();
        } catch (ReadOnlyException e) {
            LOGGER.error("could not store tags in more like this edit mode.", e);
        } catch (ValidatorException e) {
            LOGGER.error("could not store tags in more like this edit mode.", e);
        } catch (IOException e) {
            LOGGER.error("could not store tags in more like this edit mode.", e);
        }
    }

}
