package se.vgregion.service.search;

import se.vgregion.domain.decorators.WithInfoType;
import se.vgregion.domain.decorators.WithOutcome;
import se.vgregion.domain.decorators.WithPatient;
import se.vgregion.domain.pdl.CareSystem;
import se.vgregion.domain.pdl.PdlContext;

import java.util.ArrayList;

public interface CareSystems {
    WithOutcome<WithPatient<ArrayList<WithInfoType<CareSystem>>>> byPatientId(PdlContext ctx, String patientId);
}
