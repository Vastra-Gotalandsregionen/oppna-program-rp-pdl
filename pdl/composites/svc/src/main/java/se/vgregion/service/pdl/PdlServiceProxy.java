package se.vgregion.service.pdl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.riv.ehr.blocking.accesscontrol.checkblocks.v2.rivtabp21.CheckBlocksResponderInterface;
import se.riv.ehr.blocking.administration.registertemporaryextendedrevoke.v2.rivtabp21.RegisterTemporaryExtendedRevokeResponderInterface;
import se.riv.ehr.blocking.querying.getblocksforpatient.v2.rivtabp21.GetBlocksForPatientResponderInterface;
import se.riv.ehr.patientconsent.accesscontrol.checkconsent.v1.rivtabp21.CheckConsentResponderInterface;
import se.riv.ehr.patientconsent.administration.registerextendedconsent.v1.rivtabp21.RegisterExtendedConsentResponderInterface;
import se.riv.ehr.patientconsent.querying.getconsentsforpatient.v1.rivtabp21.GetConsentsForPatientResponderInterface;
import se.riv.ehr.patientrelationship.accesscontrol.checkpatientrelation.v1.rivtabp21.CheckPatientRelationResponderInterface;
import se.riv.ehr.patientrelationship.administration.registerextendedpatientrelation.v1.rivtabp21.RegisterExtendedPatientRelationResponderInterface;
import se.vgregion.domain.assignment.Assignment;
import se.vgregion.domain.pdl.*;
import se.vgregion.domain.decorators.WithOutcome;
import se.vgregion.domain.decorators.WithInfoType;
import se.vgregion.domain.systems.CareSystem;
import se.vgregion.events.context.Patient;
import se.vgregion.domain.pdl.PdlContext;
import se.vgregion.service.search.PdlService;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Service
public class PdlServiceProxy implements PdlService {

    @Resource(name = "checkBlocks")
    private CheckBlocksResponderInterface checkBlocks;
    @Resource(name = "checkConsent")
    private CheckConsentResponderInterface checkConsent;
    @Resource(name = "getConsentsForPatient")
    private GetConsentsForPatientResponderInterface getConsentsForPatient;
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
                    thread.setName("PDL-threadpool-" + thread.getName());
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
    @Deprecated
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
                getConsentsForPatient,
                checkRelationship,
                executorService,
                null
        );
    }

    @Override
    public PdlReport pdlReport(PdlContext ctx, Patient patient, List<WithInfoType<CareSystem>> careSystems, Assignment currentAssignment) {
        return Report.generateReport(
                servicesHsaId,
                ctx,
                patient,
                careSystems,
                checkBlocks,
                getConsentsForPatient,
                checkRelationship,
                executorService,
                currentAssignment
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
        WithOutcome<CheckedConsent> consentStatus = Consent.establishConsentWithFallback(
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
        WithOutcome<Boolean> relationshipStatus = Relationship
                .establishRelationWithFallback(
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
        // We're intentionally passing blocks in the app, not in security services here.
        return report;
    }


    @Override
    public PdlAssertion chooseInformation(PdlContext ctx, PdlReport report) {
        throw new IllegalStateException("Not implemented");
    }
}
