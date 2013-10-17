package se.vgregion.service.pdl;

import se.vgregion.domain.pdl.PatientWithEngagements;

public interface PatientEngagements {

    PatientWithEngagements forPatient(String ssn);
}
