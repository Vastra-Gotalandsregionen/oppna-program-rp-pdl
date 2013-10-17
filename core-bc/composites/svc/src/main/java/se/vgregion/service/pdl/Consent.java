package se.vgregion.service.pdl;

import se.riv.ehr.patientconsent.accesscontrol.checkconsentresponder.v1.CheckConsentRequestType;
import se.riv.ehr.patientconsent.accesscontrol.checkconsentresponder.v1.CheckConsentResponseType;
import se.riv.ehr.patientconsent.v1.AccessingActorType;
import se.riv.ehr.patientconsent.v1.AssertionTypeType;
import se.riv.ehr.patientrelationship.accesscontrol.checkpatientrelationresponder.v1.CheckPatientRelationRequestType;
import se.vgregion.domain.pdl.CheckedConsent;
import se.vgregion.domain.pdl.PdlContext;
import se.vgregion.domain.pdl.PdlReport;

class Consent {

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
                PdlReport.ConsentType.CONSENT : PdlReport.ConsentType.EMERGENCY;

        return new CheckedConsent( consentType, hasConsent );
    }

    public static CheckPatientRelationRequestType checkRelationshipRequest(PdlContext ctx) {

        return new CheckPatientRelationRequestType();
    }
}
