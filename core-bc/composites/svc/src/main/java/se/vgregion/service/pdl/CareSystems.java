package se.vgregion.service.pdl;

import se.vgregion.domain.pdl.CareSystem;
import se.vgregion.domain.pdl.PdlContext;
import se.vgregion.domain.pdl.WithInfoType;

import java.util.List;

public interface CareSystems {
    List<WithInfoType<CareSystem>> byPatientId(PdlContext ctx, String patientId);
}
