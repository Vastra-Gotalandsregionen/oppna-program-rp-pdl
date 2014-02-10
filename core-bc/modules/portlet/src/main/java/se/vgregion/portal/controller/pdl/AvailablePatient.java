package se.vgregion.portal.controller.pdl;

import se.vgregion.domain.decorators.WithInfoType;
import se.vgregion.domain.systems.CareSystem;
import se.vgregion.domain.pdl.PdlContext;
import se.vgregion.domain.systems.Visibility;

import java.util.List;

public class AvailablePatient {

    private AvailablePatient() {
        // Utility class, no public constructor
    }

    public static boolean check(PdlContext ctx, List<WithInfoType<CareSystem>> careSystems) {
        for( WithInfoType<CareSystem> infoSystem : careSystems ) {
            CareSystem system = infoSystem.value;
            Visibility systemVisibility = ctx.currentAssignment.visibilityFor(system);
            if(systemVisibility == Visibility.SAME_CARE_UNIT) {
                return true;
            }
        }
        return false;
    }
}
