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
import se.vgregion.domain.decorators.Maybe;
import se.vgregion.domain.decorators.WithInfoType;
import se.vgregion.domain.decorators.WithOutcome;
import se.vgregion.domain.decorators.WithPatient;
import se.vgregion.domain.pdl.InformationType;
import se.vgregion.domain.pdl.PdlContext;
import se.vgregion.domain.systems.CareProviderUnit;
import se.vgregion.domain.systems.CareSystem;
import se.vgregion.domain.systems.CareSystemViewer;
import se.vgregion.events.context.Patient;
import se.vgregion.events.context.SourceReferences;
import se.vgregion.events.context.sources.radiology.RadiologySourceRefs;
import se.vgregion.portal.bfr.infobroker.domain.InfobrokerPersonIdType;
import se.vgregion.service.search.CareSystems;
import se.vgregion.service.search.HsaUnitMapper;
import urn.riv.hsa.HsaWs.v3.HsaWsFault;
import urn.riv.hsa.HsaWs.v3.HsaWsResponderInterface;

import javax.annotation.Resource;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.*;
import java.util.concurrent.*;

@Service("pdlRadiologySource")
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

    private ExecutorService executorService =
            Executors.newCachedThreadPool(new ThreadFactory() {
                private final ThreadFactory threadFactory = Executors.defaultThreadFactory();

                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = threadFactory.newThread(r);
                    thread.setName("Radiology-HSA-threadpool-" + thread.getName());
                    thread.setDaemon(true);
                    return thread;
                }
            });

    @Override
    public WithOutcome<WithPatient<ArrayList<WithInfoType<CareSystem>>>> byPatientId(
            PdlContext ctx,
            String patientId,
            InfobrokerPersonIdType patientIdType
    ) {
        Patient unknownPatient = new Patient(patientId);

        try {
            RequestList result = infoBrokerRequest(patientId, patientIdType);

            WithOutcome<ArrayList<WithInfoType<CareSystem>>> outcome =
                    getMapRadiologyResult(result);

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
            Patient.Sex patientSex = Patient.Sex.UNKNOWN;

            String patientNameSecret = replaceNameWhenHidden(result);

            switch (result.getPatient().getPatientData().getSex()) {
                case MALE:
                    patientSex = Patient.Sex.MALE;
                    break;
                case FEMALE:
                    patientSex = Patient.Sex.FEMALE;
                    break;
            }

            return unknownPatient.mapPatientInfo(patientNameSecret, patientSex);
        }
        return unknownPatient;
    }

    private String replaceNameWhenHidden(RequestList result) {
        String firstName = result.getPatient().getPatientData().getFirstName();
        String lastName = result.getPatient().getPatientData().getLastName();
        String patientName = firstName + " " + lastName;
        return (result.getPatient().getPatientData().isHiddenIdentity()) ? patientName.replaceAll(".","*") : patientName;
    }

    private WithOutcome<ArrayList<WithInfoType<CareSystem>>> getMapRadiologyResult(RequestList result) throws HsaWsFault {
        ArrayList<WithInfoType<CareSystem>> systems = new ArrayList<WithInfoType<CareSystem>>();
        WithOutcome<ArrayList<WithInfoType<CareSystem>>> outcome = WithOutcome.success(systems);

        if(result != null && result.getRequest().size() > 0)  {
            systems = mapSystemsAsync(result);

            boolean allRequestWithHsaId = systems.size() == result.getRequest().size();

            if(allRequestWithHsaId) {
                return WithOutcome.success(systems);
            } else {
                return WithOutcome.unfulfilled(systems);
            }
        }

        return outcome;
    }

    private ArrayList<WithInfoType<CareSystem>> mapSystemsAsync(final RequestList result) {
        List<Future<Maybe<WithInfoType<CareSystem>>>> futures =
                new ArrayList<Future<Maybe<WithInfoType<CareSystem>>>>();

        for(final Request req : result.getRequest()) {
            final Maybe<String> hsaUnitId = extractHsaUnitId(req);

            if(hsaUnitId.success) {

                Callable<Maybe<WithInfoType<CareSystem>>> call = new Callable<Maybe<WithInfoType<CareSystem>>>(){
                    public Maybe<WithInfoType<CareSystem>> call() throws Exception {
                        return mapSystem(req, hsaUnitId);
                    }
                };

                Future<Maybe<WithInfoType<CareSystem>>> future = executorService.submit(call);
                futures.add(future);
            }
        }

        ArrayList<WithInfoType<CareSystem>> systems = new ArrayList<WithInfoType<CareSystem>>();

        for(Future<Maybe<WithInfoType<CareSystem>>> f : futures) {
            try {
                Maybe<WithInfoType<CareSystem>> res = f.get();
                if(res.success) {
                    systems.add(res.value);
                }
            } catch (InterruptedException e) {
                LOGGER.error("Unable to complete HSA-mapping", e);
            } catch (ExecutionException e) {
                LOGGER.error("Unable to complete HSA-mapping", e);
            }
        }

        return systems;
    }

    private Maybe<WithInfoType<CareSystem>> mapSystem(Request req, Maybe<String> hsaUnitId) {
        WithOutcome<Maybe<CareProviderUnit>> careProviderUnit = hsaMapper.toCareProviderUnit(hsaUnitId.value);

        if(careProviderUnit.success) {
            if(careProviderUnit.value.success) {
                String infoBrokerId = req.getInfobrokerId();

                // Don't know any better way of getting a date. Is this even shown in gui?
                Date requestDate;
                if (req.getExamination().size() != 1) {
                    requestDate = null;
                } else {
                    requestDate = getDateFromGregorianCalendar(req.getExamination().get(0).getDate());
                }

                Map<String,SourceReferences> refs = new HashMap<String,SourceReferences>();

                RadiologySourceRefs value = new RadiologySourceRefs(
                        requestDate,
                        aggregateNumImages(req.getExamination()),
                        careProviderUnit.value.value.careUnitDisplayName,
                        req.getPlacer().getLocationData().getName(),
                        examinationCodeAggregate(req.getExamination()),
                        statusCodeAggregate(req.getExamination()),
                        infoBrokerId
                );

                String key = value.id;
                refs.put(key, value);

                CareSystem cs = new CareSystem(CareSystemViewer.BFR, careProviderUnit.value.value, refs);
                return Maybe.some(new WithInfoType<CareSystem>(InformationType.UND, cs));
            } else {
                LOGGER.warn("Could not find care unit {} among care providers with agreement.", hsaUnitId.value);
            }
        }
        return Maybe.none();
    }

    private int aggregateNumImages(List<Examination> examinations) {
        int i = 0;
        if( examinations != null) {
            for(Examination ex : examinations) {
                if(ex.getNumberOfImages() != null) {
                    i += ex.getNumberOfImages().intValue();
                }
            }
        }
        return i;
    }

    private String examinationCodeAggregate(List<Examination> examinations) {
        StringBuilder sb = new StringBuilder();

        if(examinations.size() > 0) {
            for(Examination examination : examinations) {
                if (examination.getCode() != null) {
                    sb.append(examination.getCode().getDescription());
                    if (examinations.size() > 1) {
                        sb.append(" (");
                        sb.append(examination.getNumberOfImages().intValue());
                        sb.append(")");
                    }

                }
            }
        } else {
            sb.append("- ? -");
        }

        return sb.toString();
    }

    private String statusCodeAggregate(List<Examination> examinations) {
        StringBuilder sb = new StringBuilder();

        if(examinations.size() > 0) {
            for(Examination examination : examinations) {
                // Examination status
                if (examination.getStatus() != null) {
                    sb.append(examination.getStatus());
                } else {
                    sb.append(" - ? - ");
                }
            }
        } else {
            sb.append("- ? -");
        }

        return sb.toString();
    }

    private Maybe<String> extractHsaUnitId(Request req) {
        Maybe<String> hsaUnitId = Maybe.none();

        boolean hasData = req.getPlacer() != null &&
                req.getPlacer().getLocationData() != null &&
                req.getPlacer().getLocationData().getExternalIds() != null;

        if(hasData) {
            for( ExternalId eid : req.getPlacer().getLocationData().getExternalIds().getExternalId()) {
                if(eid.getType().getCode().equals(HSA_UNIT)) {
                    hsaUnitId = Maybe.some(eid.getValue());
                }
            }
        }

        return hsaUnitId;
    }

    private RequestList infoBrokerRequest(String patientId, InfobrokerPersonIdType pidType) {
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

    /**
     * Will return 1912-12-12 12:12 if date is null.
     *
     * @param gregorianCal
     *            date to convert
     * @return converted date
     */
    private static Date getDateFromGregorianCalendar(XMLGregorianCalendar gregorianCal) {
        Calendar returnDate = Calendar.getInstance();
        if (gregorianCal != null) {
            returnDate.set(gregorianCal.getYear(), gregorianCal.getMonth() - 1, gregorianCal.getDay(),
                    gregorianCal.getHour(), gregorianCal.getMinute());
        } else {
            return null;
        }
        return returnDate.getTime();
    }
}
