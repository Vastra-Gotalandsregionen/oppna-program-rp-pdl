package se.vgregion.service.pdl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.riv.ehr.blocking.accesscontrol.checkblocks.v2.rivtabp21.CheckBlocksResponderInterface;
import se.riv.ehr.blocking.administration.registertemporaryextendedrevoke.v2.rivtabp21.RegisterTemporaryExtendedRevokeResponderInterface;
import se.riv.ehr.blocking.querying.getblocksforpatient.v2.rivtabp21.GetBlocksForPatientResponderInterface;
import se.riv.ehr.patientconsent.accesscontrol.checkconsent.v1.rivtabp21.CheckConsentResponderInterface;
import se.riv.ehr.patientconsent.administration.registerextendedconsent.v1.rivtabp21.RegisterExtendedConsentResponderInterface;
import se.riv.ehr.patientrelationship.accesscontrol.checkpatientrelation.v1.rivtabp21.CheckPatientRelationResponderInterface;
import se.riv.ehr.patientrelationship.administration.registerextendedpatientrelation.v1.rivtabp21.RegisterExtendedPatientRelationResponderInterface;
import se.vgregion.domain.pdl.*;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Service
public class PdlServiceImpl implements PdlService {

    @Resource(name = "checkBlocks")
    private CheckBlocksResponderInterface checkBlocks;
    @Resource(name = "checkConsent")
    private CheckConsentResponderInterface checkConsent;
    @Resource(name = "checkRelationship")
    private CheckPatientRelationResponderInterface checkRelationship;
    @Resource(name = "blocksForPatient")
    private GetBlocksForPatientResponderInterface blocksForPatient;
    @Resource(name = "temporaryRevoke")
    private RegisterTemporaryExtendedRevokeResponderInterface temporaryRevoke;
    @Resource(name = "establishRelationship")
    private RegisterExtendedPatientRelationResponderInterface establishRelationship;
    @Resource(name = "establishConsent")
    private RegisterExtendedConsentResponderInterface establishConsent;
    @Value("${pdl.regionalSecurityServicesHsaId}")
    private String servicesHsaId;

    private ExecutorService executorService =
            Executors.newCachedThreadPool(new ThreadFactory() {
                private final ThreadFactory threadFactory = Executors.defaultThreadFactory();

                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = threadFactory.newThread(r);
                    thread.setDaemon(true);
                    return thread;
                }
            });

    @PreDestroy
    public void destroy() {
        executorService.shutdown();
    }

    // Injection seam for testing
    void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    // Injection seam for testing
    void setServicesHsaId(String servicesHsaId) {
        this.servicesHsaId = servicesHsaId;
    }

    @Override
    public PdlReport pdlReport(
            final PdlContext ctx,
            Patient patient,
            List<WithInfoType<CareSystem>> careSystems
    ) {
        return Report.generateReport(
                servicesHsaId,
                ctx,
                patient,
                careSystems,
                checkBlocks,
                checkConsent,
                checkRelationship,
                executorService
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
        WithFallback<CheckedConsent> consentStatus = Consent.establishConsent(
                servicesHsaId,
                establishConsent,
                ctx,
                patientId,
                consentType,
                reason,
                duration,
                roundedTimeUnit
        );

        return report.withConsent(consentStatus);
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
                        establishRelationship,
                        ctx,
                        patientId,
                        reason,
                        duration,
                        timeUnit
                );

        return report.withRelationship(relationshipStatus);
    }

    @Override
    public PdlReport unblockInformation(
            PdlContext ctx,
            PdlReport report,
            String patientId,
            UnblockType unblockType,
            String reason,
            int duration,
            RoundedTimeUnit roundedTimeUnit
    ) {
        return report; // FIXME  2013-11-15 : Magnus Andersson > Report not updated.
    }


    @Override
    public PdlAssertion chooseInformation(PdlContext ctx, PdlReport report) {
        throw new IllegalStateException("Not implemented");
    }
}
