package se.vgregion.service.sources;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.vgregion.domain.decorators.WithInfoType;
import se.vgregion.domain.decorators.WithOutcome;
import se.vgregion.domain.decorators.WithPatient;
import se.vgregion.domain.pdl.PdlContext;
import se.vgregion.domain.systems.CareSystem;
import se.vgregion.service.search.CareSystems;

import java.util.ArrayList;

@Service("CareSystemsProxy")
public class CareSystemsProxy implements CareSystems {

    @Autowired
    RadiologySource radiologySource;

    @Override
    public WithOutcome<WithPatient<ArrayList<WithInfoType<CareSystem>>>> byPatientId(PdlContext ctx, String patientId) {
        return radiologySource.byPatientId(ctx, patientId);
    }

}
