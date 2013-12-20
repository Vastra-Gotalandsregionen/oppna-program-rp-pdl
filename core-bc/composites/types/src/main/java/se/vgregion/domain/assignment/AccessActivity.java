package se.vgregion.domain.assignment;

import se.vgregion.domain.decorators.WithOutcome;

public enum AccessActivity {
    READ("Läsa"),
    WRITE("Skriva"),
    SIGN("Signera");

    public final String value;

    private AccessActivity(String value) {
        this.value = value;
    }

    public static WithOutcome<AccessActivity> getByValue(String value) {
        for(AccessActivity val : AccessActivity.values()) {
            if(val.value.equalsIgnoreCase(value)) {
                return WithOutcome.success(val);
            }
        }

        return WithOutcome.clientError(null);
    }
}
