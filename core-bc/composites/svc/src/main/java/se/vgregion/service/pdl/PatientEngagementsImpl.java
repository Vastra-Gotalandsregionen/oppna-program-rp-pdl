package se.vgregion.service.pdl;

import org.springframework.stereotype.Service;
import se.vgregion.domain.pdl.PatientEngagement;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class PatientEngagementsImpl implements PatientEngagements {
    @Override
    public List<PatientEngagement> forPatient(String ssn) {
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
