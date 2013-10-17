package se.vgregion.service.pdl;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import riv.ehr.blocking.accesscontrol._3.AccessingActorType;
import riv.ehr.blocking.accesscontrol._3.CheckBlocksResultType;
import riv.ehr.blocking.accesscontrol._3.CheckResultType;
import riv.ehr.blocking.accesscontrol._3.CheckStatusType;
import se.riv.ehr.blocking.accesscontrol.checkblocksresponder.v3.CheckBlocksRequestType;
import se.riv.ehr.blocking.accesscontrol.checkblocksresponder.v3.CheckBlocksResponseType;
import se.vgregion.domain.pdl.PatientWithEngagements;
import se.vgregion.domain.pdl.PdlContext;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class BlockingSpec {

    private BlockingSpec(){
        // Utility class, no constructor!
    }

    static CheckBlocksResponseType blockedResult(int rowNumber, boolean isBlocked) {
        CheckBlocksResponseType response = new CheckBlocksResponseType();

        CheckBlocksResultType result = new riv.ehr.blocking.accesscontrol._3.CheckBlocksResultType();
        List<CheckResultType> results = result.getCheckResults();

        CheckResultType check = new CheckResultType();
        CheckStatusType blockStatus = (isBlocked) ? CheckStatusType.BLOCKED : CheckStatusType.OK;
        check.setStatus(blockStatus);
        check.setRowNumber(rowNumber);
        results.add(check);
        response.setCheckBlocksResultType(result);

        return response;
    }


    static Answer<CheckBlocksResponseType> blockingRequestAndRespond(
            final PdlContext ctx,
            final PatientWithEngagements pe,
            final int rowNumber,
            final boolean isBlocked
    ) {
        return new Answer<CheckBlocksResponseType>() {
            @Override
            public CheckBlocksResponseType answer(InvocationOnMock invocationOnMock) throws Throwable {
                CheckBlocksRequestType arg2 = (CheckBlocksRequestType) (invocationOnMock.getArguments()[1]);
                AccessingActorType actor = arg2.getAccessingActor();
                assertEquals(pe.patientId, arg2.getPatientId());
                assertEquals(ctx.careProviderHsaId, actor.getCareProviderId());
                assertEquals(ctx.careUnitHsaId, actor.getCareUnitId());
                assertEquals(ctx.employeeHsaId, actor.getEmployeeId());

                return blockedResult(rowNumber, isBlocked);
            }
        };
    }
}
