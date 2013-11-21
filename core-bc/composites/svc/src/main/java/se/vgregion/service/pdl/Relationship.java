package se.vgregion.service.pdl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.riv.ehr.patientrelationship.accesscontrol.checkpatientrelationresponder.v1.CheckPatientRelationRequestType;
import se.riv.ehr.patientrelationship.administration.registerextendedpatientrelation.v1.rivtabp21.RegisterExtendedPatientRelationResponderInterface;
import se.riv.ehr.patientrelationship.administration.registerextendedpatientrelationresponder.v1.RegisterExtendedPatientRelationRequestType;
import se.riv.ehr.patientrelationship.administration.registerextendedpatientrelationresponder.v1.RegisterExtendedPatientRelationResponseType;
import se.riv.ehr.patientrelationship.v1.*;
import se.vgregion.domain.pdl.PdlContext;
import se.vgregion.domain.pdl.RoundedTimeUnit;
import se.vgregion.domain.pdl.decorators.WithOutcome;

import java.io.Serializable;

public class Relationship {
    private static final Logger LOGGER = LoggerFactory.getLogger(Relationship.class.getName());


    private Relationship(){
        // Utility class, no costructor!
    }

    public static CheckPatientRelationRequestType checkRelationshipRequest(PdlContext ctx, String patientId) {
        CheckPatientRelationRequestType request = new CheckPatientRelationRequestType();
        AccessingActorType actor = new AccessingActorType();
        actor.setCareProviderId(ctx.careProviderHsaId);
        actor.setCareUnitId(ctx.careUnitHsaId);
        actor.setEmployeeId(ctx.employeeHsaId);
        request.setPatientId(patientId);
        request.setAccessingActor(actor);
        return request;
    }

    public static WithOutcome<Boolean> establishRelation(
            String servicesHsaId,
            RegisterExtendedPatientRelationResponderInterface establishRelationship,
            PdlContext ctx,
            String patientId,
            String reason,
            int duration,
            RoundedTimeUnit timeUnit
    ) {
        RegisterExtendedPatientRelationRequestType request = new RegisterExtendedPatientRelationRequestType();
        request.setCareProviderId(ctx.careProviderHsaId);
        request.setCareUnitId(ctx.careUnitHsaId);
        request.setEmployeeId(ctx.employeeHsaId);
        request.setPatientId(patientId);
        request.setPatientRelationId(java.util.UUID.randomUUID().toString());

        ActionType action = new ActionType();
        action.setReasonText(reason);
        action.setRegistrationDate(XMLDuration.currentDateAsXML());
        action.setRequestDate(XMLDuration.currentDateAsXML());

        ActorType actor = new ActorType();
        actor.setAssignmentId(ctx.getAssignmentHsaId());
        actor.setAssignmentName(ctx.getAssignmentDisplayName());
        actor.setEmployeeId(ctx.employeeHsaId);

        action.setRequestedBy(actor);
        action.setRegisteredBy(actor);
        request.setRegistrationAction(action);

        XMLDuration xmlDuration = new XMLDuration(duration, timeUnit);

        request.setStartDate(xmlDuration.startDate);
        request.setEndDate(xmlDuration.endDate);

        RegisterExtendedPatientRelationResponseType response = establishRelationship
                .registerExtendedPatientRelation(servicesHsaId, request);

        if( response.getResultType().getResultCode() == ResultCodeType.VALIDATION_ERROR ) {
            LOGGER.error(
                    "Validation error for register patient relationship. Message: {}",
                    response.getResultType().getResultText()
            );
        }

        return WithOutcome.success(response.getResultType().getResultCode() == ResultCodeType.OK);
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
                    "Patient relationship service returned something else than 'OK'. Continuing anyways. \nResult code was {}. \nMessage was {}.",
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
