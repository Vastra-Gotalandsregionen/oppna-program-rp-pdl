package se.vgregion.service.pdl;


import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import se.riv.ehr.patientrelationship.accesscontrol.checkpatientrelationresponder.v1.CheckPatientRelationRequestType;
import se.riv.ehr.patientrelationship.accesscontrol.checkpatientrelationresponder.v1.CheckPatientRelationResponseType;
import se.riv.ehr.patientrelationship.v1.AccessingActorType;
import se.riv.ehr.patientrelationship.v1.CheckResultType;
import se.vgregion.domain.pdl.PdlContext;

import static org.junit.Assert.assertEquals;

public class RelationshipSpec {

    private RelationshipSpec() {
        // Utility class, no constructor!
    }

    public static CheckPatientRelationResponseType relationshipResult(boolean hasRelationship) {
        CheckPatientRelationResponseType response = new CheckPatientRelationResponseType();
        CheckResultType result = new CheckResultType();
        result.setHasPatientrelation(hasRelationship);
        response.setCheckResultType(result);
        return response;
    }

    public static Answer<CheckPatientRelationResponseType> relationshipRequestAndResponse(final PdlContext ctx, final boolean hasRelationship) {
        return new Answer<CheckPatientRelationResponseType>() {
            @Override
            public CheckPatientRelationResponseType answer(InvocationOnMock invocationOnMock) throws Throwable {
                CheckPatientRelationRequestType arg2 = (CheckPatientRelationRequestType) (invocationOnMock.getArguments()[1]);

                assertEquals(ctx.patientHsaId, arg2.getPatientId());
                AccessingActorType actor = arg2.getAccessingActor();
                assertEquals(ctx.careProviderHsaId, actor.getCareProviderId());
                assertEquals(ctx.careUnitHsaId, actor.getCareUnitId());
                assertEquals(ctx.employeeHsaId, actor.getEmployeeId());

                return relationshipResult(hasRelationship);
            }
        };
    }
}
