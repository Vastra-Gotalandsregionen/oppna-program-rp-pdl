package se.vgregion.portal.controller.pdl;

import se.vgregion.domain.pdl.CareSystem;
import se.vgregion.domain.pdl.PdlContext;
import se.vgregion.domain.pdl.decorators.WithInfoType;

import java.util.List;

public class AvailablePatient {

    private AvailablePatient() {
        // Utility class, no public constructor
    }

    public static boolean check(PdlContext ctx, List<WithInfoType<CareSystem>> careSystems) {
        for( WithInfoType<CareSystem> infoSystem : careSystems ) {
            CareSystem system = infoSystem.value;
            boolean sameProvider = system.getCareProviderHsaId().equals(ctx.getCareProviderHsaId());
            boolean sameCareUnit = system.getCareUnitHsaId().equals(ctx.getCareUnitHsaId());
            if(sameProvider && sameCareUnit) {
                return true;
            }
        }
        return false;
    }
}
