package se.vgregion.service.sources;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import se.vgregion.domain.decorators.WithInfoType;
import se.vgregion.domain.decorators.WithOutcome;
import se.vgregion.domain.decorators.WithPatient;
import se.vgregion.domain.pdl.PdlContext;
import se.vgregion.domain.systems.CareSystem;
import se.vgregion.portal.bfr.infobroker.domain.InfobrokerPersonIdType;
import se.vgregion.service.search.CareSystems;

import java.util.ArrayList;

@Service("CareSystemsProxy")
public class  CareSystemsProxy implements CareSystems {

    @Autowired
    @Qualifier("pdlRadiologySource")
    CareSystems radiologySource;

    @Override
    public WithOutcome<WithPatient<ArrayList<WithInfoType<CareSystem>>>> byPatientId(PdlContext ctx, String patientId, InfobrokerPersonIdType patientIdType) {
        return Deduplication.remapDeduplicate(radiologySource.byPatientId(ctx, patientId, patientIdType));
    }


}
