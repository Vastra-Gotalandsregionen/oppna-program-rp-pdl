package se.vgregion.events;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * Utility class for person id validation etc.
 *
 * @author jonas liljenfeldt
 * @author anders bergkvist
 */
public final class PersonIdUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonIdUtil.class);
    private static final int NO_OF_CHARS_IN_PERSON_ID = 12;

    private PersonIdUtil() {
    }

    /**
     * In order to make valid request to other systems, person id needs to be valid.
     *
     * @param personId
     *            expected format: XXXXXXXXYYYY.
     * @return True if person id is valid.
     */
    public static boolean personIdIsValid(final String personId) {
        boolean isValid = false;

        if (personId != null && personId.length() == NO_OF_CHARS_IN_PERSON_ID) {
            isValid = true;
        } else {
            LOGGER.info("Found invalid person id: " + personId);
        }
        return isValid;
    }

    /**
     * Formats a person id to compact format XXXXXXXXYYYY.
     *
     * @param personId
     *            Person id
     * @return formatted person id
     */
    public static String formatPersonId(final String personId) {
        String ret = "";
        if (personId != null) {
            ret = personId.replace("-", "");
        }
        return ret;
    }


    public static int getAge(String formattedPersonId) {
        String personIdDateString =
                formattedPersonId.substring(0, formattedPersonId.length() - 4);

        try {
            Date personDate = DateUtils.parseDate(personIdDateString, new String[]{"yyyyMMdd"});
            return getAge(personDate, new Date());
        } catch (ParseException e) {
            LOGGER.error("Unable to parse date {}. Returning zero.", personIdDateString, e);
            return 0;
        }
    }

    /**
     * Calculates age.
     *
     * @param personIdDate
     *            Person id as date
     * @param nowDate
     *            Now as date
     * @return age
     */
    public static int getAge(Date personIdDate, Date nowDate) {
        Calendar personIdcalendar = Calendar.getInstance();
        personIdcalendar.setTime(personIdDate);

        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTime(nowDate);

        int res = nowCalendar.get(Calendar.YEAR) - personIdcalendar.get(Calendar.YEAR);
        if ((personIdcalendar.get(Calendar.MONTH) > nowCalendar.get(Calendar.MONTH))
                || (personIdcalendar.get(Calendar.MONTH) == nowCalendar.get(Calendar.MONTH) && personIdcalendar
                .get(Calendar.DAY_OF_MONTH) > nowCalendar.get(Calendar.DAY_OF_MONTH))) {
            res--;
        }
        return res;
    }

    /**
     * Formats person id to display format XX-XXXXXX-YYYY.
     *
     * @param personId
     *            Person id
     * @return formatted person id
     */
    public static String formatDisplayPersonId(String personId) {
        String formattedId = personId;
        if (personId != null && personId.length() == NO_OF_CHARS_IN_PERSON_ID) {
            formattedId = personId.substring(0, 2) + "-" + personId.substring(2, 8) + "-"
                    + personId.substring(8, 12);
        }
        return formattedId;
    }
}
