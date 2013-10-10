package se.vgregion.service.pdl;

import se.vgregion.pdl.domain.InformationType;
import se.vgregion.pdl.domain.PatientEngagement;

import java.util.Arrays;
import java.util.List;

public class PatientLookupImpl implements PatientLookup {
    @Override
    public List<PatientEngagement> findPatient(String s) {
        return Arrays.asList(new PatientEngagement("careProviderHsaId", "careUnitHsaId", "employeeHsaId", InformationType.OTHR));
    }
}
