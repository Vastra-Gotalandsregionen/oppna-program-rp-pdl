package se.vgregion.service.pdl;

import se.vgregion.domain.decorators.WithInfoType;
import se.vgregion.domain.decorators.WithOutcome;
import se.vgregion.domain.pdl.CareSystem;
import se.vgregion.domain.pdl.PdlContext;

import java.util.ArrayList;

public interface CareSystems {
    WithOutcome<ArrayList<WithInfoType<CareSystem>>> byPatientId(PdlContext ctx, String patientId);
}
