package se.vgregion.events.context;

import se.vgregion.events.PersonIdUtil;

import java.io.Serializable;

public class Patient implements Serializable {
    public enum Sex {
        MALE("Man", "Pojke"), FEMALE("Kvinna", "Flicka"), UNKNOWN("Kön okänt"), NODATA("");

        private static final int ADULT_AGE = 18;
        private final String displayName;
        private final String youngDisplayName;

        Sex(String displayName, String youngDisplayName) {
            this.displayName = displayName;
            this.youngDisplayName = youngDisplayName;
        }

        Sex(String displayName) {
            this.displayName = displayName;
            this.youngDisplayName = displayName;
        }

        public String getDisplayName(int age) {
            if(age < ADULT_AGE) {
                return youngDisplayName;
            }
            return displayName;
        }
    }
    private static final long serialVersionUID = -3164297652632714945L;

    public final String patientId;
    public final String patientDisplayName;
    public final Sex sex;
    public final int age;
    public final boolean haveInformation;

    public Patient(String patientId) {
        this.patientId = patientId;
        this.patientDisplayName = "";
        this.sex = Sex.NODATA;
        this.age = PersonIdUtil.getAge(patientId);
        this.haveInformation = false;
    }

    public Patient(String patientId, String patientDisplayName, Sex sex) {
        this.patientId = patientId;
        this.patientDisplayName = patientDisplayName;
        this.sex = sex;
        this.age = PersonIdUtil.getAge(patientId);
        this.haveInformation = true;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getPatientDisplayName() {
        return patientDisplayName;
    }

    public String getSexDisplayName() {
        return sex.getDisplayName(age);
    }

    public boolean isHaveInformation() {
        return haveInformation;
    }

    public int getAge() {
        return age;
    }

    // Format 193404231234 into 19340423-1234
    public String getPatientIdFormatted() {
        return new StringBuilder(patientId).insert(patientId.length()-4, "-").toString();
    }

    public Patient mapPatientInfo(String newName, Sex newSex) {
        return new Patient(patientId, newName, newSex);
    }

    @Override
    public String toString() {
        return "Patient{" +
                "patientId='" + patientId + '\'' +
                ", patientDisplayName='" + patientDisplayName + '\'' +
                ", sex=" + sex +
                ", haveInformation=" + haveInformation +
                '}';
    }
}
