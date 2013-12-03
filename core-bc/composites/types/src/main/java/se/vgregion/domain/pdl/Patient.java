package se.vgregion.domain.pdl;

import java.io.Serializable;

public class Patient implements Serializable {
    private static final long serialVersionUID = -3164297652632714945L;

    public final String patientId;
    public final String patientDisplayName;

    public Patient(String patientId, String patientDisplayName) {
        this.patientId = patientId;
        this.patientDisplayName = patientDisplayName;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getPatientDisplayName() {
        return patientDisplayName;
    }

    // Format 193404231234 into 19340423-1234
    public String getPatientIdFormatted() {
        return new StringBuilder(patientId).insert(patientId.length()-4, "-").toString();
    }

    @Override
    public String toString() {
        return "Patient{" +
                "patientId='" + patientId + '\'' +
                ", patientDisplayName='" + patientDisplayName + '\'' +
                '}';
    }
}
