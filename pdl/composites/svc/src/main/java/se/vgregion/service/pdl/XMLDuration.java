package se.vgregion.service.pdl;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import se.vgregion.domain.pdl.RoundedTimeUnit;

class XMLDuration {
    public final XMLGregorianCalendar startDate;
    public final XMLGregorianCalendar endDate;

    private static final Logger LOGGER = LoggerFactory.getLogger(XMLDuration.class.getName());


    public XMLDuration(int duration, RoundedTimeUnit timeUnit) {
        startDate = currentDateAsXML();
        endDate = calculateEndDate(duration, timeUnit);
    }

    private static XMLGregorianCalendar calculateEndDate(int duration, RoundedTimeUnit timeUnit) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTimeZone(TimeZone.getTimeZone("Europe/Stockholm"));

        try {
            switch (timeUnit) {
                case NEAREST_YEAR:
                    Date yearAdded = DateUtils.addYears(new Date(), duration);
                    Date yearRounded = DateUtils.ceiling(yearAdded, Calendar.YEAR);
                    Date yearTruncated = DateUtils.truncate(yearRounded, Calendar.YEAR);
                    c.setTime(yearTruncated);

                    return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
                case NEAREST_MONTH:
                    Date monthAdded = DateUtils.addMonths(new Date(), duration);
                    Date monthRounded = DateUtils.ceiling(monthAdded, Calendar.MONTH);
                    Date monthTruncated = DateUtils.truncate(monthRounded, Calendar.MONTH);
                    c.setTime(monthTruncated);

                    return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
                case NEAREST_DAY:
                    Date dayAdded = DateUtils.addDays(new Date(), duration);
                    Date dayRounded = DateUtils.ceiling(dayAdded, Calendar.DAY_OF_MONTH);
                    Date dayTruncated = DateUtils.truncate(dayRounded, Calendar.DAY_OF_MONTH);
                    c.setTime(dayTruncated);

                    return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
                case NEAREST_HOUR:
                    Date hourAdded = DateUtils.addHours(new Date(), duration);
                    Date hourRounded = DateUtils.ceiling(hourAdded, Calendar.HOUR);
                    Date hourTruncated = DateUtils.truncate(hourRounded, Calendar.HOUR);
                    c.setTime(hourTruncated);

                    return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
                case NEAREST_HALF_HOUR:
                    int minutes = duration * 30;
                    Date halfHourAdded = DateUtils.addMinutes(new Date(), minutes);
                    c.setTime(halfHourAdded);
                    int currMinutes = c.get(Calendar.MINUTE);
                    if( currMinutes > 30 ) {
                        Date halfHourCeiling = DateUtils.ceiling(halfHourAdded, Calendar.HOUR);
                        Date halfHourTruncated = DateUtils.truncate(halfHourCeiling, Calendar.MINUTE);
                        c.setTime(halfHourTruncated);
                    } else {
                        int addMinutes = 30 - currMinutes;
                        Date halfHourCeiling = DateUtils.addMinutes(halfHourAdded, addMinutes);
                        Date halfHourTruncated = DateUtils.truncate(halfHourCeiling, Calendar.MINUTE);
                        c.setTime(halfHourTruncated);
                    }
                    return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
                default:
                    XMLGregorianCalendar currentDate = currentDateAsXML();
                    LOGGER.error("Unknown time unit {}, fallback to date {}. This requires code a change to be corrected.", timeUnit, currentDate );
                    return currentDateAsXML(); // To make the java compiler happy
            }
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException("Unable to create XMLDate", e);
        }
    }

    static XMLGregorianCalendar currentDateAsXML() {
        try {
            GregorianCalendar c = new GregorianCalendar();
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException("Unable to create XMLDate", e);
        }
    }

    @Override
    public String toString() {
        return "XMLDuration{" +
                "startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
