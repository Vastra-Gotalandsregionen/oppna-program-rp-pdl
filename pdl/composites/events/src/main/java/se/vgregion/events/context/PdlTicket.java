package se.vgregion.events.context;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

public class PdlTicket implements Serializable {

    private static final long serialVersionUID = -8430279685177330801L;

    public final Patient patient;
    public final UserContext userContext;
    public final Map<String, SourceReferences> references;

    public PdlTicket(
            Patient patient,
            Map<String, SourceReferences> references,
            UserContext userContext
    ) {
        this.patient = patient;
        this.userContext = userContext;
        this.references = Collections.unmodifiableMap(references);
    }

    public Patient getPatient() {
        return patient;
    }

    public UserContext getUserContext() {
        return userContext;
    }

    public Map<String, SourceReferences> getReferences() {
        return references;
    }

    @Override
    public String toString() {
        return "PdlTicket{" +
                "patient=" + patient +
                ", userContext=" + userContext +
                ", references=" + references +
                '}';
    }
}
