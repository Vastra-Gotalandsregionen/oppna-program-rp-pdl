package se.vgregion.portal.controller.pdl;

import se.vgregion.domain.pdl.CareSystem;
import se.vgregion.domain.pdl.PdlContext;
import se.vgregion.domain.pdl.Visibility;
import se.vgregion.domain.assignment.Assignment;
import se.vgregion.domain.decorators.WithInfoType;

import java.util.List;

public class AvailablePatient {

    private AvailablePatient() {
        // Utility class, no public constructor
    }

    public static boolean check(PdlContext ctx, List<WithInfoType<CareSystem>> careSystems) {
        Assignment currentAssignment = ctx.assignments.get(ctx.currentAssignment);
        for( WithInfoType<CareSystem> infoSystem : careSystems ) {
            CareSystem system = infoSystem.value;
            Visibility systemVisibility = system.getVisibilityFor(ctx.assignments.get(ctx.currentAssignment));
            if(systemVisibility == Visibility.SAME_CARE_UNIT) {
                return true;
            }
        }
        return false;
    }
}
