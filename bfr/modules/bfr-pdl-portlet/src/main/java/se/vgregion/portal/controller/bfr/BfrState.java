package se.vgregion.portal.controller.bfr;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import se.vgregion.domain.bfr.Referral;
import se.vgregion.domain.decorators.Maybe;
import se.vgregion.domain.decorators.WithOutcome;
import se.vgregion.events.context.PdlTicket;
import se.vgregion.events.context.sources.radiology.RadiologySourceRefs;

import java.io.Serializable;
import java.util.ArrayList;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class BfrState implements Serializable {
    private static final long serialVersionUID = 6000047137847271688L;

    private Maybe<ArrayList<RadiologySourceRefs>> refs;
    private WithOutcome<Referral> currentReferral;
    private Maybe<PdlTicket> ticket;

    public BfrState() {
        this.refs = Maybe.none();
    }

    public Maybe<ArrayList<RadiologySourceRefs>> getRefs() {
        return refs;
    }

    public void setRefs(ArrayList<RadiologySourceRefs> refs) {
        this.refs = Maybe.some(refs);
    }

    public void setCurrentReferral(WithOutcome<Referral> currentReferral) {
        this.currentReferral = currentReferral;
    }

    public WithOutcome<Referral> getCurrentReferral() {
        return currentReferral;
    }

    public void setTicket(PdlTicket ticket) {
        this.ticket = Maybe.some(ticket);
    }

    public Maybe<PdlTicket> getTicket() {
        return ticket;
    }

    @Override
    public String toString() {
        return "BfrState{" +
                "refs=" + refs +
                ", currentReferral=" + currentReferral +
                ", ticket=" + ticket +
                '}';
    }
}
