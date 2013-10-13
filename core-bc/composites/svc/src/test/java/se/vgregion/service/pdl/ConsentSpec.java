package se.vgregion.service.pdl;


import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import se.riv.ehr.patientconsent.accesscontrol.checkconsentresponder.v1.CheckConsentRequestType;
import se.riv.ehr.patientconsent.accesscontrol.checkconsentresponder.v1.CheckConsentResponseType;
import se.riv.ehr.patientconsent.v1.AccessingActorType;
import se.riv.ehr.patientconsent.v1.AssertionTypeType;
import se.riv.ehr.patientconsent.v1.ResultCodeType;
import se.riv.ehr.patientconsent.v1.ResultType;
import se.vgregion.domain.pdl.PdlContext;

import static org.junit.Assert.assertEquals;

public class ConsentSpec {

    private ConsentSpec() {
        // Utility class, no constructor!
    }


    static CheckConsentResponseType consentResult(boolean hasConsent, boolean emergency) {
        CheckConsentResponseType response = new CheckConsentResponseType();
        se.riv.ehr.patientconsent.v1.CheckResultType check = new se.riv.ehr.patientconsent.v1.CheckResultType();

        AssertionTypeType consentType = (emergency) ? AssertionTypeType.EMERGENCY : AssertionTypeType.CONSENT;

        check.setHasConsent(hasConsent);
        check.setAssertionType(consentType);

        ResultType result = new ResultType();
        result.setResultCode(ResultCodeType.OK);
        result.setResultText("All OK.");

        check.setResult(result);
        response.setCheckResultType(check);
        return response;
    }

    static Answer<CheckConsentResponseType> consentRequestAndRespond(final PdlContext ctx, final boolean hasConsent, final boolean emergency) {
        return new Answer<CheckConsentResponseType>() {
            @Override
            public CheckConsentResponseType answer(InvocationOnMock invocationOnMock) throws Throwable {
                CheckConsentRequestType arg2 = (CheckConsentRequestType) (invocationOnMock.getArguments()[1]);

                assertEquals(ctx.patientHsaId, arg2.getPatientId());
                AccessingActorType actor = arg2.getAccessingActor();
                assertEquals(ctx.careProviderHsaId, actor.getCareProviderId());
                assertEquals(ctx.careUnitHsaId, actor.getCareUnitId());
                assertEquals(ctx.employeeHsaId, actor.getEmployeeId());

                return consentResult(hasConsent, emergency);
            }
        };
    }
}
