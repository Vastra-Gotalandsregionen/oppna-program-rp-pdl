package se.vgregion.service.pdl;


import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import se.riv.ehr.patientconsent.accesscontrol.checkconsentresponder.v1.CheckConsentRequestType;
import se.riv.ehr.patientconsent.accesscontrol.checkconsentresponder.v1.CheckConsentResponseType;
import se.riv.ehr.patientconsent.administration.registerextendedconsentresponder.v1.RegisterExtendedConsentRequestType;
import se.riv.ehr.patientconsent.administration.registerextendedconsentresponder.v1.RegisterExtendedConsentResponseType;
import se.riv.ehr.patientconsent.querying.getconsentsforpatientresponder.v1.GetConsentsForPatientRequestType;
import se.riv.ehr.patientconsent.querying.getconsentsforpatientresponder.v1.GetConsentsForPatientResponseType;
import se.riv.ehr.patientconsent.v1.AccessingActorType;
import se.riv.ehr.patientconsent.v1.AssertionTypeType;
import se.riv.ehr.patientconsent.v1.GetConsentsResultType;
import se.riv.ehr.patientconsent.v1.PDLAssertionType;
import se.riv.ehr.patientconsent.v1.ResultCodeType;
import se.riv.ehr.patientconsent.v1.ResultType;
import se.vgregion.events.context.Patient;
import se.vgregion.domain.pdl.PdlContext;
import se.vgregion.domain.pdl.PdlReport;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

public class ConsentSpec {

    private ConsentSpec() {
        // Utility class, no constructor!
    }

    static GetConsentsForPatientResponseType queryResultGetConsentsForPatient(boolean hasConsent, boolean emergency) {
        GetConsentsForPatientResponseType response = new GetConsentsForPatientResponseType();

        AssertionTypeType consentType = (emergency) ? AssertionTypeType.EMERGENCY : AssertionTypeType.CONSENT;

        ResultType result = new ResultType();
        result.setResultCode(ResultCodeType.OK);
        result.setResultText("All OK.");

        GetConsentsResultType getConsentsResultType = new GetConsentsResultType();

        getConsentsResultType.setResult(result);

        if (hasConsent) {
            PDLAssertionType pdlAssertionType = new PDLAssertionType();
            pdlAssertionType.setAssertionType(consentType);

            getConsentsResultType.getPdlAssertions().add(pdlAssertionType);
        }

        response.setGetConsentsResultType(getConsentsResultType);

        return response;
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
            final Patient pe,
            final boolean hasConsent,
            final boolean emergency
    ) {
        return new Answer<CheckConsentResponseType>() {
            @Override
            public CheckConsentResponseType answer(InvocationOnMock invocationOnMock) throws Throwable {
                CheckConsentRequestType req = (CheckConsentRequestType) (invocationOnMock.getArguments()[1]);

                assertEquals(pe.patientId, req.getPatientId());
                AccessingActorType actor = req.getAccessingActor();
                assertEquals(ctx.currentAssignment.careProviderHsaId, actor.getCareProviderId());
                assertEquals(ctx.currentAssignment.careUnitHsaId, actor.getCareUnitId());
                assertEquals(ctx.employeeHsaId, actor.getEmployeeId());

                CheckConsentResponseType response = queryResult(hasConsent, emergency);

                return response;
            }
        };
    }

    static Answer<GetConsentsForPatientResponseType> queryGetConsentsForPatientRequestAndRespond(
            final PdlContext ctx,
            final Patient pe,
            final boolean hasConsent,
            final boolean emergency
    ) {
        return new Answer<GetConsentsForPatientResponseType>() {
            @Override
            public GetConsentsForPatientResponseType answer(InvocationOnMock invocationOnMock) throws Throwable {
                GetConsentsForPatientRequestType req = (GetConsentsForPatientRequestType) (invocationOnMock.getArguments()[1]);

                assertEquals(pe.patientId, req.getPatientId());
                assertEquals(ctx.currentAssignment.careProviderHsaId, req.getCareProviderId());

                GetConsentsForPatientResponseType response = queryResultGetConsentsForPatient(hasConsent, emergency);

                return response;
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
            final Patient pe,
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
                assertEquals(ctx.currentAssignment.careProviderHsaId, req.getCareProviderId());
                assertEquals(ctx.currentAssignment.careUnitHsaId, req.getCareUnitId());
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
