package se.vgregion.service.pdl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.riv.ehr.blocking.accesscontrol.checkblocks.v2.rivtabp21.CheckBlocksResponderInterface;
import se.riv.ehr.patientconsent.accesscontrol.checkconsent.v1.rivtabp21.CheckConsentResponderInterface;
import se.riv.ehr.patientconsent.administration.registerextendedconsent.v1.rivtabp21.RegisterExtendedConsentResponderInterface;
import se.riv.ehr.patientrelationship.accesscontrol.checkpatientrelation.v1.rivtabp21.CheckPatientRelationResponderInterface;
import se.riv.ehr.patientrelationship.administration.registerextendedpatientrelation.v1.rivtabp21.RegisterExtendedPatientRelationResponderInterface;
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
    @Resource(name = "establishRelationship")
    private RegisterExtendedPatientRelationResponderInterface establishRelationship;
    @Resource(name = "establishConsent")
    private RegisterExtendedConsentResponderInterface establishConsent;
    @Value("${pdl.regionalSecurityServicesHsaId}")
    private String servicesHsaId;

    // Injection seam for testing
    void setServicesHsaId(String servicesHsaId) {
        this.servicesHsaId = servicesHsaId;
    }

    @Override
    public PdlReport pdlReport(final PdlContext ctx, PatientWithEngagements patientEngagements) {
        return Report.generateReport(
                servicesHsaId,
                ctx,
                patientEngagements,
                blocksForPatient,
                consentForPatient,
                relationshipWithPatient
        );
    }

    @Override
    public PdlReport patientConsent(
            PdlContext ctx,
            PdlReport report,
            String patientId,
            String reason,
            int duration,
            RoundedTimeUnit roundedTimeUnit,
            PdlReport.ConsentType consentType
    ) {
        WithFallback<Boolean> consentStatus = Consent.establishConsent(
                servicesHsaId,
                ctx,
                patientId,
                consentType,
                establishConsent,
                reason,
                duration,
                roundedTimeUnit
        );
        return report.withConsent(consentStatus, consentType);

    }

    @Override
    public PdlReport patientRelationship(
            PdlContext ctx,
            PdlReport report,
            String patientId,
            String reason,
            int duration,
            RoundedTimeUnit timeUnit
    ) {
        WithFallback<Boolean> relationshipStatus = Relationship
                .establishRelation(
                        servicesHsaId,
                        ctx,
                        patientId,
                        establishRelationship,
                        reason,
                        duration,
                        timeUnit
                );

        return report.withRelationship(relationshipStatus);
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
