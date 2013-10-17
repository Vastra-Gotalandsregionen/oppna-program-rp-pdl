package se.vgregion.domain.pdl;

import java.util.Collections;
import java.util.List;

public class PatientWithEngagements {

    public final String patientId;
    public final List<Engagement> engagements;
    public final String patientDisplayName;

    public PatientWithEngagements(String patientId, String patientDisplayName, List<Engagement> engagements) {
        this.patientId = patientId;
        this.engagements = Collections.unmodifiableList(engagements); // Immutable
        this.patientDisplayName = patientDisplayName;
    }

    public String getPatientId() {
        return patientId;
    }

    public List<Engagement> getEngagements() {
        return engagements;
    }

    public String getPatientDisplayName() {
        return patientDisplayName;
    }

    @Override
    public String toString() {
        return "PatientWithEngagements{" +
                "patientId='" + patientId + '\'' +
                ", engagements=" + engagements +
                ", patientDisplayName='" + patientDisplayName + '\'' +
                '}';
    }
}
