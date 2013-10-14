package se.vgregion.service.pdl;

import se.riv.ehr.blocking.accesscontrol.checkblocks.v3.rivtabp21.CheckBlocksResponderInterface;
import se.riv.ehr.blocking.accesscontrol.checkblocks.v3.rivtabp21.CheckBlocksResponderService;
import se.riv.ehr.blocking.accesscontrol.checkblocksresponder.v3.CheckBlocksResponseType;
import se.riv.ehr.patientconsent.accesscontrol.checkconsent.v1.rivtabp21.CheckConsentResponderInterface;
import se.riv.ehr.patientconsent.accesscontrol.checkconsent.v1.rivtabp21.CheckConsentResponderService;
import se.riv.ehr.patientconsent.accesscontrol.checkconsentresponder.v1.CheckConsentResponseType;
import se.riv.ehr.patientrelationship.accesscontrol.checkpatientrelation.v1.rivtabp21.CheckPatientRelationResponderInterface;
import se.riv.ehr.patientrelationship.accesscontrol.checkpatientrelation.v1.rivtabp21.CheckPatientRelationResponderService;
import se.riv.ehr.patientrelationship.accesscontrol.checkpatientrelationresponder.v1.CheckPatientRelationResponseType;
import se.vgregion.domain.pdl.CheckedBlock;
import se.vgregion.domain.pdl.CheckedConsent;
import se.vgregion.domain.pdl.PdlContext;
import se.vgregion.domain.pdl.PdlReport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Report {
    private Report() {
        // Utility class, no constructor!
    }

    static PdlReport generateReport(
            final PdlContext ctx,
            final CheckBlocksResponderService blocksForPatient,
            final CheckConsentResponderService consentForPatient,
            final CheckPatientRelationResponderService relationshipWithPatient) {

        ExecutorService executorService = Executors.newFixedThreadPool(3); // TODO: 2013-10-14: Magnus Andersson > Bad Choise? Feels like a bad idea to fix a pool.

        // Start multiple requests
        Future<List<CheckedBlock>> blocksFuture = blocks(ctx, blocksForPatient, executorService);
        Future<CheckedConsent> consentFuture = consent(ctx, consentForPatient, executorService);
        Future<Boolean> relationshipFuture = relationship(ctx, relationshipWithPatient, executorService);

        // Aggreagate results
        List<CheckedBlock> checkedBlocks = blocksWithFallback(blocksFuture);
        CheckedConsent checkedConsent = consentWithFallback(consentFuture);
        Boolean hasRelationship = relationshipWithFallback(relationshipFuture);

        executorService.shutdown();

        return new PdlReport(checkedBlocks, checkedConsent, hasRelationship);
    }

    private static Boolean relationshipWithFallback(Future<Boolean> relationshipFuture) {
        Boolean hasRelationship = true;
        try {
            hasRelationship = relationshipFuture.get();
        } catch (InterruptedException e) {
            // FIXME 2013-10-14 - Magnus Andersson > LOG this!
        } catch (ExecutionException e) {
            // FIXME 2013-10-14 - Magnus Andersson > LOG this!
        }
        return hasRelationship;
    }

    private static CheckedConsent consentWithFallback(Future<CheckedConsent> consentFuture) {
        CheckedConsent checkedConsent = null;
        try {
            checkedConsent = consentFuture.get();
        } catch (InterruptedException e) {
            // FIXME 2013-10-14 - Magnus Andersson > LOG this!
            checkedConsent = new CheckedConsent(PdlReport.ConsentType.FALLBACK, true); // Fallback!
        } catch (ExecutionException e) {
            // FIXME 2013-10-14 - Magnus Andersson > LOG this!
            checkedConsent = new CheckedConsent(PdlReport.ConsentType.FALLBACK, true); // Fallback!
        }
        return checkedConsent;
    }

    private static List<CheckedBlock> blocksWithFallback(Future<List<CheckedBlock>> blocksFuture) {
        List<CheckedBlock> checkedBlocks = null;
        try {
            checkedBlocks = blocksFuture.get();
        } catch (InterruptedException e) {
            // FIXME 2013-10-14 - Magnus Andersson > LOG this!
            checkedBlocks = new ArrayList<CheckedBlock>(); // Fallback!
        } catch (ExecutionException e) {
            // FIXME 2013-10-14 - Magnus Andersson > LOG this!
            checkedBlocks = new ArrayList<CheckedBlock>(); // Fallback!
        }
        return checkedBlocks;
    }

    static Future<Boolean> relationship(final PdlContext ctx, final CheckPatientRelationResponderService relationshipWithPatient, ExecutorService executorService) {
        Callable<Boolean> relationshipAsync = new Callable<Boolean>() {
            public Boolean call() throws Exception {
                return checkRelationship(ctx, relationshipWithPatient);
            }
        };

        return executorService.submit(relationshipAsync);
    }

    static Future<CheckedConsent> consent(final PdlContext ctx, final CheckConsentResponderService consentForPatient, ExecutorService executorService) {
        Callable<CheckedConsent> consentAsync = new Callable<CheckedConsent>() {
            public CheckedConsent call() throws Exception {
                return checkConsent(ctx, consentForPatient);
            }
        };

        return executorService.submit(consentAsync);
    }

    static Future<List<CheckedBlock>> blocks(final PdlContext ctx, final CheckBlocksResponderService blocksForPatient, ExecutorService executorService) {
        Callable<List<CheckedBlock>> blocksAsync = new Callable<List<CheckedBlock>>() {
            public List<CheckedBlock> call() throws Exception {
                return checkBlocks(ctx, blocksForPatient);
            }
        };

        return executorService.submit(blocksAsync);
    }

    static boolean checkRelationship(PdlContext ctx, CheckPatientRelationResponderService relationshipWithPatient) {
        CheckPatientRelationResponderInterface relationshipPort =
                relationshipWithPatient.getCheckPatientRelationResponderPort();
        CheckPatientRelationResponseType relationshipResponse =
                relationshipPort.checkPatientRelation(ctx.careProviderHsaId, Relationship.checkRelationshipRequest(ctx));

        return relationshipResponse.getCheckResultType().isHasPatientrelation();
    }

    static List<CheckedBlock> checkBlocks(PdlContext ctx, CheckBlocksResponderService blocksForPatient) {
        CheckBlocksResponderInterface blockPort = blocksForPatient.getCheckBlocksResponderPort();
        CheckBlocksResponseType blockResponse =
                blockPort.checkBlocks(ctx.careProviderHsaId, Blocking.checkBlocksRequest(ctx));

        return Blocking.asCheckedBlocks(ctx, blockResponse);
    }

    static CheckedConsent checkConsent(PdlContext ctx, CheckConsentResponderService consentForPatient) {
        CheckConsentResponderInterface consentPort =
                consentForPatient.getCheckConsentResponderPort();
        CheckConsentResponseType consentResponse =
                consentPort.checkConsent(ctx.careProviderHsaId, Consent.checkConsentRequest(ctx));

        return Consent.asCheckedConsent(consentResponse);
    }

}
