package se.vgregion.domain.bfr;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

public class Refferal implements Serializable {
    private static final long serialVersionUID = -842588744537632710L;

    public final List<String> statusList;
    public final List<String> examinationDescriptionList;
    public final BigInteger imageCount;
    public final Date placingDate;
    public final String placerLocation; // The unit that placed the request (the department of the referring physician)
    public final String fillerLocation; // The unit that registered the request (the radiology department)
    public final String comment;
    public final String lastColumnUnknown;
    public final String infoBrokerId;
    public final String risId;

    public final String priority;
    public final String referringPhysicianName;
    public final String question;
    public final String anamnesis;
    public final List<Study> studies;

    public Refferal(
            List<String> statusList,
            List<String> examinationDescriptionList,
            BigInteger imageCount,
            Date placingDate,
            String placerLocation,
            String fillerLocation,
            String comment,
            String lastColumnUnknown,
            String infoBrokerId,
            String risId,
            String priority,
            String referringPhysicianName,
            String question,
            String anamnesis,
            List<Study> studies
    ) {
        this.statusList = statusList;
        this.examinationDescriptionList = examinationDescriptionList;
        this.imageCount = imageCount;
        this.placingDate = placingDate;
        this.placerLocation = placerLocation;
        this.fillerLocation = fillerLocation;
        this.comment = comment;
        this.lastColumnUnknown = lastColumnUnknown;
        this.infoBrokerId = infoBrokerId;
        this.risId = risId;
        this.priority = priority;
        this.referringPhysicianName = referringPhysicianName;
        this.question = question;
        this.anamnesis = anamnesis;
        this.studies = studies;
    }

    @Override
    public String toString() {
        return "Refferal{" +
                "statusList=" + statusList +
                ", examinationDescriptionList=" + examinationDescriptionList +
                ", imageCount=" + imageCount +
                ", placingDate=" + placingDate +
                ", placerLocation='" + placerLocation + '\'' +
                ", fillerLocation='" + fillerLocation + '\'' +
                ", comment='" + comment + '\'' +
                ", lastColumnUnknown='" + lastColumnUnknown + '\'' +
                ", infoBrokerId='" + infoBrokerId + '\'' +
                ", risId='" + risId + '\'' +
                ", priority='" + priority + '\'' +
                ", referringPhysicianName='" + referringPhysicianName + '\'' +
                ", question='" + question + '\'' +
                ", anamnesis='" + anamnesis + '\'' +
                ", studies=" + studies +
                '}';
    }
}
