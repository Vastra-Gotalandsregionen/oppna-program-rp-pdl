package se.vgregion.service.pdl;


import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import se.riv.ehr.patientrelationship.accesscontrol.checkpatientrelationresponder.v1.CheckPatientRelationRequestType;
import se.riv.ehr.patientrelationship.accesscontrol.checkpatientrelationresponder.v1.CheckPatientRelationResponseType;
import se.riv.ehr.patientrelationship.administration.registerextendedpatientrelationresponder.v1.RegisterExtendedPatientRelationRequestType;
import se.riv.ehr.patientrelationship.administration.registerextendedpatientrelationresponder.v1.RegisterExtendedPatientRelationResponseType;
import se.riv.ehr.patientrelationship.v1.AccessingActorType;
import se.riv.ehr.patientrelationship.v1.CheckResultType;
import se.riv.ehr.patientrelationship.v1.ResultCodeType;
import se.riv.ehr.patientrelationship.v1.ResultType;
import se.vgregion.domain.pdl.Patient;
import se.vgregion.domain.pdl.PdlContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RelationshipSpec {

    private RelationshipSpec() {
        // Utility class, no constructor!
    }

    public static CheckPatientRelationResponseType relationshipResult(boolean hasRelationship) {
        CheckPatientRelationResponseType response = new CheckPatientRelationResponseType();
        CheckResultType result = new CheckResultType();

        ResultType r = new ResultType();
        r.setResultCode(ResultCodeType.OK);

        result.setResult(r);
        result.setHasPatientrelation(hasRelationship);
        response.setCheckResultType(result);
        return response;
    }

    public static Answer<CheckPatientRelationResponseType> queryRequestAndResponse(
            final PdlContext ctx,
            final Patient pe,
            final boolean hasRelationship
    ) {
        return new Answer<CheckPatientRelationResponseType>() {
            @Override
            public CheckPatientRelationResponseType answer(InvocationOnMock invocationOnMock) throws Throwable {
                CheckPatientRelationRequestType req = (CheckPatientRelationRequestType) (invocationOnMock.getArguments()[1]);

                assertEquals(pe.patientId, req.getPatientId());
                AccessingActorType actor = req.getAccessingActor();
                assertEquals(ctx.currentAssignment.careProviderHsaId, actor.getCareProviderId());
                assertEquals(ctx.currentAssignment.careUnitHsaId, actor.getCareUnitId());
                assertEquals(ctx.employeeHsaId, actor.getEmployeeId());

                return relationshipResult(hasRelationship);
            }
        };
    }

    private static RegisterExtendedPatientRelationResponseType establishResult(boolean success) {
        RegisterExtendedPatientRelationResponseType resp = new RegisterExtendedPatientRelationResponseType();
        ResultType result = new ResultType();
        if(success) {
            result.setResultCode(ResultCodeType.OK);
        } else {
            result.setResultCode(ResultCodeType.ERROR);
        }
        resp.setResultType(result);

        return resp;
    }

    public static Answer<RegisterExtendedPatientRelationResponseType> establishRequestAndResponse(final PdlContext ctx, final Patient pe, final boolean success) {

        return new Answer<RegisterExtendedPatientRelationResponseType>() {
            @Override
            public RegisterExtendedPatientRelationResponseType answer(InvocationOnMock invocationOnMock) throws Throwable {
                RegisterExtendedPatientRelationRequestType req = (RegisterExtendedPatientRelationRequestType) (invocationOnMock.getArguments()[1]);

                assertEquals(pe.getPatientId(), req.getPatientId());
                assertEquals(ctx.currentAssignment.careProviderHsaId, req.getCareProviderId());
                assertEquals(ctx.currentAssignment.careUnitHsaId, req.getCareUnitId());
                assertEquals(ctx.employeeHsaId, req.getEmployeeId());
                assertNotNull(req.getRegistrationAction().getRegistrationDate());
                assertNotNull(req.getEndDate());
                assertNotNull(req.getPatientRelationId());
                assertEquals(ctx.employeeHsaId ,req.getRegistrationAction().getRegisteredBy().getEmployeeId());
                assertEquals(ctx.employeeHsaId ,req.getRegistrationAction().getRequestedBy().getEmployeeId());

                return establishResult(success);
            }
        };
    }
}
