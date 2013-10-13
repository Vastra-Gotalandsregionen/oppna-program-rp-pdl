package se.vgregion.service.pdl;

import org.springframework.stereotype.Service;
import se.riv.ehr.blocking.accesscontrol.checkblocks.v3.rivtabp21.CheckBlocksResponderInterface;
import se.riv.ehr.blocking.accesscontrol.checkblocks.v3.rivtabp21.CheckBlocksResponderService;
import se.riv.ehr.blocking.accesscontrol.checkblocksresponder.v3.CheckBlocksResponseType;
import se.riv.ehr.patientconsent.accesscontrol.checkconsent.v1.rivtabp21.CheckConsentResponderInterface;
import se.riv.ehr.patientconsent.accesscontrol.checkconsent.v1.rivtabp21.CheckConsentResponderService;
import se.riv.ehr.patientconsent.accesscontrol.checkconsentresponder.v1.CheckConsentResponseType;
import se.riv.ehr.patientrelationship.accesscontrol.checkpatientrelation.v1.rivtabp21.CheckPatientRelationResponderInterface;
import se.riv.ehr.patientrelationship.accesscontrol.checkpatientrelation.v1.rivtabp21.CheckPatientRelationResponderService;
import se.riv.ehr.patientrelationship.accesscontrol.checkpatientrelationresponder.v1.CheckPatientRelationResponseType;
import se.vgregion.domain.pdl.*;

import javax.annotation.Resource;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.GregorianCalendar;
import java.util.List;

@Service
public class PdlServiceImpl implements PdlService {

    @Resource(name = "blocksForPatient")
    private CheckBlocksResponderService blocksForPatient;
    @Resource(name = "consentForPatient")
    private CheckConsentResponderService consentForPatient;
    @Resource(name = "relationshipWithPatient")
    private CheckPatientRelationResponderService relationshipWithPatient;

    public PdlServiceImpl() {
    }

    public static XMLGregorianCalendar xmlDate() {
        try {
            GregorianCalendar c = new GregorianCalendar();
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException("Unable to create XMLDate", e);
        }
    }

    public PdlReport pdlReport(PdlContext ctx) {

        List<CheckedBlock> checkedBlocks = checkBlocks(ctx);
        CheckedConsent checkedConsent = checkConsent(ctx);
        boolean hasRelationship = checkRelationship(ctx);

        return new PdlReport(checkedBlocks, checkedConsent, hasRelationship);
    }

    private boolean checkRelationship(PdlContext ctx) {
        CheckPatientRelationResponderInterface relationshipPort =
                relationshipWithPatient.getCheckPatientRelationResponderPort();
        CheckPatientRelationResponseType relationshipResponse =
                relationshipPort.checkPatientRelation(ctx.careProviderHsaId, Relationship.checkRelationshipRequest(ctx));

        return relationshipResponse.getCheckResultType().isHasPatientrelation();
    }

    private List<CheckedBlock> checkBlocks(PdlContext ctx) {
        CheckBlocksResponderInterface blockPort = blocksForPatient.getCheckBlocksResponderPort();
        CheckBlocksResponseType blockResponse =
                blockPort.checkBlocks(ctx.careProviderHsaId, Blocking.checkBlocksRequest(ctx));

        return Blocking.asCheckedBlocks(ctx, blockResponse);
    }

    public CheckedConsent checkConsent(PdlContext ctx) {
        CheckConsentResponderInterface consentPort =
                consentForPatient.getCheckConsentResponderPort();
        CheckConsentResponseType consentResponse =
                consentPort.checkConsent(ctx.careProviderHsaId, Consent.checkConsentRequest(ctx));

        return Consent.asCheckedConsent(consentResponse);
    }




}
