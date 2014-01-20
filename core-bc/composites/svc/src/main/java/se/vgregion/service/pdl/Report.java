package se.vgregion.service.pdl;

import org.apache.cxf.binding.soap.SoapFault;
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
import se.vgregion.domain.decorators.WithBlock;
import se.vgregion.domain.decorators.WithInfoType;
import se.vgregion.domain.decorators.WithOutcome;
import se.vgregion.domain.pdl.*;

import javax.xml.ws.WebServiceException;
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
            final String servicesHsaId,
            final PdlContext ctx,
            final Patient patient,
            final List<WithInfoType<CareSystem>> careSystems,
            final CheckBlocksResponderInterface checkBlocks,
            final CheckConsentResponderInterface checkConsent,
            final CheckPatientRelationResponderInterface checkRelationship,
            final ExecutorService executorService
    ) {

        // Start multiple requests
        Future<WithOutcome<ArrayList<WithInfoType<WithBlock<CareSystem>>>>> blocksFuture =
                blocks(servicesHsaId, ctx, patient, careSystems, checkBlocks, executorService);

        Future<WithOutcome<CheckedConsent>> consentFuture;

        if(ctx.currentAssignment.isOtherProviders()) {
            consentFuture =
                consent(
                    servicesHsaId,
                    ctx,
                    patient.patientId,
                    checkConsent,
                    executorService
                );
        } else {
            // Return dummy false value. This will not be used since the assignment is only within same care giver.
            consentFuture = consentNotNeeded(executorService);
        }

        Future<WithOutcome<Boolean>> relationshipFuture =
                relationship(servicesHsaId, ctx, patient.patientId, checkRelationship, executorService);

        // Aggreagate results
        WithOutcome<ArrayList<WithInfoType<WithBlock<CareSystem>>>> checkedSystems =
                blocksWithFallback(blocksFuture, careSystems, patient.patientId);

        WithOutcome<CheckedConsent> checkedConsent =
                consentWithFallback(consentFuture, patient.patientId);

        WithOutcome<Boolean> hasRelationship =
                relationshipWithFallback(relationshipFuture, patient.patientId);

        return new PdlReport(checkedSystems, checkedConsent, hasRelationship);
    }

    private static WithOutcome<Boolean> relationshipWithFallback(Future<WithOutcome<Boolean>> relationshipFuture, String patientId) {
        WithOutcome<Boolean> hasRelationship = WithOutcome.clientError(true);
        try {
            hasRelationship = relationshipFuture.get();
        } catch (InterruptedException e) {
            LOGGER.error("Failed to fetch relationship for patientId {} during report generation. Using fallback response.", patientId, e);
        } catch (ExecutionException e) {
            LOGGER.error("Failed to fetch relationship for patientId {} during report generation. Using fallback response.", patientId, e);
        }  catch (WebServiceException e) {
            LOGGER.error("Failed to fetch relationship for patientId {} during report generation. Using fallback response.", patientId, e);
            hasRelationship = WithOutcome.commFailure(true);
        } catch (SoapFault e) {
            LOGGER.error("Failed to fetch relationship for patientId {} during report generation. Using fallback response.", patientId, e);
            hasRelationship = WithOutcome.commFailure(true);
        }
        return hasRelationship;
    }

    private static WithOutcome<CheckedConsent> consentWithFallback(Future<WithOutcome<CheckedConsent>> consentFuture, String patientId) {
        WithOutcome<CheckedConsent> checkedConsent = WithOutcome.clientError(
                new CheckedConsent(PdlReport.ConsentType.Consent, true));
        try {
            checkedConsent = consentFuture.get();
        } catch (InterruptedException e) {
            LOGGER.error("Failed to fetch consent for patientId {} during report generation. Using fallback response.", patientId, e);
        } catch (ExecutionException e) {
            LOGGER.error("Failed to fetch consent for patientId {} during report generation. Using fallback response.", patientId, e);
        } catch (WebServiceException e) {
            LOGGER.error("Failed to fetch systems for patientId {} during report generation. Using fallback response.", patientId, e);
            checkedConsent = WithOutcome.commFailure(
                    new CheckedConsent(PdlReport.ConsentType.Consent, true));
        } catch (SoapFault e) {
            LOGGER.error("Failed to fetch systems for patientId {} during report generation. Using fallback response.", patientId, e);
            checkedConsent = WithOutcome.commFailure(
                    new CheckedConsent(PdlReport.ConsentType.Consent, true));
        }
        return checkedConsent;
    }

    private static WithOutcome<ArrayList<WithInfoType<WithBlock<CareSystem>>>> blocksWithFallback(
            Future<WithOutcome<ArrayList<WithInfoType<WithBlock<CareSystem>>>>> blocksFuture,
            List<WithInfoType<CareSystem>> careSystems, String patientId
    ) {
        WithOutcome<ArrayList<WithInfoType<WithBlock<CareSystem>>>> checkedBlocks =
                WithOutcome.clientError(mapCareSystemsFallback(careSystems));
        try {
            checkedBlocks = blocksFuture.get();
        } catch (InterruptedException e) {
            LOGGER.error("Failed to fetch systems for patientId {} during report generation. Using fallback response.", patientId, e);
        } catch (ExecutionException e) {
            LOGGER.error("Failed to fetch systems for patientId {} during report generation. Using fallback response.", patientId, e);
        } catch (WebServiceException e) {
            LOGGER.error("Failed to fetch systems for patientId {} during report generation. Using fallback response.", patientId, e);
            checkedBlocks = WithOutcome.commFailure(mapCareSystemsFallback(careSystems));
        } catch (SoapFault e) {
            LOGGER.error("Failed to fetch systems for patientId {} during report generation. Using fallback response.", patientId, e);
            checkedBlocks = WithOutcome.commFailure(mapCareSystemsFallback(careSystems));
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

    static Future<WithOutcome<Boolean>> relationship(
            final String servicesHsaId,
            final PdlContext ctx,
            final String patientId,
            final CheckPatientRelationResponderInterface checkRelationship,
            ExecutorService executorService
    ) {
        Callable<WithOutcome<Boolean>> relationshipAsync = new Callable<WithOutcome<Boolean>>(){
            public WithOutcome<Boolean>call() throws Exception {
                return checkRelationship(servicesHsaId, ctx, patientId, checkRelationship);
            }
        };

        return executorService.submit(relationshipAsync);
    }

    static Future<WithOutcome<CheckedConsent>> consent(
            final String servicesHsaId,
            final PdlContext ctx,
            final String patientId,
            final CheckConsentResponderInterface checkConsent,
            ExecutorService executorService
    ) {
        Callable<WithOutcome<CheckedConsent>> consentAsync = new Callable<WithOutcome<CheckedConsent>>(){
            public WithOutcome<CheckedConsent> call() throws Exception {
                return checkConsent(servicesHsaId, ctx, patientId, checkConsent);
            }
        };

        return executorService.submit(consentAsync);
    }

    // Sorry for all the brackets...
    // This is a list of Care systems, each belonging to a specific information type.
    // The response is from an external system. It can fail with fallback or succeed.
    // This is all executed async.
    static Future<WithOutcome<ArrayList<WithInfoType<WithBlock<CareSystem>>>>> blocks(
            final String servicesHsaId, final PdlContext ctx,
            final Patient patient,
            final List<WithInfoType<CareSystem>> careSystems,
            final CheckBlocksResponderInterface checkBlocks,
            final ExecutorService executorService
    ) {
        Callable<WithOutcome<ArrayList<WithInfoType<WithBlock<CareSystem>>>>> blocksAsync =
                new Callable<WithOutcome<ArrayList<WithInfoType<WithBlock<CareSystem>>>>>() {
                    public WithOutcome<ArrayList<WithInfoType<WithBlock<CareSystem>>>> call() throws Exception {
                        return checkBlocks(servicesHsaId, ctx, patient, careSystems, checkBlocks);
                    }
                };

        return executorService.submit(blocksAsync);
    }

    static WithOutcome<Boolean> checkRelationship(
            String regionalSecurityServicesHsaId,
            PdlContext ctx,
            String patientId,
            CheckPatientRelationResponderInterface checkRelationship
    ) {
        CheckPatientRelationRequestType request = Relationship.checkRelationshipRequest(ctx, patientId);
        CheckPatientRelationResponseType relationshipResponse =
                checkRelationship.checkPatientRelation(regionalSecurityServicesHsaId, request);

        boolean relation = relationshipResponse.getCheckResultType().isHasPatientrelation();

        return Relationship.decideOutcome(relationshipResponse.getCheckResultType().getResult(), relation);
    }

    static WithOutcome<ArrayList<WithInfoType<WithBlock<CareSystem>>>> checkBlocks(
            String regionalSecurityServicesHsaId,
            PdlContext ctx,
            Patient patient,
            List<WithInfoType<CareSystem>> careSystems,
            CheckBlocksResponderInterface checkBlocks
    ) {
        CheckBlocksRequestType request = Blocking.checkBlocksRequest(ctx, patient, careSystems);
        CheckBlocksResponseType blockResponse =
                checkBlocks.checkBlocks(regionalSecurityServicesHsaId, request);

        ArrayList<WithInfoType<WithBlock<CareSystem>>> decoratedSystems = Blocking.decorateCareSystems(careSystems, blockResponse);

        return Blocking.decideOutcome(blockResponse.getCheckBlocksResultType().getResult(), decoratedSystems);
    }

    static WithOutcome<CheckedConsent> checkConsent(
            String regionalSecurityServicesHsaId,
            PdlContext ctx,
            String patientId,
            CheckConsentResponderInterface checkConsent
    ) {
        CheckConsentRequestType request = Consent.checkConsentRequest(ctx, patientId);
        CheckConsentResponseType consentResponse =
                checkConsent.checkConsent(regionalSecurityServicesHsaId, request);

        CheckedConsent consent = Consent.asCheckedConsent(consentResponse);

        return Consent.decideOutcome(consentResponse.getCheckResultType().getResult(), consent);
    }

    private static Future<WithOutcome<CheckedConsent>> consentNotNeeded(ExecutorService executorService) {
        Future<WithOutcome<CheckedConsent>> consentFuture;Callable<WithOutcome<CheckedConsent>> consentCallable =
                new Callable<WithOutcome<CheckedConsent>>() {
                    public WithOutcome<CheckedConsent> call() {
                        return WithOutcome.success(new CheckedConsent(PdlReport.ConsentType.Consent, false));
                    }
                };

        consentFuture = executorService.submit(consentCallable);
        return consentFuture;
    }

}
