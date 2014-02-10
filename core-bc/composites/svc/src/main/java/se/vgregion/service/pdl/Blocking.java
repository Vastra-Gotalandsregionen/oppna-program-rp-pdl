package se.vgregion.service.pdl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.riv.ehr.blocking.accesscontrol.checkblocksresponder.v2.CheckBlocksRequestType;
import se.riv.ehr.blocking.accesscontrol.checkblocksresponder.v2.CheckBlocksResponseType;
import se.riv.ehr.blocking.administration.registertemporaryextendedrevoke.v2.rivtabp21.RegisterTemporaryExtendedRevokeResponderInterface;
import se.riv.ehr.blocking.administration.registertemporaryextendedrevokeresponder.v2.RegisterTemporaryExtendedRevokeRequestType;
import se.riv.ehr.blocking.administration.registertemporaryextendedrevokeresponder.v2.RegisterTemporaryExtendedRevokeResponseType;
import se.riv.ehr.blocking.querying.getblocksforpatient.v2.rivtabp21.GetBlocksForPatientResponderInterface;
import se.riv.ehr.blocking.querying.getblocksforpatientresponder.v2.GetBlocksForPatientRequestType;
import se.riv.ehr.blocking.querying.getblocksforpatientresponder.v2.GetBlocksForPatientResponseType;
import se.riv.ehr.blocking.v2.*;
import se.vgregion.domain.pdl.*;
import se.vgregion.domain.decorators.WithBlock;
import se.vgregion.domain.decorators.WithInfoType;
import se.vgregion.domain.decorators.WithOutcome;
import se.vgregion.domain.systems.CareSystem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static se.riv.ehr.blocking.v2.ResultCodeType.OK;
import static se.riv.ehr.blocking.v2.ResultCodeType.VALIDATIONERROR;

class Blocking {
    private static final Logger LOGGER = LoggerFactory.getLogger(Blocking.class.getName());

    private static class RequestEntity {

        public final InformationType informationType;
        public final String careProviderHsaId;
        public final String careUnitHsaId;

        private RequestEntity(
                InformationType informationType,
                String careProviderHsaId,
                String careUnitHsaId
        ) {
            this.informationType = informationType;
            this.careProviderHsaId = careProviderHsaId;
            this.careUnitHsaId = careUnitHsaId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof RequestEntity)) return false;

            RequestEntity that = (RequestEntity) o;

