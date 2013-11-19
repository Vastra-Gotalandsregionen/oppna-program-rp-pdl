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
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class Report {

    private static final Logger LOGGER = LoggerFactory.getLogger(Report.class.getName());


    private Report() {
        // Utility class, no constructor!
    }

    static PdlReport generateReport(
            String servicesHsaId,
            final PdlContext ctx,
            final Patient patient,
            List<WithInfoType<CareSystem>> careSystems,
            final CheckBlocksResponderInterface checkBlocks,
            final CheckConsentResponderInterface checkConsent,
            final CheckPatientRelationResponderInterface checkRelationship,
            final ExecutorService executorService
    ) {

        // Start multiple requests
        Future<ArrayList<WithInfoType<WithBlock<CareSystem>>>> blocksFuture =
                blocks(servicesHsaId, ctx, patient, careSystems, checkBlocks, executorService);

        Future<CheckedConsent> consentFuture =
                consent(servicesHsaId, ctx, patient.patientId, checkConsent, executorService);

        Future<Boolean> relationshipFuture =
                relationship(servicesHsaId, ctx, patient.patientId, checkRelationship, executorService);

        // Aggreagate results
        WithFallback<ArrayList<WithInfoType<WithBlock<CareSystem>>>> checkedSystems = blocksWithFallback(blocksFuture, careSystems, patient.patientId);
        WithFallback<CheckedConsent> checkedConsent = consentWithFallback(consentFuture, patient.patientId);
        WithFallback<Boolean> hasRelationship = relationshipWithFallback(relationshipFuture, patient.patientId);

        return new PdlReport(checkedSystems, checkedConsent, hasRelationship);
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

    private static WithFallback<ArrayList<WithInfoType<WithBlock<CareSystem>>>> blocksWithFallback(
            Future<ArrayList<WithInfoType<WithBlock<CareSystem>>>> blocksFuture,
            List<WithInfoType<CareSystem>> careSystems, String patientId
    ) {
        WithFallback<ArrayList<WithInfoType<WithBlock<CareSystem>>>> checkedBlocks = null;
        try {
            checkedBlocks = WithFallback.success(blocksFuture.get());
        } catch (InterruptedException e) {
            LOGGER.warn("Failed to fetch systmes for patientId {} during report generation. Using fallback response.", patientId, e);
            checkedBlocks = WithFallback.fallback(mapCareSystemsFallback(careSystems));
        } catch (ExecutionException e) {
            LOGGER.warn("Failed to fetch systmes for patientId {} during report generation. Using fallback response.", patientId, e);
            checkedBlocks = WithFallback.fallback(mapCareSystemsFallback(careSystems));
        }
        return checkedBlocks;
    }

    /**
     * <p>Maps List&lt;WithInfoType&lt;CareSystem&gt;&gt; to List&lt;WithInfoType&lt;WithBlock&lt;CareSystem&gt;&gt;&gt;.</p>
     * <p>None of the mapped care systems will have a block.</p>
     * @param careSystems Care systems to map
     * @return
     */
    private static ArrayList<WithInfoType<WithBlock<CareSystem>>> mapCareSystemsFallback(
            List<WithInfoType<CareSystem>> careSystems
    ) {
        ArrayList<WithInfoType<WithBlock<CareSystem>>> fallbackSystems = new ArrayList<WithInfoType<WithBlock<CareSystem>>>();

        for(WithInfoType<CareSystem> cs : careSystems) {
            WithBlock<CareSystem> unblockedSystem = WithBlock.unblocked(cs.value);
            WithInfoType<WithBlock<CareSystem>> infotypeWithFallbackBlock = cs.mapValue(unblockedSystem);
            fallbackSystems.add(infotypeWithFallbackBlock);
        }

        return fallbackSystems;
    }

    static Future<Boolean> relationship(
            final String servicesHsaId,
            final PdlContext ctx,
            final String patientId,
            final CheckPatientRelationResponderInterface checkRelationship,
            ExecutorService executorService
    ) {
        Callable<Boolean> relationshipAsync = new Callable<Boolean>() {
            public Boolean call() throws Exception {
                return checkRelationship(servicesHsaId, ctx, patientId, checkRelationship);
            }
        };

        return executorService.submit(relationshipAsync);
    }

    static Future<CheckedConsent> consent(
            final String servicesHsaId,
            final PdlContext ctx,
            final String patientId,
            final CheckConsentResponderInterface checkConsent,
            ExecutorService executorService
    ) {
        Callable<CheckedConsent> consentAsync = new Callable<CheckedConsent>() {
            public CheckedConsent call() throws Exception {
                return checkConsent(servicesHsaId, ctx, patientId, checkConsent);
            }
        };

        return executorService.submit(consentAsync);
    }

    static Future<ArrayList<WithInfoType<WithBlock<CareSystem>>>> blocks(
            final String servicesHsaId, final PdlContext ctx,
            final Patient patient,
            final List<WithInfoType<CareSystem>> careSystems,
            final CheckBlocksResponderInterface checkBlocks,
            final ExecutorService executorService
    ) {
        Callable<ArrayList<WithInfoType<WithBlock<CareSystem>>>> blocksAsync = new Callable<ArrayList<WithInfoType<WithBlock<CareSystem>>>>() {
            public ArrayList<WithInfoType<WithBlock<CareSystem>>> call() throws Exception {
                return checkBlocks(servicesHsaId, ctx, patient, careSystems, checkBlocks);
            }
        };

        return executorService.submit(blocksAsync);
    }

    static boolean checkRelationship(
            String regionalSecurityServicesHsaId,
            PdlContext ctx,
            String patientId,
            CheckPatientRelationResponderInterface checkRelationship
    ) {
        CheckPatientRelationRequestType reuqest = Relationship.checkRelationshipRequest(ctx, patientId);
        CheckPatientRelationResponseType relationshipResponse =
                checkRelationship.checkPatientRelation(regionalSecurityServicesHsaId, reuqest);

        return relationshipResponse.getCheckResultType().isHasPatientrelation();
    }

    static ArrayList<WithInfoType<WithBlock<CareSystem>>> checkBlocks(
            String regionalSecurityServicesHsaId,
            PdlContext ctx,
            Patient patient,
            List<WithInfoType<CareSystem>> careSystems,
            CheckBlocksResponderInterface checkBlocks
    ) {
        CheckBlocksRequestType request = Blocking.checkBlocksRequest(ctx, patient, careSystems);
        CheckBlocksResponseType blockResponse =
                checkBlocks.checkBlocks(regionalSecurityServicesHsaId, request);

        return Blocking.decorateCareSystems(careSystems, blockResponse);
    }


    static CheckedConsent checkConsent(
            String regionalSecurityServicesHsaId,
            PdlContext ctx,
            String patientId,
            CheckConsentResponderInterface checkConsent
    ) {
        CheckConsentRequestType request = Consent.checkConsentRequest(ctx, patientId);
        CheckConsentResponseType consentResponse =
                checkConsent.checkConsent(regionalSecurityServicesHsaId, request);

        return Consent.asCheckedConsent(consentResponse);
    }

}
