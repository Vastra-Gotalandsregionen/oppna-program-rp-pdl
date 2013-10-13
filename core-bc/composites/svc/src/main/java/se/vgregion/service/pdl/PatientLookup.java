package se.vgregion.service.pdl;

import se.vgregion.domain.pdl.PatientEngagement;

import java.util.List;

public interface PatientLookup {

    List<PatientEngagement> findPatient(String s);
}
