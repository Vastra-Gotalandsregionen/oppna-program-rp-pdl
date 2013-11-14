package se.vgregion.service.pdl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.riv.ehr.patientconsent.accesscontrol.checkconsentresponder.v1.CheckConsentRequestType;
import se.riv.ehr.patientconsent.accesscontrol.checkconsentresponder.v1.CheckConsentResponseType;
import se.riv.ehr.patientconsent.administration.registerextendedconsent.v1.rivtabp21.RegisterExtendedConsentResponderInterface;
import se.riv.ehr.patientconsent.administration.registerextendedconsentresponder.v1.RegisterExtendedConsentRequestType;
import se.riv.ehr.patientconsent.administration.registerextendedconsentresponder.v1.RegisterExtendedConsentResponseType;
import se.riv.ehr.patientconsent.v1.*;
import se.riv.ehr.patientrelationship.accesscontrol.checkpatientrelationresponder.v1.CheckPatientRelationRequestType;
import se.vgregion.domain.pdl.*;

import javax.xml.ws.soap.SOAPFaultException;

class Consent {
    private static final Logger LOGGER = LoggerFactory.getLogger(Consent.class.getName());

    private Consent() {
        // Utility class, no constructor!
    }

    static CheckConsentRequestType checkConsentRequest(PdlContext ctx, String patientId) {
        CheckConsentRequestType request = new CheckConsentRequestType();
        AccessingActorType actor = new AccessingActorType();
        actor.setCareProviderId(ctx.careProviderHsaId);
        actor.setCareUnitId(ctx.careUnitHsaId);
        actor.setEmployeeId(ctx.employeeHsaId);
        request.setPatientId(patientId);
        request.setAccessingActor(actor);
        return request;
    }


    public static CheckedConsent asCheckedConsent(CheckConsentResponseType consentResponse) {
        boolean hasConsent =
                consentResponse.getCheckResultType().isHasConsent();


        PdlReport.ConsentType consentType = (
                consentResponse.
                        getCheckResultType().
                        getAssertionType() == AssertionTypeType.CONSENT) ?
                PdlReport.ConsentType.Consent : PdlReport.ConsentType.Emergency;

        return new CheckedConsent( consentType, hasConsent );
    }

    public static CheckPatientRelationRequestType checkRelationshipRequest(PdlContext ctx) {

        return new CheckPatientRelationRequestType();
    }

    public static WithFallback<Boolean> establishConsent(
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
        request.setCareProviderId(ctx.careProviderHsaId);
        request.setCareUnitId(ctx.getCareUnitHsaId());
        request.setEmployeeId(ctx.getEmployeeHsaId());

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
        actor.setAssignmentId(ctx.assignmentHsaId);
        actor.setAssignmentName(ctx.assignmentDisplayName);
        actor.setEmployeeId(ctx.employeeHsaId);

        action.setRegisteredBy(actor);
        action.setRequestedBy(actor);
        request.setRegistrationAction(action);

        try {
            RegisterExtendedConsentResponseType response =
                    establishConsent
                            .registerExtendedConsent(servicesHsaId, request);

            ResultCodeType resultCode = response.getResultType().getResultCode();

            boolean consent = ( resultCode == ResultCodeType.OK || resultCode == ResultCodeType.ALREADYEXISTS);

            LOGGER.trace("Consent established for patient {}", patientId);

            return new WithFallback<Boolean>(consent, false); // FIXME 2013-10-21: Magnus Andersson > Handle exceptions that generate fallback

        } catch( SOAPFaultException e) {
            LOGGER.error("Could not contact Consent service. Using fallback.", e);
            return new WithFallback<Boolean>(true, true);
        }

    }
}
