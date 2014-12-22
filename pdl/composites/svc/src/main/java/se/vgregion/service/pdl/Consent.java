package se.vgregion.service.pdl;

import org.apache.cxf.binding.soap.SoapFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.riv.ehr.patientconsent.accesscontrol.checkconsentresponder.v1.CheckConsentRequestType;
import se.riv.ehr.patientconsent.accesscontrol.checkconsentresponder.v1.CheckConsentResponseType;
import se.riv.ehr.patientconsent.administration.registerextendedconsent.v1.rivtabp21.RegisterExtendedConsentResponderInterface;
import se.riv.ehr.patientconsent.administration.registerextendedconsentresponder.v1.RegisterExtendedConsentRequestType;
import se.riv.ehr.patientconsent.administration.registerextendedconsentresponder.v1.RegisterExtendedConsentResponseType;
import se.riv.ehr.patientconsent.querying.getconsentsforpatientresponder.v1.GetConsentsForPatientRequestType;
import se.riv.ehr.patientconsent.querying.getconsentsforpatientresponder.v1.GetConsentsForPatientResponseType;
import se.riv.ehr.patientconsent.v1.*;
import se.riv.ehr.patientrelationship.accesscontrol.checkpatientrelationresponder.v1.CheckPatientRelationRequestType;
import se.vgregion.domain.decorators.WithOutcome;
import se.vgregion.domain.pdl.CheckedConsent;
import se.vgregion.domain.pdl.PdlContext;
import se.vgregion.domain.pdl.PdlReport;
import se.vgregion.domain.pdl.RoundedTimeUnit;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;
import java.io.Serializable;
import java.util.List;

class Consent {
    private static final Logger LOGGER = LoggerFactory.getLogger(Consent.class.getName());

    private Consent() {
        // Utility class, no constructor!
    }

    static GetConsentsForPatientRequestType getConsentsForPatientRequest(PdlContext ctx, String patientId) {

        GetConsentsForPatientRequestType request = new GetConsentsForPatientRequestType();

        request.setPatientId(patientId);
        request.setCareProviderId(ctx.currentAssignment.careProviderHsaId);

        return request;
    }

    static CheckConsentRequestType checkConsentRequest(PdlContext ctx, String patientId) {

        CheckConsentRequestType request = new CheckConsentRequestType();
        AccessingActorType actor = new AccessingActorType();
        actor.setCareProviderId(ctx.currentAssignment.careProviderHsaId);
        actor.setCareUnitId(ctx.currentAssignment.careUnitHsaId);
        actor.setEmployeeId(ctx.employeeHsaId);
        request.setPatientId(patientId);
        request.setAccessingActor(actor);
        return request;
    }


    public static CheckedConsent asCheckedConsent(CheckConsentResponseType consentResponse) {
        boolean hasConsent = consentResponse.getCheckResultType().isHasConsent();


        PdlReport.ConsentType consentType = (
                consentResponse.
                        getCheckResultType().
                        getAssertionType() == AssertionTypeType.CONSENT) ?
                PdlReport.ConsentType.Consent : PdlReport.ConsentType.Emergency;

        return new CheckedConsent( consentType, hasConsent );
    }

    /**
     * Checks whether at least one consent (PdlAssertion) exists. If at least one consent is of type Consent, as opposed
     * to Emergency, the Consent type is chosen.
     *
     * @param getConsentsForPatientResponse
     * @return
     */
    public static CheckedConsent asCheckedConsent(GetConsentsForPatientResponseType getConsentsForPatientResponse) {
        List<PDLAssertionType> pdlAssertions = getConsentsForPatientResponse.getGetConsentsResultType()
                .getPdlAssertions();

        boolean hasConsent = pdlAssertions != null && pdlAssertions.size() > 0;

        PdlReport.ConsentType consentType = PdlReport.ConsentType.None;

        // This loop will make consentType equal to ConsentType.Consent if any pdlAssertion is of type Consent.
        for (PDLAssertionType assertion : pdlAssertions) {
            if (consentType.equals(PdlReport.ConsentType.None) || consentType.equals(PdlReport.ConsentType.Emergency)) {
                consentType = PdlReport.ConsentType.valueOf(assertion.getAssertionType().value());
            }
        }

        return new CheckedConsent(consentType, hasConsent);
    }


    public static CheckPatientRelationRequestType checkRelationshipRequest(PdlContext ctx) {

        return new CheckPatientRelationRequestType();
    }

    public static WithOutcome<CheckedConsent> establishConsentWithFallback(
            String servicesHsaId,
            RegisterExtendedConsentResponderInterface establishConsent,
            PdlContext ctx,
            String patientId,
            PdlReport.ConsentType consentType,
            String reason,
            int duration,
            RoundedTimeUnit roundedTimeUnit
    ) {
        WithOutcome<CheckedConsent> consent;
        try {
            consent = establishConsent(
                servicesHsaId,
                establishConsent,
                ctx,
                patientId,
                consentType,
                reason,
                duration,
                roundedTimeUnit
            );
        } catch (WebServiceException e) {
            LOGGER.error("Failed to establish relationship for patientId {}. Using fallback response.", patientId, e);
            consent = WithOutcome.commFailure(new CheckedConsent(consentType, true));
        } catch (SoapFault e) {
            LOGGER.error("Failed to establish relationship for patientId {}. Using fallback response.", patientId, e);
            consent = WithOutcome.commFailure(new CheckedConsent(consentType, true));
        }

        return consent;
    }

