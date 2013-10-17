package se.vgregion.service.pdl;

import riv.ehr.blocking.accesscontrol._3.AccessingActorType;
import riv.ehr.blocking.accesscontrol._3.CheckResultType;
import riv.ehr.blocking.accesscontrol._3.InformationEntityType;
import se.riv.ehr.blocking.accesscontrol.checkblocksresponder.v3.CheckBlocksRequestType;
import se.riv.ehr.blocking.accesscontrol.checkblocksresponder.v3.CheckBlocksResponseType;
import se.vgregion.domain.pdl.CheckedBlock;
import se.vgregion.domain.pdl.Engagement;
import se.vgregion.domain.pdl.PatientWithEngagements;
import se.vgregion.domain.pdl.PdlContext;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
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

            en.setInformationCareProviderId(eg.careProviderHsaId);
            en.setInformationCareUnitId(eg.careUnitHsaId);
            en.setInformationStartDate(xmlDate());
            en.setInformationEndDate(xmlDate());
            if (eg.informationType != Engagement.InformationType.OTHR) {
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
    static List<CheckedBlock> asCheckedBlocks(
            PdlContext ctx,
            PatientWithEngagements pe,
            CheckBlocksResponseType blockResponse
    ) {
        ArrayList<CheckedBlock> checkedBlocks = new ArrayList<CheckedBlock>();
        for (CheckResultType crt : blockResponse.getCheckBlocksResultType().getCheckResults()) {
            int i = crt.getRowNumber();
            Engagement elem = pe.engagements.get(i);
            switch (crt.getStatus()) {
                case OK:
                    checkedBlocks.add(new CheckedBlock(elem, CheckedBlock.BlockStatus.OK));
                    break;
                case BLOCKED:
                    checkedBlocks.add(new CheckedBlock(elem, CheckedBlock.BlockStatus.BLOCKED));
                    break;
                case VALIDATIONERROR:
                    // TODO: 2013-10-10 Magnus Andersson This results in a skipped block. How to handle?
                    break;
            }
        }
        return Collections.unmodifiableList(checkedBlocks); // IMMUTABLE
    }

    private static XMLGregorianCalendar xmlDate() {
        try {
            GregorianCalendar c = new GregorianCalendar();
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException("Unable to create XMLDate", e);
        }
    }
}


