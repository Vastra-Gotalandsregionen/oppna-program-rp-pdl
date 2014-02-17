package se.vgregion.events.context;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class PdlTicket implements Serializable {

    private static final long serialVersionUID = -8430279685177330801L;

    public final Patient patient;

    /*
    * Since the original list must be an array list the resulting unmodifiable list is also serializable
    * For more info see: http://docs.oracle.com/javase/7/docs/api/java/util/Collections.html#unmodifiableList(java.util.List)
    * 2014-02-13 : Magnus Andersson
    */
    @SuppressWarnings("serial")
    public final List<SourceReferences> references;

    public PdlTicket(Patient patient, List<SourceReferences> references) {
        this.patient = patient;
        this.references = Collections.unmodifiableList(references);
    }

    public Patient getPatient() {
        return patient;
    }

    public List<SourceReferences> getReferences() {
        return references;
    }

    @Override
    public String toString() {
        return "PdlTicket{" +
                "patient='" + patient + '\'' +
                ", references=" + references +
                '}';
    }
}
