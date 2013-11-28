package se.vgregion.service.pdl;

import se.vgregion.domain.pdl.PdlContext;
import se.vgregion.domain.pdl.decorators.WithAccess;

public interface AccessControl {

    /**
     * Determines if this user's assignment allows the user to see information from other care providers.
     * @param ctx The PdlContext with the current user
     * @return PdlContext decorated with authorization information.
     */
    WithAccess<PdlContext> authorize(PdlContext ctx);
}
