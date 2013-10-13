package se.vgregion.domain.pdl;

public class PatientEngagement {
    public enum InformationType {
        LAK("Läkemedel - Ordination/förskrivning"), UPP("Uppmärksamhetsinformation"),
        OTHR("Annan information");

        private final String desc;

        InformationType( String desc ) {
            this.desc = desc;
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName().toLowerCase();
        }
    }

    public final String careProviderHsaId;
    public final String careUnitHsaId;
    public final String employeeHsaId;
    public final InformationType informationType;

    public PatientEngagement(
            String careProviderHsaId,
            String careUnitHsaId,
            String employeeHsaId,
            InformationType informationType) {
        this.careProviderHsaId = careProviderHsaId;
        this.careUnitHsaId = careUnitHsaId;
        this.employeeHsaId = employeeHsaId;
        this.informationType = informationType;
    }
}
