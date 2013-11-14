package se.vgregion.service.pdl;


import se.riv.ehr.blocking.accesscontrol.checkblocksresponder.v2.CheckBlocksRequestType;
import se.riv.ehr.blocking.accesscontrol.checkblocksresponder.v2.CheckBlocksResponseType;
import se.riv.ehr.blocking.administration.getextendedblocksforpatient.v2.rivtabp21.GetExtendedBlocksForPatientResponderInterface;
import se.riv.ehr.blocking.administration.registertemporaryextendedrevoke.v2.rivtabp21.RegisterTemporaryExtendedRevokeResponderInterface;
import se.riv.ehr.blocking.v2.AccessingActorType;
import se.riv.ehr.blocking.v2.CheckResultType;
import se.riv.ehr.blocking.v2.InformationEntityType;
import se.vgregion.domain.pdl.*;

import java.util.ArrayList;
import java.util.List;

class Blocking {

    private Blocking(){
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
     *
     * @param ctx
     * @param pe
     *@param blockResponse  @return
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


    public static WithFallback<ArrayList<CheckedBlock>> unblockInformation(
            String servicesHsaId,
            GetExtendedBlocksForPatientResponderInterface ctx,
            RegisterTemporaryExtendedRevokeResponderInterface patientBlocks,
            PdlContext temporaryRevoke,
            Engagement engagement,
            String reason,
            int duration,
            RoundedTimeUnit roundedTimeUnit
    ) {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }
}


