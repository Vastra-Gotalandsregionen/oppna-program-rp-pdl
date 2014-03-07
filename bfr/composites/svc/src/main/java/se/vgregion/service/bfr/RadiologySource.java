package se.vgregion.service.bfr;

import com.mawell.ib.patientsearch.RequestOrder;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3._2005._08.addressing.AttributedURIType;
import riv.ehr.ehrexchange.patienthistory._1.rivtabp20.PatientHistoryResponderInterface;
import se.ecare.ib.exportmessage.*;
import se.vgregion.domain.bfr.Referral;
import se.vgregion.domain.bfr.ReferralBuilder;
import se.vgregion.domain.bfr.Study;
import se.vgregion.domain.bfr.StudyReport;
import se.vgregion.domain.decorators.WithOutcome;

import javax.annotation.Resource;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.WebServiceException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service("bfrRadiologySource")
public class RadiologySource {
    private static final Logger LOGGER = LoggerFactory.getLogger(RadiologySource.class);

    @Resource(name = "infoBroker")
    private PatientHistoryResponderInterface infoBroker;

    public static final String HTML_BREAK = "<br>";
    public static final String NEWLINE_CHAR = "\n";

    public WithOutcome<Referral> requestByBrokerId(String brokerId) {
        try {
            Request request = ibRequest(brokerId);
            Referral referral = mapRequestToRequestDetails(request);

            return WithOutcome.success(referral);
        } catch (WebServiceException e) {
            LOGGER.error("Could not complete request with info broker id {}", brokerId, e);
            return WithOutcome.commFailure(null);
        }
    }

    private Referral mapRequestToRequestDetails(Request req) {
        ReferralBuilder b = new ReferralBuilder();

        b.statusList = getStatuses(req);
        b.imageCount = sumNumImages(req.getExamination());
        b.placingDate = getDateFromGregorianCalendar(req.getPlacer().getLocationData().getCreatedInInfobroker());
        b.placerLocation = getLocationString(req.getPlacer().getLocationData());
        b.fillerLocation = getLocationString(req.getFiller().getLocationData());
        b.infoBrokerId = req.getInfobrokerId();
        b.risId = req.getRisId();
        b.priority = req.getPriority();
        b.referringPhysicianName = getPhysicianName(req);
        b.question = replaceNewlineWithHtmlBreak(req.getQuestion());
        b.anamnesis = replaceNewlineWithHtmlBreak(req.getAnamnesis());
        b.studies = getStudies(req.getExamination());

        return b.buildReferral();
    }

    private List<Study> getStudies(List<Examination> examinations) {
        ArrayList<Study> studies = new ArrayList<Study>();

        for(Examination ex: examinations) {
            String risId = ex.getRisId();

            String code = "";
            String description = "";

            if(ex.getExaminationCode() != null) {
                code = ex.getExaminationCode().getCode();
                description = ex.getExaminationCode().getDescription();
            }

            Date date = getDateFromGregorianCalendar(ex.getDate());
            int noOfImages = sumStudyImages(ex);

            List<StudyReport> studyReports = getStudyReports(ex);
            List<String> dicomSeriesUiids = getDicomSeriesUiid(ex);

            Study stu = new Study(
                risId,
                code,
                description,
                date,
                noOfImages,
                studyReports,
                dicomSeriesUiids
            );

            studies.add(stu);
        }

        return studies;
    }

    private List<String> getDicomSeriesUiid(Examination ex) {
        List<String> dicomSeriesStudyUids = new ArrayList<String>();
        for(DicomStudy dstudy : ex.getDicomStudy()) {
            dicomSeriesStudyUids.add(dstudy.getStudyUid());
        }
        return dicomSeriesStudyUids;
    }

    private List<StudyReport> getStudyReports(Examination ex) {
        List<StudyReport> studyReports = new ArrayList<StudyReport>();

        if(ex.getReports() != null) {
            for(ReportData rep : ex.getReports().getReport()) {
                String status = rep.getStatus().getName();
                Date d = getDateFromGregorianCalendar(rep.getDate());

                StringBuilder sb = new StringBuilder();
                if (rep.getSigner() != null && rep.getSigner().getUserData() != null) {
                    rep.getSigner().getUserData().getFirstName();
                    sb.append(" ");
                    sb.append(rep.getSigner().getUserData().getLastName());
                }
                String signer = sb.toString().trim();
                String text = replaceNewlineWithHtmlBreak(rep.getText());

                studyReports.add(new StudyReport(status,d,signer,text));
            }
        }
        return studyReports;
    }

    private int sumDicomNumImages(Examination examination) {
        int i = 0;
        List<DicomStudy> dicomStudies = examination.getDicomStudy();
        if(dicomStudies != null && dicomStudies.size() > 0) {
            for( DicomStudy study : examination.getDicomStudy()) {
                for(DicomSeries series : study.getDicomSeries()) {
                    i += series.getNumberOfImages().intValue();
                }
            }
        }
        return i;
    }

    private int sumStudyImages(Examination ex) {
        int i = 0;
        for( DicomStudy study : ex.getDicomStudy()) {
            for(DicomSeries series : study.getDicomSeries()) {
                i += series.getNumberOfImages().intValue();
            }
        }
        return i;
    }

    private int sumNumImages(List<Examination> examinations) {
        int i = 0;
        if( examinations != null) {
            for(Examination ex : examinations) {
                i += sumStudyImages(ex);
            }
        }
        return i;
    }

    private String getPhysicianName(Request req) {
        StringBuilder sb = new StringBuilder();
        if (req.getSubmitter() != null && req.getSubmitter().getUserData() != null) {
            sb.append(req.getSubmitter().getUserData().getFirstName());
            sb.append(" ");
            sb.append(req.getSubmitter().getUserData().getLastName());
        }
        return sb.toString();
    }

    private List<String> getStatuses(Request req) {
        List<String> statuses = new ArrayList<String>();
        if(req.getReports() != null) {
            for(ReportData rep : req.getReports().getReport()) {
                statuses.add(rep.getStatus().getName());
            }
        }
        return statuses;
    }

    private Request ibRequest(String brokerId) {
        AttributedURIType attribURIType = new AttributedURIType();

        RequestOrder reqOrder = new RequestOrder();
        reqOrder.setInfobrokerId(brokerId);

        return infoBroker.getRequest(attribURIType, reqOrder);
    }

    private static String getLocationString(LocationData locationData) {
        StringBuilder sb = new StringBuilder();
        if (locationData != null) {
            if (!StringUtils.isEmpty(locationData.getName())) {
                sb.append(locationData.getName());
            }
            if (!StringUtils.isEmpty(locationData.getHospitalName())) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(locationData.getHospitalName());
            }
            if (!StringUtils.isEmpty(locationData.getRegionName())) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(locationData.getRegionName());
            }
        }
        return sb.toString();
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
            returnDate.set(1912, 11, 12, 12, 12);
        }
        return returnDate.getTime();
    }

    private static String replaceNewlineWithHtmlBreak(String source) {
        String result = "";
        if (source != null) {
            result = source.replace(NEWLINE_CHAR, HTML_BREAK);
        }
        return result;
    }

}
