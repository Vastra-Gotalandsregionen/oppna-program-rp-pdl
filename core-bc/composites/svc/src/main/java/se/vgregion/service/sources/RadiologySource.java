package se.vgregion.service.sources;

import com.mawell.ib.patientsearch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.w3._2005._08.addressing.AttributedURIType;
import riv.ehr.ehrexchange.patienthistory._1.rivtabp20.PatientHistoryResponderInterface;
import se.ecare.ib.exportmessage.ExternalId;
import se.riv.hsa.hsaws.v3.HsaWsFault;
import se.riv.hsa.hsaws.v3.HsaWsResponderInterface;
import se.vgregion.domain.decorators.Maybe;
import se.vgregion.domain.decorators.WithInfoType;
import se.vgregion.domain.decorators.WithOutcome;
import se.vgregion.domain.decorators.WithPatient;
import se.vgregion.domain.pdl.InformationType;
import se.vgregion.domain.pdl.Patient;
import se.vgregion.domain.pdl.PdlContext;
import se.vgregion.domain.source.RadiologySourceRefs;
import se.vgregion.domain.systems.CareProviderUnit;
import se.vgregion.domain.systems.CareSystem;
import se.vgregion.domain.systems.CareSystemViewer;
import se.vgregion.portal.bfr.infobroker.domain.InfobrokerPersonIdType;
import se.vgregion.service.search.CareSystems;
import se.vgregion.service.search.HsaUnitMapper;

import javax.annotation.Resource;
import java.util.ArrayList;

@Service
public class RadiologySource implements CareSystems {
    private static final String HSA_UNIT = "HSA-ENHET";

    private static final Logger LOGGER = LoggerFactory.getLogger(RadiologySource.class);

    @Resource(name = "infoBroker")
    private PatientHistoryResponderInterface infoBroker;

    @Autowired
    @Qualifier("hsaUnitMapping")
    private HsaUnitMapper hsaMapper;

    @Resource(name = "hsaOrgmaster")
    private HsaWsResponderInterface hsaOrgmaster;


    @Override
    public WithOutcome<WithPatient<ArrayList<WithInfoType<CareSystem>>>> byPatientId(
            PdlContext ctx, String patientId
    ) {
        Patient unknownPatient = new Patient(patientId);

        try {
            // FIXME 2014-01-22 : Magnus Andersson > HACK to prepare for workshop.
            InfobrokerPersonIdType pidType = (!patientId.equals("20090226D077")) ? InfobrokerPersonIdType.PAT_PERS_NR : InfobrokerPersonIdType.SU_PAT_RES_NR;

            RequestList result = ibRequest(patientId, pidType);


            WithOutcome<ArrayList<WithInfoType<CareSystem>>> outcome =
                    getMapRadiologyResult(ctx, patientId, result);

            Patient patient = extractPatient(result, unknownPatient);

            WithPatient<ArrayList<WithInfoType<CareSystem>>> withPatient =
                    new WithPatient<ArrayList<WithInfoType<CareSystem>>>(patient, outcome.value);

            return outcome.mapValue(withPatient);
        } catch (Exception e) {
            LOGGER.error("Unable to search in radiology source with patient id {}.", patientId, e);

            ArrayList<WithInfoType<CareSystem>> systems = new ArrayList<WithInfoType<CareSystem>>();

            WithPatient<ArrayList<WithInfoType<CareSystem>>> withPatient =
                    new WithPatient<ArrayList<WithInfoType<CareSystem>>>(unknownPatient, systems);

            return WithOutcome.remoteFailure(withPatient);
        }
    }

    private Patient extractPatient(RequestList result, Patient unknownPatient) {

        boolean hasPatientInfo = result != null &&
                result.getPatient() != null &&
                (result.getPatient().getPatientData().getFirstName() != null ||
                result.getPatient().getPatientData().getLastName() != null);

        if(hasPatientInfo) {
            String firstName = result.getPatient().getPatientData().getFirstName();
            String lastName = result.getPatient().getPatientData().getLastName();

            Patient.Sex patientSex = Patient.Sex.UNKNOWN;

            switch (result.getPatient().getPatientData().getSex()) {
                case MALE:
                    patientSex = Patient.Sex.MALE;
                    break;
                case FEMALE:
                    patientSex = Patient.Sex.FEMALE;
                    break;
            }

            return unknownPatient.mapNameSex(firstName + " " + lastName, patientSex);
        }
        return unknownPatient;
    }

    private WithOutcome<ArrayList<WithInfoType<CareSystem>>> getMapRadiologyResult(PdlContext ctx, String patientId, RequestList result) throws HsaWsFault {
        ArrayList<WithInfoType<CareSystem>> systems = new ArrayList<WithInfoType<CareSystem>>();
        WithOutcome<ArrayList<WithInfoType<CareSystem>>> outcome = WithOutcome.success(systems);

        if(result != null && result.getRequest().size() > 0)  {
            for(Request req : result.getRequest()) {

                Maybe<String> hsaUnitId = extractHsaUnitId(req);

                if(hsaUnitId.success) {
                    // TODO 2014-02-03 : Magnus Andersson > Do this in parallell all unit hsa id:s, list for futures.

                    WithOutcome<Maybe<CareProviderUnit>> careProviderUnit = hsaMapper.toCareProviderUnit(hsaUnitId.value);

                    if(careProviderUnit.success) {
                        if(careProviderUnit.value.success) {
                            String infoBrokerId = req.getInfobrokerId();
                            CareSystem cs = new CareSystem(CareSystemViewer.BFR, careProviderUnit.value.value, new RadiologySourceRefs(infoBrokerId));
                            systems.add(new WithInfoType<CareSystem>(InformationType.UND, cs));
                        } else {
                            LOGGER.warn("Could not find care unit {} among care providers with agreement.", hsaUnitId.value);
                        }
                    } else {
                        outcome = outcome.mapOutcome(careProviderUnit.outcome);
                    }
                }
            }

            boolean allRequestWithHsaId = systems.size() == result.getRequest().size();

            if(allRequestWithHsaId) {
                return WithOutcome.success(systems);
            } else {
                return WithOutcome.unfulfilled(systems);
            }
        }

        return outcome;
    }

    private Maybe<String> extractHsaUnitId(Request req) {
        Maybe<String> hsaUnitId = Maybe.none();
        for( ExternalId eid : req.getPlacer().getLocationData().getExternalIds().getExternalId()) {
            if(eid.getType().getCode().equals(HSA_UNIT)) {
                hsaUnitId = Maybe.some(eid.getValue());
            }
        }
        return hsaUnitId;
    }

    private RequestList ibRequest(String patientId, InfobrokerPersonIdType pidType) {
        AttributedURIType attribURIType = new AttributedURIType();
        PatientSearchOrder patientSearchOrder = new PatientSearchOrder();
        Search searchValue = new Search();
        patientSearchOrder.setSearch(searchValue);

        Exactpidsearch exactPidSearch = new Exactpidsearch();
        exactPidSearch.setPid(patientId);
        searchValue.setExactpidsearch(exactPidSearch);

        patientSearchOrder.
                getSearch().
                getExactpidsearch().
                setPidType(pidType.getInfoBrokerTypeCode());

        return infoBroker.getRequestList(attribURIType, patientSearchOrder);
    }
}
