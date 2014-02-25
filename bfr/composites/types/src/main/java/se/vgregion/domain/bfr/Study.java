package se.vgregion.domain.bfr;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

public class Study implements Serializable {
    private static final long serialVersionUID = 7588464572431043701L;

    public final String risId;
    public final String code;
    public final String description;
    public final Date date;
    public final BigInteger noOfImages;
    public final List<StudyReport> studyReports;
    public final String dicomSeriesStudyUids;

    public Study(
            String risId,
             String code,
             String description,
             Date date,
             BigInteger noOfImages,
             List<StudyReport> studyReports,
             String dicomSeriesStudyUids
    ) {
        this.risId = risId;
        this.code = code;
        this.description = description;
        this.date = date;
        this.noOfImages = noOfImages;
        this.studyReports = studyReports;
        this.dicomSeriesStudyUids = dicomSeriesStudyUids;
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

    public BigInteger getNoOfImages() {
        return noOfImages;
    }

    public List<StudyReport> getStudyReports() {
        return studyReports;
    }

    public String getDicomSeriesStudyUids() {
        return dicomSeriesStudyUids;
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

