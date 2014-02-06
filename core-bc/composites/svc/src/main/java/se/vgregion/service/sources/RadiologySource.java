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
import se.riv.hsa.hsawsresponder.v3.GetCareUnitResponseType;
import se.vgregion.domain.decorators.Maybe;
import se.vgregion.domain.decorators.WithInfoType;
import se.vgregion.domain.decorators.WithOutcome;
import se.vgregion.domain.pdl.*;
import se.vgregion.portal.bfr.infobroker.domain.InfobrokerPersonIdType;
import se.vgregion.service.search.CareSystems;
import se.vgregion.service.search.HsaUnitMapper;
import se.vgregion.service.hsa.HsaWsUtil;

import javax.annotation.Resource;
import java.util.ArrayList;

@Service
public class RadiologySource implements CareSystems {
    private static final String HSA_UNIT = "HSA-ENHET";

    private static final Logger LOGGER = LoggerFactory.getLogger(RadiologySource.class);

    @Resource(name = "infoBroker")
    private PatientHistoryResponderInterface infoBroker;

    @Autowired
    @Qualifier("hsaUnitMappingMock")
    private HsaUnitMapper hsaMapper;

    @Resource(name = "hsaOrgmaster")
    private HsaWsResponderInterface hsaOrgmaster;


    @Override
    public WithOutcome<ArrayList<WithInfoType<CareSystem>>> byPatientId(PdlContext ctx, String patientId) {
        try {

            // FIXME 2014-01-22 : Magnus Andersson > HACK to prepare for workshop.
            InfobrokerPersonIdType pidType = (!patientId.equals("20090226D077")) ? InfobrokerPersonIdType.PAT_PERS_NR : InfobrokerPersonIdType.SU_PAT_RES_NR;

            RequestList result = getIbRequests(patientId, pidType);

            return getMapRadiologyResult(ctx, patientId, result);

        } catch (Exception e) {
            LOGGER.error("Unable to search in radiology source with patient id {}.", patientId, e);
            return WithOutcome.remoteFailure(new ArrayList<WithInfoType<CareSystem>>());
        }
    }

    private WithOutcome<ArrayList<WithInfoType<CareSystem>>> getMapRadiologyResult(PdlContext ctx, String patientId, RequestList result) throws HsaWsFault {
        ArrayList<WithInfoType<CareSystem>> systems = new ArrayList<WithInfoType<CareSystem>>();

        if(result != null && result.getRequest().size() > 0)  {
            for(Request req : result.getRequest()) {

                Maybe<String> hsaUnitId = getHsaUnitId(req);

                if(hsaUnitId.success) {
                    // TODO 2014-02-03 : Magnus Andersson > Do this in parallell all unit hsa id:s, list for futures.
                    GetCareUnitResponseType careUnitResponse = getGetCareUnit(hsaUnitId);

                    boolean isValidResponse =
                            careUnitResponse != null &&
                            careUnitResponse.getCareGiver() != null &&
                            careUnitResponse.getCareUnitHsaIdentity() != null;

                    if(isValidResponse) {
                        String careProviderHsaId = careUnitResponse.getCareGiver();
                        String careUnitHsaId = careUnitResponse.getCareUnitHsaIdentity();

                        Maybe<CareProviderUnit> careProviderUnit =
                                hsaMapper.toCareProviderUnit(careProviderHsaId, careUnitHsaId);

                        if(careProviderUnit.success) {
                            CareSystem cs = new CareSystem(CareSystemSource.BFR, careProviderUnit.value);
                            systems.add(new WithInfoType<CareSystem>(InformationType.UND, cs));
                        }
                    }
                }
            }

            boolean allRequestWithHsaId = systems.size() == result.getRequest().size();

            if(allRequestWithHsaId) {
                return WithOutcome.success(systems);
            } else {
                return WithOutcome.unfulfilled(systems);
            }
        } else {
            // FIXME 2014-02-04 : Magnus Andersson > This call need to be removed when proper test data exists
            return new CareSystemsImpl().byPatientId(ctx, patientId);
        }
    }

    private Maybe<String> getHsaUnitId(Request req) {
        Maybe<String> hsaUnitId = Maybe.none();
        for( ExternalId eid : req.getPlacer().getLocationData().getExternalIds().getExternalId()) {
            if(eid.getType().getCode().equals(HSA_UNIT)) {
                hsaUnitId = Maybe.some(eid.getValue());
            }
        }
        return hsaUnitId;
    }

    private GetCareUnitResponseType getGetCareUnit(Maybe<String> hsaUnitId) throws HsaWsFault {
        // FIXME 2014-02-03 : Magnus Andersson > Hard coded value, use config
        return hsaOrgmaster.getCareUnit(
                HsaWsUtil.getAttribute("SE165565594230-1000"),
                HsaWsUtil.getAttribute(null),
                HsaWsUtil.getLookupByHsaId(hsaUnitId.value)
        );
    }

    private RequestList getIbRequests(String patientId, InfobrokerPersonIdType pidType) {
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
