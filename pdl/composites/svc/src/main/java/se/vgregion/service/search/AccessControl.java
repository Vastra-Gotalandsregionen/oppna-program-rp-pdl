package se.vgregion.service.search;

import se.vgregion.domain.pdl.PdlContext;
import se.vgregion.domain.decorators.WithOutcome;

public interface AccessControl {
    public WithOutcome<PdlContext> getContextByEmployeeId(String hsaId);
}
