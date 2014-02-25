package se.vgregion.domain.bfr;

import java.util.Date;
import java.util.List;

public class ReferralBuilder {
    private static final long serialVersionUID = 2439473899061905297L;

    public List<String> statusList;
    public List<String> examinationDescriptionList;
    public int imageCount;
    public Date placingDate;
    public String placerLocation; // The unit that placed the request (the department of the referring physician)
    public String fillerLocation; // The unit that registered the request (the radiology department)
    public String infoBrokerId;
    public String risId;
    public String priority;
    public String referringPhysicianName;
    public String question;
    public String anamnesis;
    public List<Study> studies;

    public Referral buildReferral() {
        return new Referral(this);
    }
}
