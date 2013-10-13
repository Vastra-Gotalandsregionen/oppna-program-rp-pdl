package se.vgregion.domain.pdl;

public class CheckedConsent {
    public final PdlReport.ConsentType consentType;
    public final boolean hasConsent;

    public CheckedConsent(PdlReport.ConsentType consentType, boolean hasConsent) {
        this.consentType = consentType;
        this.hasConsent = hasConsent;
    }
}
