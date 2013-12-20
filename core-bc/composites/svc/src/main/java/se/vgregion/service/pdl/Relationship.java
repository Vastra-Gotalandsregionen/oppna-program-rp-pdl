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
import se.vgregion.domain.assignment.Assignment;
import se.vgregion.domain.decorators.WithOutcome;

import java.io.Serializable;

public class Relationship {
    private static final Logger LOGGER = LoggerFactory.getLogger(Relationship.class.getName());


    private Relationship(){
        // Utility class, no costructor!
    }

    public static CheckPatientRelationRequestType checkRelationshipRequest(PdlContext ctx, String patientId) {

        CheckPatientRelationRequestType request = new CheckPatientRelationRequestType();
        AccessingActorType actor = new AccessingActorType();
        actor.setCareProviderId(ctx.currentAssignment.careProviderHsaId);
        actor.setCareUnitId(ctx.currentAssignment.careUnitHsaId);
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
        Assignment currentAssignment = ctx.currentAssignment;

        RegisterExtendedPatientRelationRequestType request = new RegisterExtendedPatientRelationRequestType();
        request.setCareProviderId(currentAssignment.careProviderHsaId);
        request.setCareUnitId(currentAssignment.careUnitHsaId);
        request.setEmployeeId(ctx.employeeHsaId);
        request.setPatientId(patientId);
        request.setPatientRelationId(java.util.UUID.randomUUID().toString());

        ActionType action = new ActionType();
        action.setReasonText(reason);
        action.setRegistrationDate(XMLDuration.currentDateAsXML());
        action.setRequestDate(XMLDuration.currentDateAsXML());

        ActorType actor = new ActorType();
        actor.setAssignmentId(ctx.currentAssignment.getAssignmentHsaId());
        actor.setAssignmentName(currentAssignment.assignmentDisplayName);
        actor.setEmployeeId(ctx.employeeHsaId);

        action.setRequestedBy(actor);
        action.setRegisteredBy(actor);
        request.setRegistrationAction(action);

        XMLDuration xmlDuration = new XMLDuration(duration, timeUnit);

        request.setStartDate(xmlDuration.startDate);
        request.setEndDate(xmlDuration.endDate);

        RegisterExtendedPatientRelationResponseType response = establishRelationship
                .registerExtendedPatientRelation(servicesHsaId, request);

        if( response.getResultType().getResultCode() == ResultCodeType.OK ) {
            return WithOutcome.success(true);
        } else {
            switch (response.getResultType().getResultCode()) {
                case VALIDATION_ERROR :
                    LOGGER.error(
                        "Validation error for register patient relationship. Message: {}",
                        response.getResultType().getResultText()
                    );
                    return WithOutcome.clientError(true);
                case ERROR:
                    LOGGER.error(
                        "Error when registring patient relationship. Message: {}",
                        response.getResultType().getResultText()
                    );
                    return WithOutcome.remoteFailure(true);
                case ACCESSDENIED:
                    LOGGER.error(
                        "AccessWithScope denied when registring patient relationship. Message: {}",
                        response.getResultType().getResultText()
                    );
                    return WithOutcome.commFailure(true);
                case ALREADYEXISTS:
                    LOGGER.error(
                            "Duplicate ids when registring patient relationship. Message: {}",
                            response.getResultType().getResultText()
                    );
                    return WithOutcome.clientError(true);
                case INVALIDSTATE:
                    LOGGER.error(
                        "Invalid state when registring patient relationship. Message: {}",
                        response.getResultType().getResultText()
                    );
                default:
                    LOGGER.error(
                            "Unknown error when establishing relationship. Message: {}",
                            response.getResultType().getResultText()
                    );
                    return WithOutcome.remoteFailure(true);
            }
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
