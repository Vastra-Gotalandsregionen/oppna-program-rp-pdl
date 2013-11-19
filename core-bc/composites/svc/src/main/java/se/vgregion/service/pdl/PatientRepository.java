package se.vgregion.service.pdl;


import se.vgregion.domain.pdl.Patient;

public interface PatientRepository {
    Patient byPatientId(String patientId);
}
