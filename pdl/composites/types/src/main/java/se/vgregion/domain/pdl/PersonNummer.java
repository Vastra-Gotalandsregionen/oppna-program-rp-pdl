/**
 * Copyright 2010 Västra Götalandsregionen
 *
 *   This library is free software; you can redistribute it and/or modify
 *   it under the terms of version 2.1 of the GNU Lesser General Public
 *   License as published by the Free Software Foundation.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the
 *   Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *   Boston, MA 02111-1307  USA
 *
 */

package se.vgregion.domain.pdl;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Full implementation of the swedish person identifier number.
 * Used by Patient event.
 *
 * @author <a href="mailto:david.rosell@redpill-linpro.com">David Rosell</a>
 */
public final class PersonNummer implements Serializable {
    private static final long serialVersionUID = 7763500091845636827L;

    /**
     * Gender enumeration.
     */
    public enum Gender {
        MALE, FEMALE
    }

    /**
     * PersonNumber Type enumeration.
     * This type is determined by the input string, and spans from if it can be interpreted as
     * a person number at all, to how the input string where broken down.
     */
    public enum Type {
        SHORT, NORMAL, FULL_NO, FULL, FAKE_FULL_NO, INVALID
    }

    private int century;
    private int year;
    private int month;
    private int day;
    private String birthNumber;
    private int checkNumber;
    private String separator; // or "+" if age over 100

    private boolean checkNumberValid = false;
    private int calculatedCheckNumber;
    private boolean monthValid = false;
    private boolean dayValid = false;
    private Gender gender;
    private Type type;
    private String numberText;

    private PersonNummer(String personnummer) {
        setNumberText(personnummer);
    }

    /**
     * Factory method for PersonNumber.
     *
     * @param personnummer String to resolve to PersonNumber
     * @return PersonNumber
     */
    public static PersonNummer personummer(String personnummer) {
        PersonNummer pNo = new PersonNummer(personnummer);
        pNo.initYear();
        pNo.initSeparator();
        pNo.initCentury();
        pNo.initMonth();
        pNo.initDay();
        pNo.initBirthNumber();
        pNo.initCheckNumber();
        pNo.initGender();

        return pNo;
    }

    /**
     * Get the shortest possible String representation of the PersonNumber.
     *
     * @return String
     */
    public String getShort() {
        if (type == Type.INVALID) {
            return String.format("INVALID [%s]", numberText);
        }
        if (separator.equals("-")) {
            return String.format("%02d%02d%02d%s%d", year, month, day, birthNumber, checkNumber);
        } else {
            return String.format("%02d%02d%02d%02d%s%d", century, year, month, day, birthNumber, checkNumber);
        }
    }

    /**
     * Get the standard string representation of the PersonNumber.
     *
     * @return String
     */
    public String getNormal() {
        if (type == Type.INVALID) {
            return String.format("INVALID [%s]", numberText);
        }
        return String.format("%02d%02d%02d%s%s%d", year, month, day, separator, birthNumber, checkNumber);
    }

    /**
     * Get the full string representation of the PersonNumber.
     *
     * @return String
     */
    public String getFull() {
        if (type == Type.INVALID) {
            return String.format("INVALID [%s]", numberText);
        }
        return String.format("%02d%02d%02d%02d-%s%d", century, year, month, day, birthNumber, checkNumber);
    }

    /**
     * Get the full string representation of the PersonNumber without the separator character.
     *
     * @return String
     */
    public String getFullNo() {
        if (type == Type.INVALID) {
            return String.format("INVALID [%s]", numberText);
        }
        return String.format("%02d%02d%02d%02d%s%d", century, year, month, day, birthNumber, checkNumber);
    }

    private void setNumberText(String numberText) {
        this.numberText = numberText;
        this.initType();
    }

    private void initGender() {
        if (type == Type.INVALID || type == Type.FAKE_FULL_NO) {
            gender = null;
        } else {
            gender = (Integer.parseInt(birthNumber) % 2 == 0) ? Gender.FEMALE : Gender.MALE;
        }
    }

    private void initCheckNumber() {
        if (type == Type.INVALID) {
            checkNumber = -1;
            calculatedCheckNumber = -1;
            checkNumberValid = false;
        } else if (type == Type.FAKE_FULL_NO) {
            checkNumber = Integer.parseInt(numberText.substring(numberText.length() - 1));
            checkNumberValid = true;
        } else {
            checkNumber = Integer.parseInt(numberText.substring(numberText.length() - 1));
            calculatedCheckNumber = checkDigitCalculator(getShort());
            checkNumberValid = (calculatedCheckNumber == checkNumber);
        }
    }

