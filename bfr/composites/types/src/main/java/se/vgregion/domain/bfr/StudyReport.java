package se.vgregion.domain.bfr;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Comparator;
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
        this.displayDate = date == null ? "- ? -" : DateFormatUtils.format(date, "yyyy-MM-dd HH:mm");
        this.signer = signer;
        this.text = text;
    }


    public static final Comparator<StudyReport> studyReportDateDescendingComparator = new Comparator<StudyReport>() {
        public int compare(StudyReport study1, StudyReport study2) {
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
