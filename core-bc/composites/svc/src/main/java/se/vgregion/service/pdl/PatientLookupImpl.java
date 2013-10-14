package se.vgregion.service.pdl;

import org.springframework.stereotype.Service;
import se.vgregion.domain.pdl.PatientEngagement;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class PatientLookupImpl implements PatientLookup {
    @Override
    public List<PatientEngagement> findPatient(String s) {
        return Collections.unmodifiableList(
                Arrays.asList(
                        new PatientEngagement(
                                "careProviderHsaId",
                                "careUnitHsaId",
                                "employeeHsaId",
                                PatientEngagement.InformationType.OTHR
                        )
                )
        );
    }
}
