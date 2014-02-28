package se.vgregion.events.context.sources.radiology;

import org.apache.commons.lang.time.DateFormatUtils;
import se.vgregion.events.context.SourceReferences;

import java.util.Date;

public class RadiologySourceRefs implements SourceReferences {

    public static final String SYSTEM_ID = "bfr";
    private static final long serialVersionUID = 6013888161146061222L;

    public final Date requestDate;
    public final String requestDisplayDate;
    public final int numImages;
    public final String id;
    public final String careUnitDisplayName;
    public final String orgUnitDisplayName;
    public final String examinations;
    public final String status;
    public final String infoBrokerId;

    public RadiologySourceRefs(
            Date requestDate,
            int numImages,
            String careUnitDisplayName,
            String orgUnitDisplayName,
            String examinations,
            String status,
            String infoBrokerId
    ) {
        this.id = java.util.UUID.randomUUID().toString();
        this.requestDate = requestDate;
        this.requestDisplayDate = DateFormatUtils.format(requestDate, "yyyy-MM-dd HH:mm");
        this.numImages = numImages;
        this.careUnitDisplayName = careUnitDisplayName;
        this.orgUnitDisplayName = orgUnitDisplayName;
        this.examinations = examinations;
        this.status = status;
        this.infoBrokerId = infoBrokerId;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public String getRequestDisplayDate() {
        return requestDisplayDate;
    }

    public int getNumImages() {
        return numImages;
    }

    public String getCareUnitDisplayName() {
        return careUnitDisplayName;
    }

    public String getOrgUnitDisplayName() {
        return orgUnitDisplayName;
    }

    public String getExaminations() {
        return examinations;
    }

    public String getStatus() {
        return status;
    }

    public String getInfoBrokerId() {
        return infoBrokerId;
    }

    public String getId() {
        return id;
    }

    @Override
    public String targetCareSystem() {
        return SYSTEM_ID;
    }

    @Override
    public SourceReferences combine(SourceReferences references) {

        return null;
    }

    @Override
    public String toString() {
        return "RadiologySourceRefs{" +
                "requestDate=" + requestDate +
                ", numImages=" + numImages +
                ", careUnitDisplayName='" + careUnitDisplayName + '\'' +
                ", orgUnitDisplayName='" + orgUnitDisplayName + '\'' +
                ", examinations='" + examinations + '\'' +
                ", status='" + status + '\'' +
                ", infoBrokerId='" + infoBrokerId + '\'' +
                '}';
    }
}
