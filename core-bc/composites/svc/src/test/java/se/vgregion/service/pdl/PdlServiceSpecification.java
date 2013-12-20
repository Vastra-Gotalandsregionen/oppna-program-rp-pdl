package se.vgregion.service.pdl;


import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import se.riv.ehr.blocking.accesscontrol.checkblocks.v2.rivtabp21.CheckBlocksResponderInterface;
import se.riv.ehr.blocking.accesscontrol.checkblocksresponder.v2.CheckBlocksRequestType;
import se.riv.ehr.patientconsent.accesscontrol.checkconsent.v1.rivtabp21.CheckConsentResponderInterface;
import se.riv.ehr.patientconsent.accesscontrol.checkconsentresponder.v1.CheckConsentRequestType;
import se.riv.ehr.patientconsent.administration.registerextendedconsent.v1.rivtabp21.RegisterExtendedConsentResponderInterface;
import se.riv.ehr.patientconsent.administration.registerextendedconsentresponder.v1.RegisterExtendedConsentRequestType;
import se.riv.ehr.patientrelationship.accesscontrol.checkpatientrelation.v1.rivtabp21.CheckPatientRelationResponderInterface;
import se.riv.ehr.patientrelationship.accesscontrol.checkpatientrelationresponder.v1.CheckPatientRelationRequestType;
import se.riv.ehr.patientrelationship.administration.registerextendedpatientrelation.v1.rivtabp21.RegisterExtendedPatientRelationResponderInterface;
import se.riv.ehr.patientrelationship.administration.registerextendedpatientrelationresponder.v1.RegisterExtendedPatientRelationRequestType;
import se.vgregion.domain.decorators.WithBlock;
import se.vgregion.domain.decorators.WithInfoType;
import se.vgregion.domain.decorators.WithOutcome;
import se.vgregion.domain.pdl.*;
import se.vgregion.service.MockContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PdlServiceSpecification {

    @Mock
    private CheckBlocksResponderInterface blocksInterface;
    @Mock
    private CheckConsentResponderInterface consentInterface;
    @Mock
    private CheckPatientRelationResponderInterface relationshipInterface;
    @Mock
    private RegisterExtendedPatientRelationResponderInterface establishRelationship;
    @Mock
    private RegisterExtendedConsentResponderInterface establishConsent;

    @InjectMocks
    private PdlServiceImpl service = new PdlServiceImpl();

    private PdlContext ctx;
    private Patient patient;
    private List<WithInfoType<CareSystem>> careSystems;

    private static final String serviceHsaId = "SE2321000131-E000000000001";
    private String otherProvider;
    private String sameProvider;

    @Before
    public void setuUp() {

        ExecutorService executorService = Executors.newFixedThreadPool(3);

        service.setServicesHsaId(serviceHsaId);
        service.setExecutorService(executorService);

        MockitoAnnotations.initMocks(this);

        ctx = MockContext.getMockContext();

        PatientRepository patients = new KivPatientRepository();
        patient = patients.byPatientId("test");

        CareSystems systems = new CareSystemsMock();
        careSystems = systems.byPatientId(ctx, patient.patientId);
    }

    @Test
    public void reportHasBlocks() throws Exception {
        PdlContext newCtx = ctx.changeAssignment(MockContext.VE);

        when(blocksInterface.checkBlocks(eq(serviceHsaId), isA(CheckBlocksRequestType.class))).
                thenAnswer(BlockingSpec.blockingRequestAndRespond(newCtx, patient, 0, true));

        when(relationshipInterface.checkPatientRelation(anyString(), isA(CheckPatientRelationRequestType.class))).
            thenReturn(RelationshipSpec.relationshipResult(false));  // Not under test, avoid null pointer


        PdlReport pdlReport = service.pdlReport(newCtx, patient, careSystems);

        // Should not be a fallback result
        assertEquals(Outcome.SUCCESS, pdlReport.systems.outcome);

        // Should at least contain more than one system
        assertTrue(pdlReport.systems.value.size() > 0);

        // Should contain at least one blocked system
        int b = 0;
        for( WithInfoType<WithBlock<CareSystem>> system : pdlReport.systems.value ) {
            if(system.value.blocked) b++;
        }
        assertTrue(b > 0);
    }

    @Test
    public void reportWithoutBlocks() throws Exception {
        when(blocksInterface.checkBlocks(eq(serviceHsaId), isA(CheckBlocksRequestType.class))).
                thenReturn(BlockingSpec.blockedResult(0, false));

        when(relationshipInterface.checkPatientRelation(anyString(), isA(CheckPatientRelationRequestType.class))).
                thenReturn(RelationshipSpec.relationshipResult(false));  // Not under test, avoid null pointer

        PdlContext newCtx = ctx.changeAssignment(MockContext.VE);

        PdlReport pdlReport = service.pdlReport(newCtx, patient, careSystems);

        // Should at least contain more than one system
        assertTrue(pdlReport.systems.value.size() > 0);

        // Should contain at least one blocked system
        int b = 0;
        for( WithInfoType<WithBlock<CareSystem>> system : pdlReport.systems.value ) {
            if(system.value.blocked) b++;
        }
        assertTrue(b == 0);
    }


    @Test
    public void reportHasConsent() throws Exception {
        when(blocksInterface.checkBlocks(anyString(), isA(CheckBlocksRequestType.class))).
                thenReturn(BlockingSpec.blockedResult(0, true)); // Not under test, avoid null pointer

        when(consentInterface.checkConsent(eq(serviceHsaId), isA(CheckConsentRequestType.class))).
                thenAnswer(ConsentSpec.queryRequestAndRespond(ctx, patient, true, false));

        when(relationshipInterface.checkPatientRelation(anyString(), isA(CheckPatientRelationRequestType.class))).
                thenReturn(RelationshipSpec.relationshipResult(false));  // Not under test, avoid null pointer

        PdlContext newCtx = ctx.changeAssignment(MockContext.SJF);

        PdlReport pdlReport = service.pdlReport(newCtx, patient, careSystems);

        assertEquals(Outcome.SUCCESS, pdlReport.consent.outcome);
        assertTrue(pdlReport.consent.value.hasConsent);
        assertEquals(PdlReport.ConsentType.Consent, pdlReport.consent.value.consentType);
    }

    @Test
    public void reportWithEmergencyConsent() throws Exception {
        when(blocksInterface.checkBlocks(anyString(), isA(CheckBlocksRequestType.class))).
                thenReturn(BlockingSpec.blockedResult(0, true)); // Not under test, avoid null pointer

        when(consentInterface.checkConsent(eq(serviceHsaId), isA(CheckConsentRequestType.class))).
                thenAnswer(ConsentSpec.queryRequestAndRespond(ctx, patient, true, true));

        when(relationshipInterface.checkPatientRelation(anyString(), isA(CheckPatientRelationRequestType.class))).
                thenReturn(RelationshipSpec.relationshipResult(false));  // Not under test, avoid null pointer

        PdlContext newCtx = ctx.changeAssignment(MockContext.SJF);

        PdlReport pdlReport = service.pdlReport(newCtx, patient, careSystems);

        assertEquals(Outcome.SUCCESS, pdlReport.consent.outcome);
        assertTrue(pdlReport.consent.value.hasConsent);
        assertEquals(PdlReport.ConsentType.Emergency, pdlReport.consent.value.consentType);
    }

    @Test
    public void reportWithoutConsent() throws Exception {
        when(blocksInterface.checkBlocks(anyString(), isA(CheckBlocksRequestType.class))).
                thenReturn(BlockingSpec.blockedResult(0, true)); // Not under test, avoid null pointer

        when(consentInterface.checkConsent(eq(serviceHsaId), isA(CheckConsentRequestType.class))).
                thenAnswer(ConsentSpec.queryRequestAndRespond(ctx, patient, false, true));

        when(relationshipInterface.checkPatientRelation(anyString(), isA(CheckPatientRelationRequestType.class))).
                thenReturn(RelationshipSpec.relationshipResult(false));  // Not under test, avoid null pointer

        PdlContext newCtx = ctx.changeAssignment(MockContext.SJF);

        PdlReport pdlReport = service.pdlReport(newCtx, patient, careSystems);

        assertEquals(Outcome.SUCCESS, pdlReport.consent.outcome);
        assertFalse(pdlReport.consent.value.hasConsent);
    }

    @Test
    public void reportHasRelationship() throws Exception {
        PdlContext newCtx = ctx.changeAssignment(MockContext.VE);

        when(blocksInterface.checkBlocks(anyString(), isA(CheckBlocksRequestType.class))).
                thenReturn(BlockingSpec.blockedResult(0, true)); // Not under test, avoid null pointer

        when(relationshipInterface.checkPatientRelation(eq(serviceHsaId), isA(CheckPatientRelationRequestType.class))).
                thenAnswer(RelationshipSpec.queryRequestAndResponse(newCtx, patient, true));

        PdlReport pdlReport = service.pdlReport(newCtx, patient, careSystems);

        assertEquals(Outcome.SUCCESS, pdlReport.hasRelationship.outcome);
        assertTrue(pdlReport.hasRelationship.value);
    }


    @Test
    public void reportNoRelationship() throws Exception {
        PdlContext newCtx = ctx.changeAssignment(MockContext.VE);

        when(blocksInterface.checkBlocks(anyString(), isA(CheckBlocksRequestType.class))).
                thenReturn(BlockingSpec.blockedResult(0, true)); // Not under test, avoid null pointer

        when(relationshipInterface.checkPatientRelation(eq(serviceHsaId), isA(CheckPatientRelationRequestType.class))).
                thenAnswer(RelationshipSpec.queryRequestAndResponse(newCtx, patient, false));

        PdlReport pdlReport = service.pdlReport(newCtx, patient, careSystems);

        assertEquals(Outcome.SUCCESS, pdlReport.hasRelationship.outcome);
        assertFalse(pdlReport.hasRelationship.value);
    }


    @Test
    public void establishRelationship() throws Exception {
        when(establishRelationship.registerExtendedPatientRelation(eq(serviceHsaId), isA(RegisterExtendedPatientRelationRequestType.class)))
                .thenAnswer(RelationshipSpec.establishRequestAndResponse(ctx, patient, true));

        WithOutcome<ArrayList<WithInfoType<WithBlock<CareSystem>>>> systems =
                WithOutcome.success(new ArrayList<WithInfoType<WithBlock<CareSystem>>>());

        WithOutcome<CheckedConsent> consent =
                WithOutcome.success(new CheckedConsent(PdlReport.ConsentType.Consent, false));

        WithOutcome<Boolean> relationship =
                WithOutcome.commFailure(false);

        PdlReport mockReport = new PdlReport(
                systems,
                consent,
                relationship
        );

        PdlReport newReport = service.patientRelationship(
                ctx,
                mockReport,
                patient.patientId,
                "Some reason",
                1,
                RoundedTimeUnit.NEAREST_HOUR
        );

        assertTrue(newReport.getHasRelationship().value);
    }

    @Test
    public void establishConsent() throws Exception {
        PdlReport.ConsentType consentType = PdlReport.ConsentType.Consent;
        when(establishConsent.registerExtendedConsent(eq(serviceHsaId), isA(RegisterExtendedConsentRequestType.class)))
                .thenAnswer(ConsentSpec.establishRequestAndResponse(ctx, patient,consentType,true));

        WithOutcome<ArrayList<WithInfoType<WithBlock<CareSystem>>>> systems =
                WithOutcome.success(new ArrayList<WithInfoType<WithBlock<CareSystem>>>());

        WithOutcome<CheckedConsent> consent =
                WithOutcome.success(new CheckedConsent(PdlReport.ConsentType.Consent, false));

        WithOutcome<Boolean> relationship =
                WithOutcome.commFailure(false);

        PdlReport mockReport = new PdlReport(
                systems,
                consent,
                relationship
        );

        PdlReport newReport = service.patientConsent(
                ctx,
                mockReport,
                patient.patientId,
                "Some reason",
                1,
                RoundedTimeUnit.NEAREST_HOUR,
                consentType
        );

        assertTrue(newReport.consent.value.hasConsent);
    }


}
