package se.vgregion.service.pdl.sources;

import com.mawell.ib.patientsearch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3._2005._08.addressing.AttributedURIType;
import riv.ehr.ehrexchange.patienthistory._1.rivtabp20.PatientHistoryResponderInterface;
import se.ecare.ib.exportmessage.ExternalId;
import se.vgregion.domain.decorators.Maybe;
import se.vgregion.domain.decorators.WithInfoType;
import se.vgregion.domain.decorators.WithOutcome;
import se.vgregion.domain.pdl.CareSystem;
import se.vgregion.domain.pdl.CareSystemSource;
import se.vgregion.domain.pdl.InformationType;
import se.vgregion.domain.pdl.PdlContext;
import se.vgregion.portal.bfr.infobroker.domain.InfobrokerPersonIdType;
import se.vgregion.service.pdl.CareSystems;
import se.vgregion.service.pdl.CareSystemsImpl;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


class ReqExam {
    public final String unitHsaId;
    public final boolean hasExams;
    public final int numExams;

    ReqExam(String unitHsaId, int numExams) {
        this.unitHsaId = unitHsaId;
        this.hasExams = numExams > 0;
        this.numExams = numExams;
    }
}

@Service
public class RadiologySource implements CareSystems {
    private static final String HSA_UNIT = "HSA-ENHET";

    private static final Logger LOGGER = LoggerFactory.getLogger(RadiologySource.class);

    @Resource(name = "infoBroker")
    private PatientHistoryResponderInterface infoBroker;

    @Override
    public WithOutcome<ArrayList<WithInfoType<CareSystem>>> byPatientId(PdlContext ctx, String patientId) {
        try {
            AttributedURIType attribURIType = new AttributedURIType();
            PatientSearchOrder patientSearchOrder = new PatientSearchOrder();
            Search searchValue = new Search();
            patientSearchOrder.setSearch(searchValue);

            Exactpidsearch exactPidSearch = new Exactpidsearch();
            exactPidSearch.setPid(patientId);
            searchValue.setExactpidsearch(exactPidSearch);

            // FIXME 2014-01-22 : Magnus Andersson > HACK to prepare for workshop.
            String pidType = (!patientId.equals("20090226D077")) ? InfobrokerPersonIdType.PAT_PERS_NR.getInfoBrokerTypeCode() : InfobrokerPersonIdType.SU_PAT_RES_NR.getInfoBrokerTypeCode();

            patientSearchOrder.
                    getSearch().
                    getExactpidsearch().
                    setPidType(pidType);

            RequestList result = infoBroker.getRequestList(attribURIType, patientSearchOrder);

            if(result != null && result.getRequest().size() > 0)  {

                List<ReqExam> reqExams = new ArrayList<ReqExam>();

                for( Request req : result.getRequest()) {
                    Maybe<String> hsaId = Maybe.none();
                    for( ExternalId eid : req.getPlacer().getLocationData().getExternalIds().getExternalId() ) {
                        if(eid.getType().getCode().equals(HSA_UNIT)) {
                            hsaId = Maybe.some(eid.getValue());
                        }
                    }

                    if(hsaId.success) {
                        ReqExam reqExam = new ReqExam(hsaId.value, req.getExamination().size());
                        reqExams.add(reqExam);
                    }
                }

                ArrayList<WithInfoType<CareSystem>> systems = new ArrayList<WithInfoType<CareSystem>>();
                for(ReqExam reqExam : reqExams) {
                    if(HsaUnitMappingMock.hsaUnitMapping.containsKey(reqExam.unitHsaId)) {
                        CareSystem cs = HsaUnitMappingMock.hsaUnitMapping.get(reqExam.unitHsaId);

                        Maybe<WithInfoType<CareSystem>> examCs = Maybe.none();

                        if(reqExam.hasExams) {
                            examCs = Maybe.some(
                                new WithInfoType<CareSystem>(
                                    InformationType.UND,
                                    new CareSystem(
                                        CareSystemSource.BFR,
                                        cs.careProviderHsaId,
                                        cs.careProviderDisplayName,
                                        cs.careUnitHsaId,
                                        cs.careUnitDisplayName + " (" + reqExam.numExams + " studie)"
                                    )
                                )
                            );
                        }

                        WithInfoType<CareSystem> withInfo =
                                new WithInfoType<CareSystem>(InformationType.VBE, cs);

                        systems.add(withInfo); // Add the request

                        if(examCs.success) {
                            systems.add(examCs.value); // Add examinations
                        }
                    }
                }

                boolean allRequestWithHsaId = reqExams.size() == result.getRequest().size();

                if(allRequestWithHsaId) {
                    return WithOutcome.success(systems);
                } else {
                    return WithOutcome.unfulfilled(systems);
                }
            } else {
                return new CareSystemsImpl().byPatientId(ctx, patientId);
            }
        } catch (Exception e) {
            LOGGER.error("Unable to search in radiology source with patient id {}.", patientId, e);
            return WithOutcome.remoteFailure(new ArrayList<WithInfoType<CareSystem>>());
        }
    }
}