            if (!careProviderHsaId.equals(that.careProviderHsaId)) return false;
            if (!careUnitHsaId.equals(that.careUnitHsaId)) return false;
            if (informationType != that.informationType) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = informationType.hashCode();
            result = 31 * result + careProviderHsaId.hashCode();
            result = 31 * result + careUnitHsaId.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "RequestEntity{" +
                    "informationType=" + informationType +
                    ", careProviderHsaId='" + careProviderHsaId + '\'' +
                    ", careUnitHsaId='" + careUnitHsaId + '\'' +
                    '}';
        }

    }
    private Blocking() {
        // Utility class, no constructor!
    }

    static CheckBlocksRequestType checkBlocksRequest(
            PdlContext ctx,
            Patient pe,
            List<WithInfoType<CareSystem>> careSystems
    ) {
        CheckBlocksRequestType req = new CheckBlocksRequestType();
        req.setPatientId(pe.patientId);
        AccessingActorType actor = new AccessingActorType();
        actor.setCareProviderId(ctx.currentAssignment.careProviderHsaId);
        actor.setCareUnitId(ctx.currentAssignment.careUnitHsaId);
        actor.setEmployeeId(ctx.employeeHsaId);
        req.setAccessingActor(actor);

        List<InformationEntityType> entities = req.getInformationEntities();
        XMLDuration duration = new XMLDuration(1, RoundedTimeUnit.NEAREST_HALF_HOUR); // FIXME 2013-11-18 : Magnus Andersson > Should this be a config value?

        for (int i = 0; i < careSystems.size(); i++) {
            WithInfoType<CareSystem> cs = careSystems.get(i);
            InformationEntityType en = new InformationEntityType();

            en.setInformationCareProviderId(cs.value.careProviderHsaId);
            en.setInformationCareUnitId(cs.value.careUnitHsaId);
            en.setInformationStartDate(duration.startDate);
            en.setInformationEndDate(duration.endDate);
            if(cs.informationType == InformationType.UPP || cs.informationType == InformationType.LAK) {
                en.setInformationType(cs.informationType.name().toLowerCase());
            }
            en.setRowNumber(i);

            entities.add(en);
        }

        return req;
    }

    /**
     * The WSDL-contract for some unfathomable reason requires the client to remember what row numbers were used when
     * sending the request. Therefore it is necessary to keep track of the request state and later enrich the answer.
     *
     * @param careSystems
     * @param blockResponse @return
     */
    static ArrayList<WithInfoType<WithBlock<CareSystem>>> decorateCareSystems(
            List<WithInfoType<CareSystem>> careSystems,
            CheckBlocksResponseType blockResponse
    ) {
        ArrayList<WithInfoType<WithBlock<CareSystem>>> careSystemsWithBlocks = new ArrayList<WithInfoType<WithBlock<CareSystem>>>();
        for (CheckResultType crt : blockResponse.getCheckBlocksResultType().getCheckResults()) {

            int row = crt.getRowNumber();

            if(-1 < row && row < careSystems.size()) { // Bounds check because we don't trust the EHR service. :)

                // Get the care system that the response corresponds to
                WithInfoType<CareSystem> careSystem = careSystems.get(row);

                // Encapsulate care system with block
                WithBlock<CareSystem> withBlock = null;
                if(crt.isBlocked()) {
                    withBlock = WithBlock.blocked(careSystem.value);
                } else {
                    withBlock = WithBlock.unblocked(careSystem.value);
                }

                // Map current info decorator container, keeping the info decorator but changing the value to a care system with block.
                WithInfoType<WithBlock<CareSystem>> careSystemBlock = careSystem.mapValue(withBlock);

                careSystemsWithBlocks.add(careSystemBlock);
            } else {
                LOGGER.error("Block service returned a row index {} that is out of bounds in the care systems list ({}). Skipping block information for this index. The user may not see all available information.", row, careSystemsWithBlocks.size());
            }

        }
        return careSystemsWithBlocks;
    }


    private static GetBlocksForPatientRequestType patientBlocksRequest(String servicesHsaId, String patientId) {
        GetBlocksForPatientRequestType request = new GetBlocksForPatientRequestType();
        request.setCareProviderId(servicesHsaId);
        request.setPatientId(patientId);
        return request;
    }

    private static GetBlocksForPatientResponseType patientBlocks(String servicesHsaId, GetBlocksForPatientResponderInterface patientBlocks, String patientId) {
        GetBlocksForPatientRequestType request = patientBlocksRequest(servicesHsaId, patientId);
        return patientBlocks.getBlocksForPatient(servicesHsaId, request);
    }

    private static Callable<Boolean> temporarilyRevoke(
            final PdlContext ctx,
            final RegisterTemporaryExtendedRevokeResponderInterface temporaryRevoke,
            final String servicesHsaId,
            final BlockType block,
            final String reason,
            final int duration,
            final RoundedTimeUnit roundedTimeUnit
    ) {
        final RegisterTemporaryExtendedRevokeRequestType request = revokeRequest(ctx, block, reason, duration, roundedTimeUnit);

        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                RegisterTemporaryExtendedRevokeResponseType response = temporaryRevoke.registerTemporaryExtendedRevoke(servicesHsaId, request);
                return response.getResultType().getResultCode() == OK;
            }
        };
    }

    private static RegisterTemporaryExtendedRevokeRequestType revokeRequest(
            PdlContext ctx,
            BlockType block,
            String reason, int duration,
            RoundedTimeUnit roundedTimeUnit
    ) {
        RegisterTemporaryExtendedRevokeRequestType request = new RegisterTemporaryExtendedRevokeRequestType();
        TemporaryRevokeRegistrationType revoke  = new TemporaryRevokeRegistrationType();
        revoke.setBlockId(block.getBlockId());
        revoke.setTemporaryRevokeId(java.util.UUID.randomUUID().toString());
        revoke.setRevokedForCareUnitId(ctx.currentAssignment.careUnitHsaId);
        revoke.setRevokedForEmployeeId(ctx.employeeHsaId);
        XMLDuration xmlDuration = new XMLDuration(duration, roundedTimeUnit);
        revoke.setEndDate(xmlDuration.endDate);
        request.setRevokeReasonText(reason);
        request.setRevokeReason(TemporaryRevokeReasonType.PATIENTS_CONSENT); // FIXME 2013-11-15 : Magnus Andersson > This should be a parameter.

        return request;
    }

    static <T extends Serializable> WithOutcome<T> decideOutcome(
            ResultType result,
            T value)
    {
        ResultCodeType resultCode = result.getResultCode();
        String resultText = result.getResultText();

        if(ResultCodeType.OK == resultCode) {
            return WithOutcome.success(value);
        } else {
            LOGGER.error(
                    "Check blocks returned something else than 'OK'. Continuing anyways. \nResult code was {}. \nMessage was {}.",
                    resultCode,
                    resultText
            );
            if(VALIDATIONERROR == resultCode) {
                return WithOutcome.clientError(value);
            } else {
                return WithOutcome.remoteFailure(value);
            }
        }
    }
}


