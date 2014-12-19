package se.vgregion.domain.pdl;

public enum RoundedTimeUnit {
    NEAREST_YEAR("År"),
    NEAREST_MONTH("Månader"),
    NEAREST_DAY("Dagar"),
    NEAREST_HOUR("Timmar"),
    NEAREST_HALF_HOUR("Halvtimmar");

    public final String description;

    private RoundedTimeUnit(String description) {

        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