    private static WithOutcome<CheckedConsent> establishConsent(
            String servicesHsaId,
            RegisterExtendedConsentResponderInterface establishConsent,
            PdlContext ctx,
            String patientId,
            PdlReport.ConsentType consentType,
            String reason,
            int duration,
            RoundedTimeUnit roundedTimeUnit
    ) {
        RegisterExtendedConsentRequestType request = new RegisterExtendedConsentRequestType();
        request.setAssertionId(java.util.UUID.randomUUID().toString());
        request.setAssertionType(AssertionTypeType.fromValue(consentType.name()));
        request.setCareProviderId(ctx.currentAssignment.careProviderHsaId);
        request.setEmployeeId(null); // Don't specify, we only wish to specify as far as careProvider...
        request.setCareUnitId(ctx.currentAssignment.getCareUnitHsaId()); //... but currently the web service requires this too

        XMLDuration xmlDuration = new XMLDuration(duration, roundedTimeUnit);
        request.setStartDate(xmlDuration.startDate);
        request.setEndDate(xmlDuration.endDate);
        request.setPatientId(patientId);
        request.setScope(ScopeType.NATIONAL_LEVEL);

        ActionType action = new ActionType();
        action.setReasonText(reason);
        action.setRegistrationDate(XMLDuration.currentDateAsXML());
        action.setRequestDate(XMLDuration.currentDateAsXML());

        ActorType actor = new ActorType();
        actor.setAssignmentId(ctx.currentAssignment.getAssignmentHsaId());
        actor.setAssignmentName(ctx.currentAssignment.getAssignmentDisplayName());
        actor.setEmployeeId(ctx.employeeHsaId);

        action.setRegisteredBy(actor);
        action.setRequestedBy(actor);
        request.setRegistrationAction(action);

        try {
            RegisterExtendedConsentResponseType response =
                    establishConsent
                            .registerExtendedConsent(servicesHsaId, request);

            if(response.getResultType().getResultCode() == ResultCodeType.OK) {
                LOGGER.trace("Consent established for patient {}", patientId);
                return WithOutcome.success(new CheckedConsent(consentType, true));

            } else {
                switch (response.getResultType().getResultCode()) {
                    case VALIDATION_ERROR :
                        LOGGER.error(
                                "Validation error for consent. Message: {}",
                                response.getResultType().getResultText()
                        );
                        return WithOutcome.clientError(new CheckedConsent(consentType, true));
                    case ERROR:
                        LOGGER.error(
                                "Error when establishing consent. Message: {}",
                                response.getResultType().getResultText()
                        );
                        return WithOutcome.remoteFailure(new CheckedConsent(consentType, true));
                    case ACCESSDENIED:
                        LOGGER.error(
                                "AccessWithScope denied when establishing consent. Message: {}",
                                response.getResultType().getResultText()
                        );
                        return WithOutcome.commFailure(new CheckedConsent(consentType, true));
                    case ALREADYEXISTS:
                        LOGGER.error(
                                "Duplicate ids when establishing consent. Message: {}",
                                response.getResultType().getResultText()
                        );
                        return WithOutcome.clientError(new CheckedConsent(consentType, true));
                    case INVALIDSTATE:
                        LOGGER.error(
                                "Invalid state when establishing consent. Message: {}",
                                response.getResultType().getResultText()
                        );
                    default:
                        LOGGER.error(
                                "Unknown error when establishing consent. Message: {}",
                                response.getResultType().getResultText()
                        );
                        return WithOutcome.remoteFailure(new CheckedConsent(consentType, true));
                }
            }
        } catch(SOAPFaultException e) {
            LOGGER.error("Could not contact Consent service. Using fallback.", e);
            return WithOutcome.commFailure(new CheckedConsent(consentType, true));
        }

    }

    public static <T extends Serializable> WithOutcome<T> decideOutcome(
            ResultType result,
            T value
    ) {

        ResultCodeType resultCode = result.getResultCode();
        String resultText = result.getResultText();

        if(ResultCodeType.OK == resultCode) {
            return WithOutcome.success(value);
        } else {
            LOGGER.error(
                    "Patient consent service returned something else than 'OK'. Continuing anyways. \nResult code was {}. \nMessage was {}.",
                    resultCode,
                    resultText
            );

            if(ResultCodeType.VALIDATION_ERROR == resultCode) {
                return WithOutcome.clientError(value);
            } else {
                return WithOutcome.remoteFailure(value);
            }
        }
    }
}
