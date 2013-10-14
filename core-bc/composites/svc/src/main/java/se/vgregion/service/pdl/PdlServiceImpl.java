package se.vgregion.service.pdl;

import org.springframework.stereotype.Service;
import se.riv.ehr.blocking.accesscontrol.checkblocks.v3.rivtabp21.CheckBlocksResponderService;
import se.riv.ehr.patientconsent.accesscontrol.checkconsent.v1.rivtabp21.CheckConsentResponderService;
import se.riv.ehr.patientrelationship.accesscontrol.checkpatientrelation.v1.rivtabp21.CheckPatientRelationResponderService;
import se.vgregion.domain.pdl.PatientEngagement;
import se.vgregion.domain.pdl.PdlAssertion;
import se.vgregion.domain.pdl.PdlContext;
import se.vgregion.domain.pdl.PdlReport;

import javax.annotation.Resource;
import java.util.List;

@Service
public class PdlServiceImpl implements PdlService {

    @Resource(name = "blocksForPatient")
    private CheckBlocksResponderService blocksForPatient;
    @Resource(name = "consentForPatient")
    private CheckConsentResponderService consentForPatient;
    @Resource(name = "relationshipWithPatient")
    private CheckPatientRelationResponderService relationshipWithPatient;

    @Override
    public PdlReport pdlReport(final PdlContext ctx) {
        return Report.generateReport(ctx, blocksForPatient, consentForPatient, relationshipWithPatient);
    }

    @Override
    public PdlReport patientConsent(PdlContext ctx) {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public PdlReport patientRelationship(PdlContext ctx) {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public PdlReport unblockInformation(PdlContext ctx, String blockId, UnblockType unblockType, String unblockComment) {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public PdlAssertion chooseInformation(PdlContext ctx, PdlReport report, List<PatientEngagement> engagements) {
        throw new IllegalStateException("Not implemented");
    }
}
