package se.vgregion.service.pdl;


import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import se.riv.ehr.blocking.accesscontrol.checkblocks.v3.rivtabp21.CheckBlocksResponderInterface;
import se.riv.ehr.blocking.accesscontrol.checkblocksresponder.v3.CheckBlocksRequestType;
import se.riv.ehr.patientconsent.accesscontrol.checkconsent.v1.rivtabp21.CheckConsentResponderInterface;
import se.riv.ehr.patientconsent.accesscontrol.checkconsentresponder.v1.CheckConsentRequestType;
import se.riv.ehr.patientrelationship.accesscontrol.checkpatientrelation.v1.rivtabp21.CheckPatientRelationResponderInterface;
import se.riv.ehr.patientrelationship.accesscontrol.checkpatientrelationresponder.v1.CheckPatientRelationRequestType;
import se.vgregion.domain.pdl.Engagement;
import se.vgregion.domain.pdl.PatientWithEngagements;
import se.vgregion.domain.pdl.PdlContext;
import se.vgregion.domain.pdl.PdlReport;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

public class PdlServiceSpecification {

    @Mock
    private CheckBlocksResponderInterface blocksInterface;
    @Mock
    private CheckConsentResponderInterface consentInterface;
    @Mock
    private CheckPatientRelationResponderInterface relationshipInterface;

    @InjectMocks
    private PdlServiceImpl service = new PdlServiceImpl();

    private PdlContext ctx;
    private PatientWithEngagements pe
            ;

    @Before
    public void setuUp() {
        MockitoAnnotations.initMocks(this);

        List<Engagement> engagements = Arrays.asList(
                new Engagement(
                        "careProviderHsaId",
                        "careUnitHsaId",
                        "employeeHsaId",
                        Engagement.InformationType.OTHR
                )
        );

        ctx = new PdlContext(
                "careProviderHsaId",
                "careUnitHsaId",
                "employeeHsaId"
        );

        pe = new PatientWithEngagements(
                "patientId",
                "Kalle Karlsson",
                engagements);
    }

    @Test
    public void reportHasBlocks() throws Exception {

        when(blocksInterface.checkBlocks(eq(ctx.careProviderHsaId), isA(CheckBlocksRequestType.class))).
                thenAnswer(BlockingSpec.blockingRequestAndRespond(ctx, pe, 0, true));

        when(consentInterface.checkConsent(anyString(), isA(CheckConsentRequestType.class))).
                thenReturn(ConsentSpec.consentResult(false, false));  // Not under test, avoid null pointer

        when(relationshipInterface.checkPatientRelation(anyString(), isA(CheckPatientRelationRequestType.class))).
            thenReturn(RelationshipSpec.relationshipResult(false));  // Not under test, avoid null pointer

        PdlReport pdlReport = service.pdlReport(ctx, pe);

        assertTrue(pdlReport.hasBlocks);
        assertEquals(1, pdlReport.blocks.size());
    }

    @Test
    public void reportWithoutBlocks() throws Exception {
        when(blocksInterface.checkBlocks(eq(ctx.careProviderHsaId), isA(CheckBlocksRequestType.class))).
                thenReturn(BlockingSpec.blockedResult(0, false));

        when(consentInterface.checkConsent(anyString(), isA(CheckConsentRequestType.class))).
                thenReturn(ConsentSpec.consentResult(false, false)); // Not under test, avoid null pointer

        when(relationshipInterface.checkPatientRelation(anyString(), isA(CheckPatientRelationRequestType.class))).
                thenReturn(RelationshipSpec.relationshipResult(false));  // Not under test, avoid null pointer

        PdlReport pdlReport = service.pdlReport(ctx, pe);

        assertFalse(pdlReport.hasBlocks);
        assertEquals(1, pdlReport.blocks.size());
    }


