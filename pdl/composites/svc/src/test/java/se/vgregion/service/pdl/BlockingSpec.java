package se.vgregion.service.pdl;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import se.riv.ehr.blocking.accesscontrol.checkblocksresponder.v2.CheckBlocksRequestType;
import se.riv.ehr.blocking.accesscontrol.checkblocksresponder.v2.CheckBlocksResponseType;
import se.riv.ehr.blocking.v2.*;
import se.vgregion.events.context.Patient;
import se.vgregion.domain.pdl.PdlContext;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class BlockingSpec {

    private BlockingSpec(){
        // Utility class, no constructor!
    }

    static CheckBlocksResponseType blockedResult(int rowNumber, boolean isBlocked) {
        CheckBlocksResponseType response = new CheckBlocksResponseType();

        CheckBlocksResultType result = new CheckBlocksResultType();
        List<CheckResultType> results = result.getCheckResults();

        ResultType r = new ResultType();
        r.setResultCode(ResultCodeType.OK);

        result.setResult(r);
        CheckResultType check = new CheckResultType();
        check.setBlocked(isBlocked);
        check.setRowNumber(rowNumber);
        results.add(check);
        response.setCheckBlocksResultType(result);

        return response;
    }


    static Answer<CheckBlocksResponseType> blockingRequestAndRespond(
            final PdlContext ctx,
            final Patient pe,
            final int rowNumber,
            final boolean isBlocked
    ) {
        return new Answer<CheckBlocksResponseType>() {
            @Override
            public CheckBlocksResponseType answer(InvocationOnMock invocationOnMock) throws Throwable {
                CheckBlocksRequestType arg2 = (CheckBlocksRequestType) (invocationOnMock.getArguments()[1]);
                AccessingActorType actor = arg2.getAccessingActor();
                assertEquals(pe.patientId, arg2.getPatientId());
                assertEquals(ctx.currentAssignment.careProviderHsaId, actor.getCareProviderId());
                assertEquals(ctx.currentAssignment.careUnitHsaId, actor.getCareUnitId());
                assertEquals(ctx.employeeHsaId, actor.getEmployeeId());

                return blockedResult(rowNumber, isBlocked);
            }
        };
    }
}
