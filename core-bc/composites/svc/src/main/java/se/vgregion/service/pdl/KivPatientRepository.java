package se.vgregion.service.pdl;

import org.springframework.stereotype.Service;
import se.vgregion.domain.pdl.Patient;

@Service
public class KivPatientRepository implements PatientRepository{
    @Override
    public Patient byPatientId(String patientId) {
        return new Patient(patientId, "Ewa Karlsson");
    }
}
