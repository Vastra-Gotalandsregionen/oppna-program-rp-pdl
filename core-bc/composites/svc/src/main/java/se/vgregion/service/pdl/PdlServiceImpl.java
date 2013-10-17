package se.vgregion.service.pdl;

import org.springframework.stereotype.Service;
import se.riv.ehr.blocking.accesscontrol.checkblocks.v3.rivtabp21.CheckBlocksResponderInterface;
import se.riv.ehr.patientconsent.accesscontrol.checkconsent.v1.rivtabp21.CheckConsentResponderInterface;
import se.riv.ehr.patientrelationship.accesscontrol.checkpatientrelation.v1.rivtabp21.CheckPatientRelationResponderInterface;
import se.vgregion.domain.pdl.*;

import javax.annotation.Resource;
import java.util.List;

@Service
public class PdlServiceImpl implements PdlService {

    @Resource(name = "blocksForPatient")
    private CheckBlocksResponderInterface blocksForPatient;
    @Resource(name = "consentForPatient")
    private CheckConsentResponderInterface consentForPatient;
    @Resource(name = "relationshipWithPatient")
    private CheckPatientRelationResponderInterface relationshipWithPatient;

    @Override
    public PdlReport pdlReport(final PdlContext ctx, PatientWithEngagements patientEngagements) {
        return Report.generateReport(ctx, patientEngagements, blocksForPatient, consentForPatient, relationshipWithPatient);
    }

    @Override
    public PdlReport patientConsent(PdlContext ctx) {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public PdlReport patientRelationship(PdlContext ctx, PdlReport report, String patientId) {
        return report.withRelationship(true);
    }

    @Override
    public PdlReport unblockInformation(PdlContext ctx, String blockId, UnblockType unblockType, String unblockComment) {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public PdlAssertion chooseInformation(PdlContext ctx, PdlReport report, List<Engagement> engagements) {
        throw new IllegalStateException("Not implemented");
    }
}
