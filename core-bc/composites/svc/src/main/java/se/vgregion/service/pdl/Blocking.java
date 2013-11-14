package se.vgregion.service.pdl;


import se.riv.ehr.blocking.accesscontrol.checkblocksresponder.v2.CheckBlocksRequestType;
import se.riv.ehr.blocking.accesscontrol.checkblocksresponder.v2.CheckBlocksResponseType;
import se.riv.ehr.blocking.administration.registertemporaryextendedrevoke.v2.rivtabp21.RegisterTemporaryExtendedRevokeResponderInterface;
import se.riv.ehr.blocking.querying.getblocksforpatient.v2.rivtabp21.GetBlocksForPatientResponderInterface;
import se.riv.ehr.blocking.querying.getblocksforpatientresponder.v2.GetBlocksForPatientRequestType;
import se.riv.ehr.blocking.querying.getblocksforpatientresponder.v2.GetBlocksForPatientResponseType;
import se.riv.ehr.blocking.v2.*;
import se.vgregion.domain.pdl.*;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;

class Blocking {

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

    public static WithFallback<ArrayList<CheckedBlock>> unblockInformation(
            String servicesHsaId,
            GetBlocksForPatientResponderInterface patientBlocks,
            RegisterTemporaryExtendedRevokeResponderInterface temporaryRevoke,
            PdlContext ctx,
            String patientId,
            Engagement engagement,
            String reason,
            int duration,
            RoundedTimeUnit roundedTimeUnit
    ) {
        GetBlocksForPatientRequestType request = patientBlocksRequest(servicesHsaId, patientId);
        GetBlocksForPatientResponseType blocks = patientBlocks.getBlocksForPatient(servicesHsaId, request);

        List<BlockType> activeBlocks = filterActiveBlocks(ctx, engagement, blocks);



        if (activeBlocks.size() > 0) {

        }

        return null;  //To change body of created methods use File | Settings | File Templates.
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


