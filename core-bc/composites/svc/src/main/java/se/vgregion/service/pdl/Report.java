package se.vgregion.service.pdl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.riv.ehr.blocking.accesscontrol.checkblocks.v3.rivtabp21.CheckBlocksResponderInterface;
import se.riv.ehr.blocking.accesscontrol.checkblocksresponder.v3.CheckBlocksResponseType;
import se.riv.ehr.patientconsent.accesscontrol.checkconsent.v1.rivtabp21.CheckConsentResponderInterface;
import se.riv.ehr.patientconsent.accesscontrol.checkconsentresponder.v1.CheckConsentResponseType;
import se.riv.ehr.patientrelationship.accesscontrol.checkpatientrelation.v1.rivtabp21.CheckPatientRelationResponderInterface;
import se.riv.ehr.patientrelationship.accesscontrol.checkpatientrelationresponder.v1.CheckPatientRelationResponseType;
import se.vgregion.domain.pdl.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

public class Report {

    private static final Logger LOGGER = LoggerFactory.getLogger(Report.class.getName());


    private Report() {
        // Utility class, no constructor!
    }

    static PdlReport generateReport(
            final PdlContext ctx,
            final PatientWithEngagements patientEngagements,
            final CheckBlocksResponderInterface blocksForPatient,
            final CheckConsentResponderInterface consentForPatient,
            final CheckPatientRelationResponderInterface relationshipWithPatient) {

        ExecutorService executorService = Executors.newFixedThreadPool(3); // TODO: 2013-10-14: Magnus Andersson > Bad Choise? Feels like a bad idea to fix a pool.

        // Start multiple requests
        Future<List<CheckedBlock>> blocksFuture = blocks(ctx, patientEngagements, blocksForPatient, executorService);
        Future<CheckedConsent> consentFuture = consent(ctx, patientEngagements.patientId, consentForPatient, executorService);
        Future<Boolean> relationshipFuture = relationship(ctx, patientEngagements.patientId, relationshipWithPatient, executorService);

        // Aggreagate results
        WithFallback<List<CheckedBlock>> checkedBlocks = blocksWithFallback(blocksFuture, patientEngagements.patientId);
        WithFallback<CheckedConsent> checkedConsent = consentWithFallback(consentFuture, patientEngagements.patientId);
        WithFallback<Boolean> hasRelationship = relationshipWithFallback(relationshipFuture, patientEngagements.patientId);

        executorService.shutdown();

        return new PdlReport(checkedBlocks, checkedConsent, hasRelationship);
    }

    private static WithFallback<Boolean> relationshipWithFallback(Future<Boolean> relationshipFuture, String patientId) {
        WithFallback<Boolean> hasRelationship = WithFallback.fallback(true);
        try {
            hasRelationship = WithFallback.success(relationshipFuture.get());
        } catch (InterruptedException e) {
            LOGGER.warn("Failed to fetch relationship for patientId {} during report generation. Using fallback response.", patientId, e);
        } catch (ExecutionException e) {
            LOGGER.warn("Failed to fetch relationship for patientId {} during report generation. Using fallback response.", patientId, e);
        }
        return hasRelationship;
    }

    private static WithFallback<CheckedConsent> consentWithFallback(Future<CheckedConsent> consentFuture, String patientId) {
        WithFallback<CheckedConsent> checkedConsent = null;
        try {
            checkedConsent = WithFallback.success(consentFuture.get());
        } catch (InterruptedException e) {
            LOGGER.warn("Failed to fetch consent for patientId {} during report generation. Using fallback response.", patientId, e);
            checkedConsent = WithFallback.fallback(
                    new CheckedConsent(PdlReport.ConsentType.CONSENT, true)
            );
        } catch (ExecutionException e) {
            LOGGER.warn("Failed to fetch consent for patientId {} during report generation. Using fallback response.", patientId, e);
            checkedConsent = WithFallback.fallback(
                    new CheckedConsent(PdlReport.ConsentType.CONSENT, true)
            );
        }
        return checkedConsent;
    }

    private static WithFallback<List<CheckedBlock>> blocksWithFallback(Future<List<CheckedBlock>> blocksFuture, String patientId) {
        WithFallback<List<CheckedBlock>> checkedBlocks = null;
        try {
            checkedBlocks = WithFallback.success(blocksFuture.get());
        } catch (InterruptedException e) {
            LOGGER.warn("Failed to fetch blocks for patientId {} during report generation. Using fallback response.", patientId, e);
            checkedBlocks = WithFallback.fallback(
                    Collections.unmodifiableList(new ArrayList<CheckedBlock>()
                    )
            );
        } catch (ExecutionException e) {
            LOGGER.warn("Failed to fetch blocks for patientId {} during report generation. Using fallback response.", patientId, e);
            checkedBlocks = WithFallback.fallback(
                    Collections.unmodifiableList(new ArrayList<CheckedBlock>()
                    )
            );
        }
        return checkedBlocks;
    }

    static Future<Boolean> relationship(
            final PdlContext ctx,
            final String patientId,
            final CheckPatientRelationResponderInterface relationshipWithPatient,
            ExecutorService executorService
    ) {
        Callable<Boolean> relationshipAsync = new Callable<Boolean>() {
            public Boolean call() throws Exception {
                return checkRelationship(ctx, patientId, relationshipWithPatient);
            }
        };

        return executorService.submit(relationshipAsync);
    }

    static Future<CheckedConsent> consent(final PdlContext ctx, final String patientId,
                                          final CheckConsentResponderInterface consentForPatient, ExecutorService executorService) {
        Callable<CheckedConsent> consentAsync = new Callable<CheckedConsent>() {
            public CheckedConsent call() throws Exception {
                return checkConsent(ctx, patientId, consentForPatient);
            }
        };

        return executorService.submit(consentAsync);
    }

    static Future<List<CheckedBlock>> blocks(
            final PdlContext ctx,
            final PatientWithEngagements patientEngagements,
            final CheckBlocksResponderInterface blocksForPatient,
            ExecutorService executorService
    ) {
        Callable<List<CheckedBlock>> blocksAsync = new Callable<List<CheckedBlock>>() {
            public List<CheckedBlock> call() throws Exception {
                return checkBlocks(ctx, patientEngagements, blocksForPatient);
            }
        };

        return executorService.submit(blocksAsync);
    }

    static boolean checkRelationship(PdlContext ctx, String patientId, CheckPatientRelationResponderInterface relationshipWithPatient) {
        CheckPatientRelationResponseType relationshipResponse =
                relationshipWithPatient.checkPatientRelation(ctx.careProviderHsaId, Relationship.checkRelationshipRequest(ctx, patientId));

        return relationshipResponse.getCheckResultType().isHasPatientrelation();
    }

    static List<CheckedBlock> checkBlocks(PdlContext ctx, PatientWithEngagements patientEngagements, CheckBlocksResponderInterface blocksForPatient) {
        CheckBlocksResponseType blockResponse =
                blocksForPatient.checkBlocks(ctx.careProviderHsaId, Blocking.checkBlocksRequest(ctx, patientEngagements));

        return Blocking.asCheckedBlocks(ctx, patientEngagements, blockResponse);
    }

    static CheckedConsent checkConsent(PdlContext ctx, String patientId, CheckConsentResponderInterface consentForPatient) {
        CheckConsentResponseType consentResponse =
                consentForPatient.checkConsent(ctx.careProviderHsaId, Consent.checkConsentRequest(ctx, patientId));

        return Consent.asCheckedConsent(consentResponse);
    }

}
