package se.vgregion.service.pdl;

import se.vgregion.domain.pdl.PatientEngagement;

import java.util.List;

public interface PatientEngagements {

    List<PatientEngagement> forPatient(String ssn);
}
