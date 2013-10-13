package se.vgregion.service.pdl;

import se.vgregion.domain.pdl.PdlContext;
import se.vgregion.domain.pdl.PdlReport;

public interface PdlService {

    public PdlReport pdlReport(PdlContext ctx);
}
