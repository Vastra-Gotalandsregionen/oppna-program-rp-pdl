package se.vgregion.service.pdl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.riv.ehr.blocking.accesscontrol.checkblocks.v2.rivtabp21.CheckBlocksResponderInterface;
import se.riv.ehr.blocking.accesscontrol.checkblocksresponder.v2.CheckBlocksRequestType;
import se.riv.ehr.blocking.accesscontrol.checkblocksresponder.v2.CheckBlocksResponseType;
import se.riv.ehr.blocking.administration.registertemporaryextendedrevoke.v2.rivtabp21.RegisterTemporaryExtendedRevokeResponderInterface;
import se.riv.ehr.blocking.administration.registertemporaryextendedrevokeresponder.v2.RegisterTemporaryExtendedRevokeRequestType;
import se.riv.ehr.blocking.administration.registertemporaryextendedrevokeresponder.v2.RegisterTemporaryExtendedRevokeResponseType;
import se.riv.ehr.blocking.querying.getblocksforpatient.v2.rivtabp21.GetBlocksForPatientResponderInterface;
import se.riv.ehr.blocking.querying.getblocksforpatientresponder.v2.GetBlocksForPatientRequestType;
import se.riv.ehr.blocking.querying.getblocksforpatientresponder.v2.GetBlocksForPatientResponseType;
import se.riv.ehr.blocking.v2.*;
import se.vgregion.domain.pdl.*;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

class Blocking {
    private static final Logger LOGGER = LoggerFactory.getLogger(Blocking.class.getName());

    private Blocking() {
        // Utility class, no constructor!
    }

    static CheckBlocksRequestType checkBlocksRequest(PdlContext ctx, PatientWithEngagements pe) {
        CheckBlocksRequestType req = new CheckBlocksRequestType();
        req.setPatientId(pe.patientId);
        AccessingActorType actor = new AccessingActorType();
        actor.setCareProviderId(ctx.careProviderHsaId);
        actor.setCareUnitId(ctx.careUnitHsaId);
        actor.setEmployeeId(ctx.employeeHsaId);
        req.setAccessingActor(actor);

        List<InformationEntityType> entities = req.getInformationEntities();

        for (int i = 0; i < pe.engagements.size(); i++) {
            InformationEntityType en = new InformationEntityType();
            Engagement eg = pe.engagements.get(i);

            XMLDuration duration = new XMLDuration(1, RoundedTimeUnit.NEAREST_HALF_HOUR);

            en.setInformationCareProviderId(eg.careProviderHsaId);
            en.setInformationCareUnitId(eg.careUnitHsaId);
            en.setInformationStartDate(duration.startDate);
            en.setInformationEndDate(duration.endDate);
            if (eg.informationType != Engagement.InformationType.ALT) {
                en.setInformationType(eg.informationType.toString());
            }
            en.setRowNumber(i);

            entities.add(en);
        }

        return req;
    }

    /**
     * The WSDL-contract for some unfathomable reason requires the client to remember what row numbers were used when
     * sending the request. Therefore it is necessary to keep track of the request state and later enrich the answer.
     *
     * @param ctx
     * @param pe
     * @param blockResponse @return
     */
    static ArrayList<CheckedBlock> asCheckedBlocks(
            PdlContext ctx,
            PatientWithEngagements pe,
            CheckBlocksResponseType blockResponse
    ) {
        ArrayList<CheckedBlock> checkedBlocks = new ArrayList<CheckedBlock>();
        for (CheckResultType crt : blockResponse.getCheckBlocksResultType().getCheckResults()) {
            int i = crt.getRowNumber();
            Engagement elem = pe.engagements.get(i);
            if (crt.isBlocked()) {
                checkedBlocks.add(new CheckedBlock(elem, CheckedBlock.BlockStatus.BLOCKED));
            } else {
                checkedBlocks.add(new CheckedBlock(elem, CheckedBlock.BlockStatus.OK));
            }
        }
        return checkedBlocks;
    }

    private static GetBlocksForPatientRequestType patientBlocksRequest(String servicesHsaId, String patientId) {
        GetBlocksForPatientRequestType request = new GetBlocksForPatientRequestType();
        request.setCareProviderId(servicesHsaId);
        request.setPatientId(patientId);
        return request;
    }

