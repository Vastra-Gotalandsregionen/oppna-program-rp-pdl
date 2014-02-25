package se.vgregion.domain.bfr;

import java.io.Serializable;
import java.util.*;

public class Study implements Serializable {
    private static final long serialVersionUID = 7588464572431043701L;

    public final String risId;
    public final String code;
    public final String description;
    public final Date date;
    public final int noOfImages;
    public final List<StudyReport> studyReports;
    public final List<String> dicomSeriesStudyUids;
    public final List<String> studyUrls;

    public static final Comparator<Study> studyDateDescendingComparator = new Comparator<Study>() {
        public int compare(Study study1, Study study2) {
            Date date1 = study1.getDate();
            Date date2 = study2.getDate();
            if (date1 == null && date2 == null) {
                return 0;
            } else if (date2 == null) {
                return -1;
            } else if (date1 == null) {
                return 1;
            }
            return date2.compareTo(date1);
        }
    };

    public Study(
             String risId,
             String code,
             String description,
             Date date,
             int noOfImages,
             List<StudyReport> studyReports,
             List<String> dicomSeriesStudyUids
    ) {
        this.risId = risId;
        this.code = code;
        this.description = description;
        this.date = date;
        this.noOfImages = noOfImages;
        this.studyReports = Collections.unmodifiableList(studyReports);
        this.dicomSeriesStudyUids = Collections.unmodifiableList(dicomSeriesStudyUids);
        this.studyUrls = new ArrayList<String>();
    }

    private Study(Study prev, List<String> urls ) {
        this.risId = prev.risId;
        this.code = prev.code;
        this.description = prev.description;
        this.date = prev.date;
        this.noOfImages = prev.noOfImages;
        this.studyReports = prev.studyReports;
        this.dicomSeriesStudyUids = prev.dicomSeriesStudyUids;
        this.studyUrls = Collections.unmodifiableList(urls);
    }

    public String getRisId() {
        return risId;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public Date getDate() {
        return date;
    }

    public int getNoOfImages() {
        return noOfImages;
    }

    public List<StudyReport> getStudyReports() {
        return studyReports;
    }

    public List<String> getDicomSeriesStudyUids() {
        return dicomSeriesStudyUids;
    }

    public List<String> getStudyUrls() {
        return studyUrls;
    }

    public Study mapStudyUrls( List<String> urls ) {
        return new Study(this, urls);
    }

    @Override
    public String toString() {
        return "Study{" +
                "risId='" + risId + '\'' +
                ", code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", noOfImages=" + noOfImages +
                ", studyReports=" + studyReports +
                ", dicomSeriesStudyUids='" + dicomSeriesStudyUids + '\'' +
                '}';
    }
}

