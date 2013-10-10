package se.vgregion.service.pdl;

import se.vgregion.pdl.domain.PdlContext;
import se.vgregion.pdl.domain.PdlReport;

public interface PdlService {

    public PdlReport pdlReport(PdlContext ctx);
}