    @Test
    public void reportHasConsent() throws Exception {
        when(blocksInterface.checkBlocks(anyString(), isA(CheckBlocksRequestType.class))).
                thenReturn(BlockingSpec.blockedResult(0, true)); // Not under test, avoid null pointer

        when(consentInterface.checkConsent(eq(ctx.careProviderHsaId), isA(CheckConsentRequestType.class))).
                thenAnswer(ConsentSpec.consentRequestAndRespond(ctx, pe, true, false));

        when(relationshipInterface.checkPatientRelation(anyString(), isA(CheckPatientRelationRequestType.class))).
                thenReturn(RelationshipSpec.relationshipResult(false));  // Not under test, avoid null pointer

        PdlReport pdlReport = service.pdlReport(ctx, pe);

        assertTrue(pdlReport.hasConsent);
        assertEquals(PdlReport.ConsentType.CONSENT, pdlReport.consentType);
    }

    @Test
    public void reportWithEmergencyConsent() throws Exception {
        when(blocksInterface.checkBlocks(anyString(), isA(CheckBlocksRequestType.class))).
                thenReturn(BlockingSpec.blockedResult(0, true)); // Not under test, avoid null pointer

        when(consentInterface.checkConsent(eq(ctx.careProviderHsaId), isA(CheckConsentRequestType.class))).
                thenAnswer(ConsentSpec.consentRequestAndRespond(ctx, pe, true, true));

        when(relationshipInterface.checkPatientRelation(anyString(), isA(CheckPatientRelationRequestType.class))).
                thenReturn(RelationshipSpec.relationshipResult(false));  // Not under test, avoid null pointer

        PdlReport pdlReport = service.pdlReport(ctx, pe);

        assertTrue(pdlReport.hasConsent);
        assertEquals(PdlReport.ConsentType.EMERGENCY, pdlReport.consentType);
    }

    @Test
    public void reportWithoutConsent() throws Exception {
        when(blocksInterface.checkBlocks(anyString(), isA(CheckBlocksRequestType.class))).
                thenReturn(BlockingSpec.blockedResult(0, true)); // Not under test, avoid null pointer

        when(consentInterface.checkConsent(eq(ctx.careProviderHsaId), isA(CheckConsentRequestType.class))).
                thenAnswer(ConsentSpec.consentRequestAndRespond(ctx, pe, false, true));

        when(relationshipInterface.checkPatientRelation(anyString(), isA(CheckPatientRelationRequestType.class))).
                thenReturn(RelationshipSpec.relationshipResult(false));  // Not under test, avoid null pointer

        PdlReport pdlReport = service.pdlReport(ctx, pe);

        assertFalse(pdlReport.hasConsent);
    }

    @Test
    public void reportHasRelationship() throws Exception {
        when(blocksInterface.checkBlocks(anyString(), isA(CheckBlocksRequestType.class))).
                thenReturn(BlockingSpec.blockedResult(0, true)); // Not under test, avoid null pointer

        when(consentInterface.checkConsent(anyString(), isA(CheckConsentRequestType.class))).
                thenReturn(ConsentSpec.consentResult(false, true)); // Not under test, avoid null pointer

        when(relationshipInterface.checkPatientRelation(eq(ctx.careProviderHsaId), isA(CheckPatientRelationRequestType.class))).
                thenAnswer(RelationshipSpec.relationshipRequestAndResponse(ctx, pe, true));

        PdlReport pdlReport = service.pdlReport(ctx, pe);

        assertTrue(pdlReport.hasRelationship);
    }


    @Test
    public void reportNoRelationship() throws Exception {
        when(blocksInterface.checkBlocks(anyString(), isA(CheckBlocksRequestType.class))).
                thenReturn(BlockingSpec.blockedResult(0, true)); // Not under test, avoid null pointer

        when(consentInterface.checkConsent(anyString(), isA(CheckConsentRequestType.class))).
                thenReturn(ConsentSpec.consentResult(false, true)); // Not under test, avoid null pointer

        when(relationshipInterface.checkPatientRelation(eq(ctx.careProviderHsaId), isA(CheckPatientRelationRequestType.class))).
                thenAnswer(RelationshipSpec.relationshipRequestAndResponse(ctx, pe, false));

        PdlReport pdlReport = service.pdlReport(ctx, pe);

        assertFalse(pdlReport.hasRelationship);
    }



}
