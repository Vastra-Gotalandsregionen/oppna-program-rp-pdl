package se.vgregion.portal.controller.bfr;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import se.vgregion.domain.bfr.Referral;
import se.vgregion.domain.decorators.Maybe;
import se.vgregion.domain.decorators.WithOutcome;
import se.vgregion.events.context.Patient;
import se.vgregion.events.context.PdlTicket;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class BfrState {

    private Maybe<PdlTicket> ticket;
    private WithOutcome<Referral> currentReferral;

    public BfrState() {
        this.ticket = Maybe.none();
        if(ticket.success) {
            Patient patient = ticket.value.patient;
        }
    }

    public Maybe<PdlTicket> getTicket() {
        return ticket;
    }

    public void setTicket(PdlTicket ticket) {
        this.ticket = Maybe.some(ticket);
    }

    @Override
    public String toString() {
        return "BfrState{" +
                "ticket=" + ticket +
                '}';
    }

    public void setCurrentReferral(WithOutcome<Referral> currentReferral) {
        this.currentReferral = currentReferral;
    }

    public WithOutcome<Referral> getCurrentReferral() {
        return currentReferral;
    }
}
