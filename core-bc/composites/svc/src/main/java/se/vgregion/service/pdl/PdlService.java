package se.vgregion.service.pdl;

import se.vgregion.domain.pdl.PatientEngagement;
import se.vgregion.domain.pdl.PdlAssertion;
import se.vgregion.domain.pdl.PdlContext;
import se.vgregion.domain.pdl.PdlReport;

import java.util.List;

public interface PdlService {
    enum UnblockType {
        CONSENT, EMERGENCY
    }

    PdlReport pdlReport(PdlContext ctx);

    PdlReport patientConsent(PdlContext ctx);

    PdlReport patientRelationship(PdlContext ctx);

    PdlReport unblockInformation(PdlContext ctx, String blockId, UnblockType unblockType, String unblockComment);

    PdlAssertion chooseInformation(PdlContext ctx, PdlReport report, List<PatientEngagement> engagements);
}
