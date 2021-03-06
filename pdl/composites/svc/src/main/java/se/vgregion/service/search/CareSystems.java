package se.vgregion.service.search;

import se.vgregion.domain.decorators.WithInfoType;
import se.vgregion.domain.decorators.WithOutcome;
import se.vgregion.domain.decorators.WithPatient;
import se.vgregion.domain.pdl.PdlContext;
import se.vgregion.domain.systems.CareSystem;
import se.vgregion.portal.bfr.infobroker.domain.InfobrokerPersonIdType;

import java.util.ArrayList;

public interface CareSystems {
    WithOutcome<WithPatient<ArrayList<WithInfoType<CareSystem>>>> byPatientId(PdlContext ctx, String patientId, InfobrokerPersonIdType patientIdType);
}
