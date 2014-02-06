package se.vgregion.service.patient;

import org.springframework.stereotype.Service;
import se.vgregion.domain.pdl.Patient;
import se.vgregion.service.search.PatientRepository;

@Service
public class KivPatientRepository implements PatientRepository {
    @Override
    public Patient byPatientId(String patientId) {
        if(patientId.equals("196609095176"))
            return new Patient(patientId, "STEFAN HOLMBERG");
        else if(patientId.equals("195004125646"))
            return new Patient(patientId, "MONICA INGEGERD NIKLASSON");
        else if(patientId.equals("20090226D077"))
            return new Patient(patientId, "BFR TESTPATIENT");
        else if(patientId.equals("201010101010"))
            return new Patient(patientId, "TIAN TESTBERG");
        else if(patientId.equals("191212121212"))
            return new Patient(patientId, "TOLVAN TOLVANSSON");
        else
            return new Patient(patientId, "EWA KARLSSON");
    }
}
