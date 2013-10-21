package se.vgregion.service.pdl;


import se.riv.ehr.patientrelationship.accesscontrol.checkpatientrelationresponder.v1.CheckPatientRelationRequestType;
import se.riv.ehr.patientrelationship.administration.registerextendedpatientrelation.v1.rivtabp21.RegisterExtendedPatientRelationResponderInterface;
import se.riv.ehr.patientrelationship.administration.registerextendedpatientrelationresponder.v1.RegisterExtendedPatientRelationRequestType;
import se.riv.ehr.patientrelationship.administration.registerextendedpatientrelationresponder.v1.RegisterExtendedPatientRelationResponseType;
import se.riv.ehr.patientrelationship.v1.AccessingActorType;
import se.riv.ehr.patientrelationship.v1.ActionType;
import se.riv.ehr.patientrelationship.v1.ActorType;
import se.riv.ehr.patientrelationship.v1.ResultCodeType;
import se.vgregion.domain.pdl.PdlContext;
import se.vgregion.domain.pdl.WithFallback;

public class Relationship {
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

    public static WithFallback<Boolean> establishRelation(
            PdlContext ctx,
            String patientId,
            RegisterExtendedPatientRelationResponderInterface establishRelationship
    ) {
        RegisterExtendedPatientRelationRequestType request = new RegisterExtendedPatientRelationRequestType();
        request.setCareProviderId(ctx.careProviderHsaId);
        request.setCareUnitId(ctx.careUnitHsaId);
        request.setEmployeeId(ctx.employeeHsaId);
        request.setPatientId(patientId);
        request.setPatientRelationId("???");
        ActionType action = new ActionType();
        action.setReasonText("VGR PDL Portlet");

        ActorType registeredBy = new ActorType();
        registeredBy.setAssignmentId(ctx.assignmentHsaId);
        registeredBy.setAssignmentName("VGR PDL Portlet");
        registeredBy.setEmployeeId(ctx.employeeHsaId);

        action.setRegisteredBy(registeredBy);

        ActorType requestedBy = new ActorType();
        requestedBy.setAssignmentId("VGR-PDL");
        requestedBy.setAssignmentName("VGR PDL Portlet");
        requestedBy.setEmployeeId(ctx.employeeHsaId);

        action.setRequestedBy(requestedBy);

        request.setRegistrationAction(action);

        request.setStartDate(null);  // FIXME 2013-10-16: Magnus Andersson > Create date.
        request.setEndDate(null);

        RegisterExtendedPatientRelationResponseType response = establishRelationship.registerExtendedPatientRelation(ctx.careProviderHsaId, request);

        return WithFallback.success(response.getResultType().getResultCode() == ResultCodeType.OK);  // FIXME 2013-10-16: Magnus Andersson > Handle soap exceptions.
    }
}
