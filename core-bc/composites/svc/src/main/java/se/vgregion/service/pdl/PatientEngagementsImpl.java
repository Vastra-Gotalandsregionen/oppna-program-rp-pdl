package se.vgregion.service.pdl;

import org.springframework.stereotype.Service;
import se.vgregion.domain.pdl.Engagement;
import se.vgregion.domain.pdl.PatientWithEngagements;

import java.util.Arrays;
import java.util.Collections;

@Service
public class PatientEngagementsImpl implements PatientEngagements {
    @Override
    public PatientWithEngagements forPatient(String ssn) {
        return new PatientWithEngagements(
                ssn,
                "Kalle Karlsson",
                Collections.unmodifiableList(
                        Arrays.asList(
                                new Engagement(
                                        "careProviderHsaId",
                                        "careUnitHsaId",
                                        "employeeHsaId",
                                        Engagement.InformationType.UPP
                                ),
                                new Engagement(
                                        "careProviderHsaId",
                                        "careUnitHsaId",
                                        "employeeHsaId",
                                        Engagement.InformationType.LAK
                                )
                        )
                )
        );
    }
}
