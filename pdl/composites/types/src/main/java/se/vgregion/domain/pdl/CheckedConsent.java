package se.vgregion.domain.pdl;

import java.io.Serializable;

public class CheckedConsent implements Serializable {
    private static final long serialVersionUID = 7700912160877500493L;

    public final PdlReport.ConsentType consentType;
    public final Boolean hasConsent;

    public CheckedConsent(PdlReport.ConsentType consentType, Boolean hasConsent) {
        this.consentType = consentType;
        this.hasConsent = hasConsent;
    }

    public PdlReport.ConsentType getConsentType() {
        return consentType;
    }

    public Boolean getHasConsent() {
        return hasConsent;
    }

    @Override
    public String toString() {
        return "CheckedConsent{" +
                "consentType=" + consentType +
                ", hasConsent=" + hasConsent +
                '}';
    }
}
