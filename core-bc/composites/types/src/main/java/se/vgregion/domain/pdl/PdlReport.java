package se.vgregion.domain.pdl;

import java.io.Serializable;
import java.util.ArrayList;

public class PdlReport implements Serializable {
    private static final long serialVersionUID = -597284170511725549L;
    public final WithFallback<CheckedConsent> consent;
    public final WithFallback<Boolean> hasRelationship;
    public final WithFallback<ArrayList<WithInfoType<WithBlock<CareSystem>>>> systems;

    public PdlReport(
            WithFallback<ArrayList<WithInfoType<WithBlock<CareSystem>>>> checkedSystems,
            WithFallback<CheckedConsent> checkedConsent,
            WithFallback<Boolean> hasRelationship
    ) {
        this.hasRelationship = hasRelationship;
        this.systems = checkedSystems;
        this.consent = checkedConsent;
    }

    private WithFallback<Boolean> isConsentWithFallback(WithFallback<CheckedConsent> checkedConsent) {
        if (checkedConsent.fallback) {
            return WithFallback.fallback(checkedConsent.value.hasConsent);
        } else {
            return WithFallback.success(checkedConsent.value.hasConsent);
        }
    }


    public PdlReport withBlocks(WithFallback<ArrayList<WithInfoType<WithBlock<CareSystem>>>> unblockedInformation) {
        return new PdlReport(
                unblockedInformation,
                consent,
                hasRelationship);
    }

    public PdlReport withRelationship(WithFallback<Boolean> newHasRelationship) {
        return new PdlReport(
                systems,
                consent,
                newHasRelationship);
    }

    public PdlReport withConsent(WithFallback<CheckedConsent> newConsent) {
        return new PdlReport(
                systems,
                newConsent,
                hasRelationship);
    }

    public WithFallback<ArrayList<WithInfoType<WithBlock<CareSystem>>>> getSystems() {
        return systems;
    }

    public WithFallback<CheckedConsent> getConsent() {
        return consent;
    }


    public WithFallback<Boolean> getHasRelationship() {
        return hasRelationship;
    }

    @Override
    public String toString() {
        return "PdlReport{" +
                "consent=" + consent +
                ", hasRelationship=" + hasRelationship +
                ", systems=" + systems +
                '}';
    }

    public enum ConsentType {
        Consent, Emergency
    }
}
