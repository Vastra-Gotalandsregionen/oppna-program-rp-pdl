package se.vgregion.domain.bfr;

import java.util.Date;

public class StudyReport {
    public final String status;
    public final Date date;
    public final String signer;
    public final String text;

    public StudyReport(
            String status,
            Date date,
            String signer,
            String text) {
        this.status = status;
        this.date = date;
        this.signer = signer;
        this.text = text;
    }

    public String getStatus() {
        return status;
    }

    public Date getDate() {
        return date;
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
                ", signer='" + signer + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
