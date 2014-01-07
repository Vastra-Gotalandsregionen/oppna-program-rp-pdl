package se.vgregion.domain.assignment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum AccessActivity {
    READ("Läsa"),
    WRITE("Skriva"),
    SIGN("Signera"),
    UNKNOWN("Okänd");

    public final String value;

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessActivity.class.getName());


    private AccessActivity(String value) {
        this.value = value;
    }

    public static AccessActivity getByValue(String value) {
        for(AccessActivity val : AccessActivity.values()) {
            if(val.value.equalsIgnoreCase(value)) {
                return val;
            }
        }

        LOGGER.error("Value unkown type, got " + value + ", expected Läsa, Skriva or Signera.");
        return UNKNOWN;
    }
}
