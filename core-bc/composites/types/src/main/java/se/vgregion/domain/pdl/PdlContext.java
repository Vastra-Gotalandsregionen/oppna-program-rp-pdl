package se.vgregion.domain.pdl;

import java.util.Collections;
import java.util.List;

public class PdlContext {

    public final String patientId;
    public final List<PatientEngagement> engagements;
    public final String careProviderHsaId;
    public final String careUnitHsaId;
    public final String employeeHsaId;

    public PdlContext(String patientId, List<PatientEngagement> engagements, String careProviderHsaId, String careUnitHsaId, String employeeHsaId) {
        this.patientId = patientId;
        this.engagements = Collections.unmodifiableList(engagements); // Immutable
        this.careProviderHsaId = careProviderHsaId;
        this.careUnitHsaId = careUnitHsaId;
        this.employeeHsaId = employeeHsaId;
    }
}
