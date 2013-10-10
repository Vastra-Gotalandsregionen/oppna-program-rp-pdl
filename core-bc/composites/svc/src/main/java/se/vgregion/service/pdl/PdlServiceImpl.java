package se.vgregion.service.pdl;

import org.springframework.stereotype.Service;
import riv.ehr.blocking.accesscontrol._3.AccessingActorType;
import riv.ehr.blocking.accesscontrol._3.CheckResultType;
import riv.ehr.blocking.accesscontrol._3.InformationEntityType;
import se.riv.ehr.blocking.accesscontrol.checkblocks.v3.rivtabp21.CheckBlocksResponderInterface;
import se.riv.ehr.blocking.accesscontrol.checkblocks.v3.rivtabp21.CheckBlocksResponderService;
import se.riv.ehr.blocking.accesscontrol.checkblocksresponder.v3.CheckBlocksRequestType;
import se.riv.ehr.blocking.accesscontrol.checkblocksresponder.v3.CheckBlocksResponseType;
import se.vgregion.pdl.domain.*;

import javax.annotation.Resource;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

@Service
public class PdlServiceImpl implements PdlService {

    private static final String VGR_HSA_ID = "SE165565594230-1234"; // FIXME: Magnus Andersson 2013-10-10 This HSA-ID is FAKE! Put in config file.
    @Resource(name = "blocksForPatient")
    private CheckBlocksResponderService blocksForPatient;

    public PdlServiceImpl() {
    }

    private XMLGregorianCalendar xmlDate() {
        try {
            GregorianCalendar c = new GregorianCalendar();
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException("Unable to create XMLDate", e);
        }
    }

    private CheckBlocksRequestType checkBlocksRequest(PdlContext ctx) {
        CheckBlocksRequestType req = new CheckBlocksRequestType();
        req.setPatientId(ctx.patientHsaId);
        AccessingActorType actor = new AccessingActorType();
        actor.setCareProviderId(ctx.careProviderHsaId);
        actor.setCareUnitId(ctx.careUnitHsaId);
        actor.setEmployeeId(ctx.employeeHsaId);
        req.setAccessingActor(actor);

        List<InformationEntityType> entities = req.getInformationEntities();

        for (int i = 0; i < ctx.engagements.size(); i++) {
            InformationEntityType en = new InformationEntityType();
            PatientEngagement eg = ctx.engagements.get(i);

            en.setInformationCareProviderId(eg.careProviderHsaId);
            en.setInformationCareUnitId(eg.careUnitHsaId);
            en.setInformationStartDate(xmlDate());
            en.setInformationEndDate(xmlDate());
            if (eg.informationType != InformationType.OTHR) {
                en.setInformationType(eg.informationType.toString());
            }
            en.setRowNumber(i);

            entities.add(en);
        }

        return req;
    }

    public PdlReport pdlReport(PdlContext ctx) {
        CheckBlocksRequestType blockRequest = null;
        blockRequest = checkBlocksRequest(ctx);

        CheckBlocksResponderInterface port = blocksForPatient.getCheckBlocksResponderPort();
        CheckBlocksResponseType blockResponse = port.checkBlocks(ctx.careProviderHsaId, blockRequest);

        return new PdlReport(asCheckedBlocks(ctx, blockResponse));
    }

    /**
     * The WSDL-contract for some unfathomable reason requires me to remember what row numbers I used when sending in my request.
     * I then have to keep track of my request state and enrich the answer with the information I had in the request.
     *
     * @param ctx
     * @param blockResponse
     * @return
     */
    private List<CheckedBlock> asCheckedBlocks(PdlContext ctx, CheckBlocksResponseType blockResponse) {
        ArrayList<CheckedBlock> checkedBlocks = new ArrayList<CheckedBlock>();
        for( CheckResultType crt : blockResponse.getCheckBlocksResultType().getCheckResults() ){
            int i = crt.getRowNumber();
            PatientEngagement elem = ctx.engagements.get(i);
            switch( crt.getStatus() ) {
                case OK:
                    checkedBlocks.add( new CheckedBlock( elem, CheckedBlock.BlockStatus.OK ) );
                    break;
                case BLOCKED:
                    checkedBlocks.add( new CheckedBlock( elem, CheckedBlock.BlockStatus.BLOCKED ) );
                    break;
                case VALIDATIONERROR:
                    // TODO: 2013-10-10 Magnus Andersson This results in a skipped block. How to handle?
                    break;
            }
        }
        return checkedBlocks;
    }
}
