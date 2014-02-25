package se.vgregion.domain.bfr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Referral implements Serializable {
    private static final long serialVersionUID = -842588744537632710L;

    public final String displayId;
    public final List<String> statusList;
    public final int imageCount;
    public final Date placingDate;
    public final String placerLocation; // The unit that placed the request (the department of the referring physician)
    public final String fillerLocation; // The unit that registered the request (the radiology department)
    public final String infoBrokerId;
    public final String risId;
    public final String priority;
    public final String referringPhysicianName;
    public final String question;
    public final String anamnesis;
    public final List<Study> studies;

    protected Referral(ReferralBuilder referralBuilder) {
        this.displayId = java.util.UUID.randomUUID().toString();
        this.statusList = Collections.unmodifiableList(referralBuilder.statusList);
        this.imageCount = referralBuilder.imageCount;
        this.placingDate = referralBuilder.placingDate;
        this.placerLocation = referralBuilder.placerLocation;
        this.fillerLocation = referralBuilder.fillerLocation;
        this.infoBrokerId = referralBuilder.infoBrokerId;
        this.risId = referralBuilder.risId;
        this.priority = referralBuilder.priority;
        this.referringPhysicianName = referralBuilder.referringPhysicianName;
        this.question = referralBuilder.question;
        this.anamnesis = referralBuilder.anamnesis;
        this.studies = Collections.unmodifiableList(referralBuilder.studies);
    }

    private Referral(Referral referral, List<Study> studies) {
        this.displayId = referral.displayId;
        this.statusList = referral.statusList;
        this.imageCount = referral.imageCount;
        this.placingDate = referral.placingDate;
        this.placerLocation = referral.placerLocation;
        this.fillerLocation = referral.fillerLocation;
        this.infoBrokerId = referral.infoBrokerId;
        this.risId = referral.risId;
        this.priority = referral.priority;
        this.referringPhysicianName = referral.referringPhysicianName;
        this.question = referral.question;
        this.anamnesis = referral.anamnesis;
        this.studies = Collections.unmodifiableList(studies);
    }

    public List<String> getStatusList() {
        return statusList;
    }

    public int getImageCount() {
        return imageCount;
    }

    public Date getPlacingDate() {
        return placingDate;
    }

    public String getPlacerLocation() {
        return placerLocation;
    }

    public String getFillerLocation() {
        return fillerLocation;
    }

    public String getInfoBrokerId() {
        return infoBrokerId;
    }

    public String getRisId() {
        return risId;
    }

    public String getPriority() {
        return priority;
    }

    public String getReferringPhysicianName() {
        return referringPhysicianName;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnamnesis() {
        return anamnesis;
    }

    public List<Study> getStudies() {
        return studies;
    }

    public String getDisplayId() {
        return displayId;
    }

    public Referral mapStudies(ArrayList<Study> studies) {
        return new Referral(this, studies);
    }

    @Override
    public String toString() {
        return "Referral{" +
                "displayId='" + displayId + '\'' +
                ", statusList=" + statusList +
                ", imageCount=" + imageCount +
                ", placingDate=" + placingDate +
                ", placerLocation='" + placerLocation + '\'' +
                ", fillerLocation='" + fillerLocation + '\'' +
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
