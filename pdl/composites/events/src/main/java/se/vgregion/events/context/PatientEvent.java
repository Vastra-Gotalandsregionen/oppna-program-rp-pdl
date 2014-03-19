package se.vgregion.events.context;

import java.io.Serializable;
import java.util.Map;

public class PatientEvent implements Serializable {

    private static final long serialVersionUID = 7453515479251370720L;

    public final PdlTicket ticket;
    public final String signedPdlXmlTicket;

    public PatientEvent(PdlTicket ticket, String signedPdlXmlTicket) {
        this.ticket = ticket;
        this.signedPdlXmlTicket = signedPdlXmlTicket;
    }

    public PdlTicket getTicket() {
        return ticket;
    }

    public String getSignedPdlXmlTicket() {
        return signedPdlXmlTicket;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PatientEvent)) return false;

        PatientEvent that = (PatientEvent) o;

        if (!signedPdlXmlTicket.equals(that.signedPdlXmlTicket)) return false;
        if (!ticket.equals(that.ticket)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = ticket.hashCode();
        result = 31 * result + signedPdlXmlTicket.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "PatientEvent{" +
                "ticket=" + ticket +
                ", signedPdlXmlTicket='" + signedPdlXmlTicket + '\'' +
                '}';
    }

    public PatientEvent mapSourceReferences(Map<String, SourceReferences> filteredReferences) {
            return new PatientEvent(ticket.mapSourceReferences(filteredReferences), signedPdlXmlTicket);
    }
}