    private void initBirthNumber() {
        if (type == Type.INVALID) {
            birthNumber = "-1";
        }

        if (type == Type.SHORT) {
            birthNumber = numberText.substring(6, 9);
        }

        if (type == Type.NORMAL) {
            birthNumber = numberText.substring(7, 10);
        }

        if (type == Type.FULL_NO) {
            birthNumber = numberText.substring(8, 11);
        }

        if (type == Type.FAKE_FULL_NO) {
            birthNumber = numberText.substring(8, 11);
        }

        if (type == Type.FULL) {
            birthNumber = numberText.substring(9, 12);
        }
    }

    private void initDay() {
        if (type == Type.INVALID) {
            day = -1;
        }

        if (type == Type.SHORT || type == Type.NORMAL) {
            day = Integer.parseInt(numberText.substring(4, 6));
        }

        if (type == Type.FULL_NO || type == Type.FULL || type == Type.FAKE_FULL_NO) {
            day = Integer.parseInt(numberText.substring(6, 8));
        }

        // is day valid
        String datePart = String.format("%02d%02d%02d%02d", century, year, month, day);
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        try {
            Date d = df.parse(datePart);
            String back = df.format(d);

            dayValid = datePart.equals(back);
        } catch (ParseException e) {
            // never happends
            e.printStackTrace();
        }
    }

    private void initMonth() {
        if (type == Type.INVALID) {
            month = -1;
        }

        if (type == Type.SHORT || type == Type.NORMAL) {
            month = Integer.parseInt(numberText.substring(2, 4));
        }

        if (type == Type.FULL_NO || type == Type.FULL || type == Type.FAKE_FULL_NO) {
            month = Integer.parseInt(numberText.substring(4, 6));
        }

        monthValid = (month > 0 && month < 13) ? true : false;
    }

    private void initSeparator() {
        if (type == Type.INVALID) {
            separator = null;
        }

        if (type == Type.SHORT) {
            separator = "-";
        }

        if (type == Type.NORMAL) {
            separator = numberText.substring(6, 7);
        }

        if (type == Type.FULL_NO || type == Type.FULL || type == Type.FAKE_FULL_NO) {
            Calendar cal = Calendar.getInstance();
            int thisFullYear = cal.get(Calendar.YEAR);
            int fullYear = Integer.parseInt(numberText.substring(0, 4));

            separator = (thisFullYear - fullYear >= 100) ? "+" : "-";
        }
    }

    /**
     * Use separator and year to determine century - Never use indata directly.
     */
    private void initCentury() {
        if (type == Type.INVALID) {
            century = -1;
        }

        Calendar cal = Calendar.getInstance();
        int thisCentury = cal.get(Calendar.YEAR) / 100;
        int thisYear = cal.get(Calendar.YEAR) % 100;

        if (Type.FULL.equals(type) || Type.FULL_NO.equals(type) || Type.FAKE_FULL_NO.equals(type)) {
            // determine century by numberText
            century = Integer.parseInt(numberText.substring(0, 2));
        } else {
            // determine century by separator sign
            if ("-".equals(separator)) {
                if (year > thisYear) {
                    century = thisCentury - 1;
                } else {
                    century = thisCentury;
                }
            }

            if ("+".equals(separator)) {
                if (year > thisYear) {
                    century = thisCentury - 2;
                } else {
                    century = thisCentury - 1;
                }
            }
        }
    }

    private void initYear() {
        if (type == Type.INVALID) {
            year = -1;
        }

        if (type == Type.SHORT || type == Type.NORMAL) {
            year = Integer.parseInt(numberText.substring(0, 2));
        }

        if (type == Type.FULL_NO || type == Type.FULL || type == Type.FAKE_FULL_NO) {
            year = Integer.parseInt(numberText.substring(2, 4));
        }
    }

    private void initType() {
        Type tmpType;

        if (numberText == null) {
            tmpType = Type.INVALID;
        } else {
            switch (numberText.length()) {
                case 10:
                    tmpType = numberText.matches("\\d{10}") ? Type.SHORT : Type.INVALID;
                    break;
                case 11:
                    tmpType = numberText.matches("\\d{6}[-|+]\\d{4}") ? Type.NORMAL : Type.INVALID;
                    break;
                case 12:
                    tmpType = numberText.matches("\\d{12}") ? Type.FULL_NO : Type.INVALID;
                    if (tmpType.equals(Type.INVALID)) {
                        tmpType = numberText.matches("[\\dA-Z]{12}") ? Type.FAKE_FULL_NO : Type.INVALID;
                    }
                    break;
                case 13:
                    tmpType = numberText.matches("\\d{8}[-|+]\\d{4}") ? Type.FULL : Type.INVALID;
                    break;
                default:
                    tmpType = Type.INVALID;
            }
        }

        this.type = tmpType;
    }

