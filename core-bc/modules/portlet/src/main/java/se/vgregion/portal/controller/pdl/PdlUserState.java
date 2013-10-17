package se.vgregion.portal.controller.pdl;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import se.vgregion.domain.pdl.PatientWithEngagements;
import se.vgregion.domain.pdl.PdlContext;
import se.vgregion.domain.pdl.PdlReport;

/**
 * Using session scope is arguably an evil thing to do, but will have to do for now since I don't(?) have a flash scope.
 * This is the only mutable data structure.
 */
@Component
@Scope("session")
public class PdlUserState {
    PdlReport report;
    PatientWithEngagements pwe;
    PdlContext ctx;

    public PdlReport getReport() {
        return report;
    }

    public PatientWithEngagements getPwe() {
        return pwe;
    }

    public PdlContext getCtx() {
        return ctx;
    }

    @Override
    public String toString() {
        return "PdlUserState{" +
                "report=" + report +
                ", pwe=" + pwe +
                ", ctx=" + ctx +
                '}';
    }
}
