package se.vgregion.portal.controller.bfr;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import se.vgregion.events.context.PdlTicket;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class BfrState {

    private PdlTicket ticket;

    public BfrState() {
    }


    public PdlTicket getTicket() {
        return ticket;
    }

    public void setTicket(PdlTicket ticket) {
        this.ticket = ticket;
    }

    @Override
    public String toString() {
        return "BfrState{" +
                "ticket=" + ticket +
                '}';
    }
}
