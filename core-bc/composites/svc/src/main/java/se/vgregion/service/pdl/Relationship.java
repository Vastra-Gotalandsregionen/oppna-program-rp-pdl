package se.vgregion.service.pdl;


import se.riv.ehr.patientrelationship.accesscontrol.checkpatientrelationresponder.v1.CheckPatientRelationRequestType;
import se.riv.ehr.patientrelationship.v1.AccessingActorType;
import se.vgregion.domain.pdl.PdlContext;

public class Relationship {
    private Relationship(){
        // Utility class, no costructor!
    }

    public static CheckPatientRelationRequestType checkRelationshipRequest(PdlContext ctx) {
        CheckPatientRelationRequestType request = new CheckPatientRelationRequestType();
        AccessingActorType actor = new AccessingActorType();
        actor.setCareProviderId(ctx.careProviderHsaId);
        actor.setCareUnitId(ctx.careUnitHsaId);
        actor.setEmployeeId(ctx.employeeHsaId);
        request.setPatientId(ctx.patientId);
        request.setAccessingActor(actor);
        return request;
    }
}