    // valid input:
    // 6509124696
    // 650912-4696
    // 650912+4696
    // 196509124696
    // 19650912-4696
    // 18650912-4696
    // 18650912+4696

    // output:
    // 6509124696 - SHORT
    // 650912-4696 - NORMAL if age < 100
    // 650912+4696 - NORMAL if age > 100
    // 19650912-4696 - LONG
    // 18650912-4696 - LONG

    /**
     * Static calculator for the checknumber digit.
     *
     * @param shortFormat Use short string representation for calculation.
     * @return String
     */
    public static int checkDigitCalculator(String shortFormat) {
        long pnr = Long.parseLong(shortFormat);

        // number sum [0...18]
        int[] numberSum = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9};
//        int checkNumber = (int) (pnr % 10);

        int sum = 0;
        for (int i = 1; i <= 9; i++) {
            pnr = pnr / 10; // strip last digit

            int digit = (int) (pnr % 10);
            int multiplyer = ((i % 2) + 1); // multiplyer is [2 1 2 1 2 1 2 1 2]

            digit = digit * multiplyer;
            sum = sum + numberSum[digit];
        }

        int checkDigit = (10 - (sum % 10)) % 10;

        return checkDigit;
    }

    /**
     * Access century part.
     *
     * @return Integer [0..99]
     */
    public int getCentury() {
        return century;
    }

    /**
     * Access year part.
     *
     * @return Integer [0..99]
     */
    public int getYear() {
        return year;
    }

    /**
     * Access Month part.
     *
     * @return Integer [0...99]
     */
    public int getMonth() {
        return month;
    }

    /**
     * Access day part.
     *
     * @return Integer [1,2...31]
     */
    public int getDay() {
        return day;
    }

    /**
     * Access birthnumber part.
     *
     * @return Integer, a 3 digit number
     */
    public String getBirthNumber() {
        return birthNumber;
    }

    /**
     * Access check number part.
     *
     * @return Integer, one digit
     */
    public int getCheckNumber() {
        return checkNumber;
    }

    /**
     * Access separator character in normal string representation.
     *
     * @return "-" if younger than 100 year, "+" if older than 100 year.
     */
    public String getSeparator() {
        return separator;
    }

    /**
     * Does the given check number correspond with the caluclated value.
     *
     * @return true/false
     */
    public boolean isCheckNumberValid() {
        return checkNumberValid;
    }

    /**
     * Access the calculated checknumber.
     * May be different from the given check number.
     *
     * @return The calculated checknumber.
     */
    public int getCalculatedCheckNumber() {
        return calculatedCheckNumber;
    }

    /**
     * Is the given month a possible month - [1..12].
     *
     * @return true/false
     */
    public boolean isMonthValid() {
        return monthValid;
    }

    /**
     * Does the given day calculated from [year, month, day] really exist.
     *
     * @return true/false
     */
    public boolean isDayValid() {
        return dayValid;
    }

    /**
     * What gender does the given number correspond to.
     *
     * @return MALE/FEMALE
     */
    public Gender getGender() {
        return gender;
    }

    /**
     * How where the input number text classified.
     *
     * @return [INVALID, NORMAL ...]
     */
    public Type getType() {
        return type;
    }

    /**
     * Compate PersonNumbers.
     *
     * @param o PersonNumber.
     * @return true/false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PersonNummer)) {
            return false;
        }

        PersonNummer that = (PersonNummer) o;

        if (type == Type.INVALID || that.type == Type.INVALID) {
            return false;
        }

        if (!birthNumber.equals(that.birthNumber)) {
            return false;
        }
        if (century != that.century) {
            return false;
        }
        if (checkNumber != that.checkNumber) {
            return false;
        }
        if (day != that.day) {
            return false;
        }
        if (month != that.month) {
            return false;
        }
        if (year != that.year) {
            return false;
        }
        if (separator != null ? !separator.equals(that.separator) : that.separator != null) {
            return false;
        }

        return true;
    }

    /**
     * HashCode defined for use in set's and map's.
     *
     * @return Integer.
     */
    @Override
    public int hashCode() {
        if (type == Type.INVALID) {
            return 0;
        } else {
            int result = century;
            result = 31 * result + year;
            result = 31 * result + month;
            result = 31 * result + day;
            result = 31 * result + birthNumber.hashCode();
            result = 31 * result + checkNumber;
            return result;
        }
    }

    /**
     * ToString used for debugging purpose.
     * Use getShort(), getNormal() and getFull() for display purposes.
     *
     * @return String representation of the object.
     */
    @Override
    public String toString() {
        if (type == Type.INVALID) {
            return new ToStringBuilder(this).
                    append("personNummer", "INVALID").
                    append("numberText", numberText).
                    toString();
        } else {
            return new ToStringBuilder(this).
                    append("personNummer", getNormal()).
                    toString();
        }
    }
}
