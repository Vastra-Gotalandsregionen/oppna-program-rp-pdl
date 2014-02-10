package se.vgregion.service.search;

import se.vgregion.domain.decorators.WithInfoType;
import se.vgregion.domain.pdl.*;
import se.vgregion.domain.systems.CareSystem;

import java.util.List;

public interface PdlService {
    enum UnblockType {
        CONSENT, EMERGENCY
    }

    /**
     * <p>Generate initial PDL Report.</p>
     *
     *
     * @param ctx PDL Context
     * @param patient Patient information
     * @param careSystems Care Systems to generate report for
     */
    PdlReport pdlReport(
            PdlContext ctx,
            Patient patient,
            List<WithInfoType<CareSystem>> careSystems
    );

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
     * @return Instance of PdlReport after attempt to establish patient relation
     */
    PdlReport patientRelationship(
            PdlContext ctx,
            PdlReport report,
            String patientId,
            String reason,
            int duration,
            RoundedTimeUnit roundedTimeUnit
    );

    /**
     * <p>This method will temporarily unblock <i>all systmes</i> (if overlapping systmes exist) that hinders the information from being accessed.</p>
     *
     * @param ctx PDL Context
     * @param report Previous report that is missing the relationship
     * @param unblockType Type of temporary removal of block. With patient consent or an emergency
     * @param reason Reason or comment
     * @param duration Positive integer of duration of rounded time units
     * @param roundedTimeUnit Time units that are <i>rounded up</i> to the nearest unit
     * @return Instance of PdlReport after attempt to unblock information
     */
    PdlReport unblockInformation(
            PdlContext ctx,
            PdlReport report,
            String patientId,
            UnblockType unblockType,
            String reason,
            int duration,
            RoundedTimeUnit roundedTimeUnit
    );

    PdlAssertion chooseInformation(PdlContext ctx, PdlReport report);
}
