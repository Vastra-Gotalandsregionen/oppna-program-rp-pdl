package se.vgregion.service.pdl;


import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import se.riv.ehr.patientconsent.accesscontrol.checkconsentresponder.v1.CheckConsentRequestType;
import se.riv.ehr.patientconsent.accesscontrol.checkconsentresponder.v1.CheckConsentResponseType;
import se.riv.ehr.patientconsent.administration.registerextendedconsentresponder.v1.RegisterExtendedConsentRequestType;
import se.riv.ehr.patientconsent.administration.registerextendedconsentresponder.v1.RegisterExtendedConsentResponseType;
import se.riv.ehr.patientconsent.v1.AccessingActorType;
import se.riv.ehr.patientconsent.v1.AssertionTypeType;
import se.riv.ehr.patientconsent.v1.ResultCodeType;
import se.riv.ehr.patientconsent.v1.ResultType;
import se.vgregion.domain.pdl.PatientWithEngagements;
import se.vgregion.domain.pdl.PdlContext;
import se.vgregion.domain.pdl.PdlReport;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

public class ConsentSpec {

    private ConsentSpec() {
        // Utility class, no constructor!
    }


    static CheckConsentResponseType queryResult(boolean hasConsent, boolean emergency) {
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

    static Answer<CheckConsentResponseType> queryRequestAndRespond(
            final PdlContext ctx,
            final PatientWithEngagements pe,
            final boolean hasConsent,
            final boolean emergency
    ) {
        return new Answer<CheckConsentResponseType>() {
            @Override
            public CheckConsentResponseType answer(InvocationOnMock invocationOnMock) throws Throwable {
                CheckConsentRequestType req = (CheckConsentRequestType) (invocationOnMock.getArguments()[1]);

                assertEquals(pe.patientId, req.getPatientId());
                AccessingActorType actor = req.getAccessingActor();
                assertEquals(ctx.careProviderHsaId, actor.getCareProviderId());
                assertEquals(ctx.careUnitHsaId, actor.getCareUnitId());
                assertEquals(ctx.employeeHsaId, actor.getEmployeeId());

                return queryResult(hasConsent, emergency);
            }
        };
    }

    private static RegisterExtendedConsentResponseType establishResponse(boolean success) {
        RegisterExtendedConsentResponseType resp = new RegisterExtendedConsentResponseType();
        ResultType result = new ResultType();
        if(success) {
            result.setResultCode(ResultCodeType.OK);
        } else {
            result.setResultCode(ResultCodeType.NOTFOUND);
        }
        resp.setResultType(result);

        return resp;
    }

    public static Answer<RegisterExtendedConsentResponseType> establishRequestAndResponse(
            final PdlContext ctx,
            final PatientWithEngagements pe,
            final PdlReport.ConsentType consentType,
            final boolean success
    ) {
        return new Answer<RegisterExtendedConsentResponseType>() {
            @Override
            public RegisterExtendedConsentResponseType answer(InvocationOnMock invocationOnMock) throws Throwable {
                RegisterExtendedConsentRequestType req =
                        (RegisterExtendedConsentRequestType) (invocationOnMock.getArguments()[1]);

                assertEquals(AssertionTypeType.fromValue(consentType.name()), req.getAssertionType());
                assertEquals(pe.getPatientId(), req.getPatientId());
                assertEquals(ctx.careProviderHsaId, req.getCareProviderId());
                assertEquals(ctx.careUnitHsaId, req.getCareUnitId());
                assertEquals(ctx.employeeHsaId, req.getEmployeeId());
                assertNotNull(req.getRegistrationAction().getRegistrationDate());
                assertNotNull(req.getEndDate());
                assertNotNull(req.getAssertionId());
                assertNotNull(req.getScope());
                assertEquals(ctx.employeeHsaId ,req.getRegistrationAction().getRegisteredBy().getEmployeeId());
                assertEquals(ctx.employeeHsaId ,req.getRegistrationAction().getRequestedBy().getEmployeeId());

                return establishResponse(success);
            }
        };
    }
}
