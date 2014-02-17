package se.vgregion.portal.controller.bfr;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import se.vgregion.domain.decorators.Maybe;
import se.vgregion.events.context.PdlTicket;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class BfrState {

    private Maybe<PdlTicket> ticket;

    public BfrState() {
        this.ticket = Maybe.none();
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
}
