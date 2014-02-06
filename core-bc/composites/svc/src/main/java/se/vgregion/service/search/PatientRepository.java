package se.vgregion.service.search;


import se.vgregion.domain.pdl.Patient;

public interface PatientRepository {
    Patient byPatientId(String patientId);
}