    public static WithFallback<Boolean> unblockInformation(
            String servicesHsaId,
            GetBlocksForPatientResponderInterface patientBlocks,
            RegisterTemporaryExtendedRevokeResponderInterface temporaryRevoke,
            CheckBlocksResponderInterface checkBlocks,
            PdlContext ctx,
            String patientId,
            Engagement engagement,
            String reason,
            int duration,
            RoundedTimeUnit roundedTimeUnit,
            ExecutorService executorService
    ) {
        GetBlocksForPatientResponseType blocks = patientBlocks(servicesHsaId, patientBlocks, patientId);
        List<BlockType> activeBlocks = filterActiveBlocks(ctx, engagement, blocks);

        if (activeBlocks.size() > 0) {
            List<Callable<Boolean>> revokationList = new ArrayList<Callable<Boolean>>();

            for(BlockType block : activeBlocks ) {
                revokationList.add(
                        temporarilyRevoke(
                                ctx,
                                temporaryRevoke,
                                servicesHsaId,
                                block,
                                reason,
                                duration,
                                roundedTimeUnit
                        )
                );
            }

            List<Future<Boolean>> responses = new ArrayList<Future<Boolean>>();
            WithFallback<Boolean> revokeResult = WithFallback.fallback(true);
            try {
                responses = executorService.invokeAll(revokationList);
                boolean result = true;
                for( Future<Boolean> response : responses ) {
                    result &= response.get(5L, TimeUnit.SECONDS);
                }
                revokeResult = WithFallback.success(result);
            } catch (InterruptedException e) {
                LOGGER.error("Unable to finish calling temporary revoke service. One or more calls failed. Using fallback.", e);  //To change body of catch statement use File | Settings | File Templates.
                return WithFallback.fallback(true);
            } catch (ExecutionException e) {
                LOGGER.error("Unable to finish calling temporary revoke service. One or more calls failed. Using fallback.", e);  //To change body of catch statement use File | Settings | File Templates.
                return WithFallback.fallback(true);
            } catch (TimeoutException e) {
                LOGGER.error("Unable to finish calling temporary revoke service. One or more calls failed. Using fallback.", e);  //To change body of catch statement use File | Settings | File Templates.
                return WithFallback.fallback(true);
            }

            if( !revokeResult.isFallback() ) {
                // Finally check that we no longer have blocks for this information.
                CheckBlocksRequestType checkedRequest = checkBlocksRequest(
                        ctx,
                        new PatientWithEngagements(
                                patientId,
                                "PATIENT NAME PLACEHOLDER", // Patient name not needed for request.
                                Arrays.asList(engagement)
                        )
                );

                boolean containBlocks = checkBlocks
                        .checkBlocks(servicesHsaId, checkedRequest)
                        .getCheckBlocksResultType()
                        .getCheckResults().size() > 0;

                return WithFallback.success( containBlocks );
            }
        }

        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    private static GetBlocksForPatientResponseType patientBlocks(String servicesHsaId, GetBlocksForPatientResponderInterface patientBlocks, String patientId) {
        GetBlocksForPatientRequestType request = patientBlocksRequest(servicesHsaId, patientId);
        return patientBlocks.getBlocksForPatient(servicesHsaId, request);
    }

    private static Callable<Boolean> temporarilyRevoke(
            final PdlContext ctx,
            final RegisterTemporaryExtendedRevokeResponderInterface temporaryRevoke,
            final String servicesHsaId,
            final BlockType block,
            final String reason,
            final int duration,
            final RoundedTimeUnit roundedTimeUnit
    ) {
        final RegisterTemporaryExtendedRevokeRequestType request = revokeRequest(ctx, block, reason, duration, roundedTimeUnit);

        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                RegisterTemporaryExtendedRevokeResponseType response = temporaryRevoke.registerTemporaryExtendedRevoke(servicesHsaId, request);
                return response.getResultType().getResultCode() == ResultCodeType.OK;
            }
        };
    }

    private static RegisterTemporaryExtendedRevokeRequestType revokeRequest(
            PdlContext ctx,
            BlockType block,
            String reason, int duration,
            RoundedTimeUnit roundedTimeUnit
    ) {
        RegisterTemporaryExtendedRevokeRequestType request = new RegisterTemporaryExtendedRevokeRequestType();
        TemporaryRevokeRegistrationType revoke  = new TemporaryRevokeRegistrationType();
        revoke.setBlockId(block.getBlockId());
        revoke.setTemporaryRevokeId(java.util.UUID.randomUUID().toString());
        revoke.setRevokedForCareUnitId(ctx.careUnitHsaId);
        revoke.setRevokedForEmployeeId(ctx.employeeHsaId);
        XMLDuration xmlDuration = new XMLDuration(duration, roundedTimeUnit);
        revoke.setEndDate(xmlDuration.endDate);
        request.setRevokeReasonText(reason);
        request.setRevokeReason(TemporaryRevokeReasonType.PATIENTS_CONSENT); // FIXME 2013-11-15 : Magnus Andersson > This should be a parameter.

        return request;
    }

    private static List<BlockType> filterActiveBlocks(
            PdlContext ctx,
                Engagement engagement,
            GetBlocksForPatientResponseType blocks
    ) {

        List<BlockType> blockList = blocks.getBlockHeaderType().getBlocks();
        List<BlockType> activeBlocks = new ArrayList<BlockType>();

        for (BlockType block : blockList) {
            InformationTypeType infoType = new InformationTypeType();
            infoType.setInfoTypeId(engagement.informationType.name().toLowerCase());

            XMLGregorianCalendar currentDate = XMLDuration.currentDateAsXML();

            final boolean isInnerBlock        = block.getBlockType() == BlockTypeType.INNER;
            final boolean notExcludedInfoType = !block.getExcludedInformationTypes().contains(infoType);
            final boolean sameCareProvider    = block.getInformationCareProviderId().equals(ctx.careProviderHsaId);
            final boolean sameCareUnit        = block.getInformationCareUnitId().equals(ctx.careUnitHsaId);
            final boolean withinDuration      = currentDate.compare(block.getInformationStartDate()) > -1 &&
                                                currentDate.compare(block.getInformationEndDate()) < 1;

            if (
                isInnerBlock &&
                notExcludedInfoType &&
                sameCareProvider &&
                sameCareUnit &&
                withinDuration
            ) {

                if (block.getTemporaryRevokes().size() > 0) {
                    for (TemporaryRevokeType revoke : block.getTemporaryRevokes()) {

                        final boolean sameRevokeCareProvider = revoke.getRevokedForEmployeeId().equals(ctx.employeeHsaId);
                        final boolean sameRevokeCareUnit     = revoke.getRevokedForCareUnitId().equals(ctx.careUnitHsaId);
                        final boolean beforeEndDate          = currentDate.compare(revoke.getEndDate()) < 1;

                        // Temporary revoke was not applicable for this employee, still add the block
                        if (!(sameRevokeCareProvider && sameRevokeCareUnit && beforeEndDate)) {
                            activeBlocks.add(block);
                        }
                    }
                } else {
                    // Temporary block is applicable for this employee, add the block
                    activeBlocks.add(block);
                }
            }
        }
        return activeBlocks;
    }
}


