package se.vgregion.service.pdl;

import se.vgregion.domain.pdl.Patient;

public class KivPatientRepository implements PatientRepository{
    @Override
    public Patient byPatientId(String patientId) {
        return new Patient(patientId, "Ewa Karlsson");
    }
}
