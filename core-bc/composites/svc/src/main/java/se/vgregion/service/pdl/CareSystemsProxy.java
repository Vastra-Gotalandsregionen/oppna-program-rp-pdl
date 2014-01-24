package se.vgregion.service.pdl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.vgregion.domain.decorators.WithInfoType;
import se.vgregion.domain.decorators.WithOutcome;
import se.vgregion.domain.pdl.CareSystem;
import se.vgregion.domain.pdl.PdlContext;
import se.vgregion.service.pdl.sources.RadiologySource;

import java.util.ArrayList;

@Service("CareSystemsProxy")
public class CareSystemsProxy implements CareSystems {

    @Autowired
    RadiologySource radiologySource;

    @Override
    public WithOutcome<ArrayList<WithInfoType<CareSystem>>> byPatientId(PdlContext ctx, String patientId) {
        return radiologySource.byPatientId(ctx, patientId);
    }
}
