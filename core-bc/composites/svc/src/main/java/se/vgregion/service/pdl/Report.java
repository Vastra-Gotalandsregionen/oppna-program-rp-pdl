package se.vgregion.service.pdl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.riv.ehr.blocking.accesscontrol.checkblocks.v2.rivtabp21.CheckBlocksResponderInterface;
import se.riv.ehr.blocking.accesscontrol.checkblocksresponder.v2.CheckBlocksRequestType;
import se.riv.ehr.blocking.accesscontrol.checkblocksresponder.v2.CheckBlocksResponseType;
import se.riv.ehr.patientconsent.accesscontrol.checkconsent.v1.rivtabp21.CheckConsentResponderInterface;
import se.riv.ehr.patientconsent.accesscontrol.checkconsentresponder.v1.CheckConsentRequestType;
import se.riv.ehr.patientconsent.accesscontrol.checkconsentresponder.v1.CheckConsentResponseType;
import se.riv.ehr.patientrelationship.accesscontrol.checkpatientrelation.v1.rivtabp21.CheckPatientRelationResponderInterface;
import se.riv.ehr.patientrelationship.accesscontrol.checkpatientrelationresponder.v1.CheckPatientRelationRequestType;
import se.riv.ehr.patientrelationship.accesscontrol.checkpatientrelationresponder.v1.CheckPatientRelationResponseType;
import se.vgregion.domain.pdl.*;

import java.util.ArrayList;
import java.util.concurrent.*;

public class Report {

    private static final Logger LOGGER = LoggerFactory.getLogger(Report.class.getName());


    private Report() {
        // Utility class, no constructor!
    }

    static PdlReport generateReport(
            String servicesHsaId,
            final PdlContext ctx,
            final PatientWithEngagements patientEngagements,
            final CheckBlocksResponderInterface blocksForPatient,
            final CheckConsentResponderInterface consentForPatient,
            final CheckPatientRelationResponderInterface relationshipWithPatient) {

        ExecutorService executorService = Executors.newFixedThreadPool(3); // TODO: 2013-10-14: Magnus Andersson > Bad Choise? Feels like a bad idea to fix a pool.

        // Start multiple requests
        Future<ArrayList<CheckedBlock>> blocksFuture = blocks(servicesHsaId, ctx, patientEngagements, blocksForPatient, executorService);
        Future<CheckedConsent> consentFuture = consent(servicesHsaId, ctx, patientEngagements.patientId, consentForPatient, executorService);
        Future<Boolean> relationshipFuture = relationship(servicesHsaId, ctx, patientEngagements.patientId, relationshipWithPatient, executorService);

        // Aggreagate results
        WithFallback<ArrayList<CheckedBlock>> checkedBlocks = blocksWithFallback(blocksFuture, patientEngagements.patientId);
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
                    new CheckedConsent(PdlReport.ConsentType.Consent, true)
            );
        } catch (ExecutionException e) {
            LOGGER.warn("Failed to fetch consent for patientId {} during report generation. Using fallback response.", patientId, e);
            checkedConsent = WithFallback.fallback(
                    new CheckedConsent(PdlReport.ConsentType.Consent, true)
            );
        }
        return checkedConsent;
    }

    private static WithFallback<ArrayList<CheckedBlock>> blocksWithFallback(Future<ArrayList<CheckedBlock>> blocksFuture, String patientId) {
        WithFallback<ArrayList<CheckedBlock>> checkedBlocks = null;
        try {
            checkedBlocks = WithFallback.success(blocksFuture.get());
        } catch (InterruptedException e) {
            LOGGER.warn("Failed to fetch blocks for patientId {} during report generation. Using fallback response.", patientId, e);
            checkedBlocks = WithFallback.fallback(new ArrayList<CheckedBlock>());
        } catch (ExecutionException e) {
            LOGGER.warn("Failed to fetch blocks for patientId {} during report generation. Using fallback response.", patientId, e);
            checkedBlocks = WithFallback.fallback(new ArrayList<CheckedBlock>());
        }
        return checkedBlocks;
    }

    static Future<Boolean> relationship(
            final String servicesHsaId,
            final PdlContext ctx,
            final String patientId,
            final CheckPatientRelationResponderInterface relationshipWithPatient,
            ExecutorService executorService
    ) {
        Callable<Boolean> relationshipAsync = new Callable<Boolean>() {
            public Boolean call() throws Exception {
                return checkRelationship(servicesHsaId, ctx, patientId, relationshipWithPatient);
            }
        };

        return executorService.submit(relationshipAsync);
    }

    static Future<CheckedConsent> consent(
            final String servicesHsaId,
            final PdlContext ctx,
            final String patientId,
            final CheckConsentResponderInterface consentForPatient,
            ExecutorService executorService
    ) {
        Callable<CheckedConsent> consentAsync = new Callable<CheckedConsent>() {
            public CheckedConsent call() throws Exception {
                return checkConsent(servicesHsaId, ctx, patientId, consentForPatient);
            }
        };

        return executorService.submit(consentAsync);
    }

    static Future<ArrayList<CheckedBlock>> blocks(
            final String servicesHsaId, final PdlContext ctx,
            final PatientWithEngagements patientEngagements,
            final CheckBlocksResponderInterface blocksForPatient,
            ExecutorService executorService
    ) {
        Callable<ArrayList<CheckedBlock>> blocksAsync = new Callable<ArrayList<CheckedBlock>>() {
            public ArrayList<CheckedBlock> call() throws Exception {
                return checkBlocks(servicesHsaId, ctx, patientEngagements, blocksForPatient);
            }
        };

        return executorService.submit(blocksAsync);
    }

    static boolean checkRelationship(
            String regionalSecurityServicesHsaId,
            PdlContext ctx,
            String patientId,
            CheckPatientRelationResponderInterface relationshipWithPatient
    ) {
        CheckPatientRelationRequestType reuqest = Relationship.checkRelationshipRequest(ctx, patientId);
        CheckPatientRelationResponseType relationshipResponse =
                relationshipWithPatient.checkPatientRelation(regionalSecurityServicesHsaId, reuqest);

        return relationshipResponse.getCheckResultType().isHasPatientrelation();
    }

    static ArrayList<CheckedBlock> checkBlocks(
            String regionalSecurityServicesHsaId,
            PdlContext ctx,
            PatientWithEngagements patientEngagements,
            CheckBlocksResponderInterface blocksForPatient
    ) {
        CheckBlocksRequestType request = Blocking.checkBlocksRequest(ctx, patientEngagements);
        CheckBlocksResponseType blockResponse =
                blocksForPatient.checkBlocks(regionalSecurityServicesHsaId, request);

        return Blocking.asCheckedBlocks(ctx, patientEngagements, blockResponse);
    }

    static CheckedConsent checkConsent(
            String regionalSecurityServicesHsaId,
            PdlContext ctx,
            String patientId,
            CheckConsentResponderInterface consentForPatient
    ) {
        CheckConsentRequestType request = Consent.checkConsentRequest(ctx, patientId);
        CheckConsentResponseType consentResponse =
                consentForPatient.checkConsent(regionalSecurityServicesHsaId, request);

        return Consent.asCheckedConsent(consentResponse);
    }

}
