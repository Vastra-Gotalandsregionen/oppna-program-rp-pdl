package se.vgregion.domain.bfr;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

public class StudyReport {
    public final String status;
    public final Date date;
    public final String displayDate;
    public final String signer;
    public final String text;

    public StudyReport(
            String status,
            Date date,
            String signer,
            String text) {
        this.status = status;
        this.date = date;
        this.displayDate = DateFormatUtils.format(date, "yyyy-MM-dd HH:mm");
        this.signer = signer;
        this.text = text;
    }

    public String getStatus() {
        return status;
    }

    public Date getDate() {
        return date;
    }

    public String getDisplayDate() {
        return displayDate;
    }

    public String getSigner() {
        return signer;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "StudyReport{" +
                "status='" + status + '\'' +
                ", date=" + date +
                ", displayDate='" + displayDate + '\'' +
                ", signer='" + signer + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
