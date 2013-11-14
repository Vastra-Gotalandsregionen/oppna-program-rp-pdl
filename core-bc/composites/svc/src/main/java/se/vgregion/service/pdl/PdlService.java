package se.vgregion.service.pdl;

import se.vgregion.domain.pdl.*;

import java.util.List;

public interface PdlService {
    enum UnblockType {
        CONSENT, EMERGENCY
    }

    /**
     * <p>Generate initial PDL Report.</p>
     *
     * @param ctx PDL Context
     * @param patientEngagements Engagements within Health Care
     * @return Instance of PdlReport containing blocks, consent and relationship
     */
    PdlReport pdlReport(PdlContext ctx, PatientWithEngagements patientEngagements);

    /**
     * <p>Attempts to establish the patient consent for shared care provider journaling. Returns a new PdlReport containing the updated information</p>
     * <p>Duration stipulates the duration of the relation, given from the time of the request.</p>
     *
     * @param ctx PDL Context
     * @param report Previous report where consent is not present
     * @param patientId Current Patient ID
     * @param reason Comment describing reason for establishing patient consent
     * @param duration Positive integer of duration of rounded time units
     * @param roundedTimeUnit Time units that are <i>rounded up</i> to the nearest unit
     * @param consentType Type of consent
     * @return Instance of PdlReport after attempt to establish patient consent
     */
    PdlReport patientConsent(
            PdlContext ctx,
            PdlReport report,
            String patientId,
            String reason,
            int duration,
            RoundedTimeUnit roundedTimeUnit,
            PdlReport.ConsentType consentType
    );

    /**
     * <p>Attempts to establish a patient relationship, returns a new PdlReport containing updated information.</p>
     * <p>Duration stipulates the duration of the relation, given from the time of the request.</p>
     *
     * @param ctx PDL Context
     * @param report Previous report that is missing the relationship
     * @param patientId Current Patient ID
     * @param reason Comment describing reason for establishing patient relationship
     * @param duration Positive integer of duration of rounded time units
     * @param roundedTimeUnit Time units that are <i>rounded up</i> to the nearest unit
     * @return Instance of PdlReport containing after attempt to establish patient relation
     */
    PdlReport patientRelationship(
            PdlContext ctx,
            PdlReport report,
            String patientId,
            String reason,
            int duration,
            RoundedTimeUnit roundedTimeUnit
    );

    PdlReport unblockInformation(PdlContext ctx, String blockId, UnblockType unblockType, String unblockComment);

    PdlAssertion chooseInformation(PdlContext ctx, PdlReport report, List<Engagement> engagements);
}
