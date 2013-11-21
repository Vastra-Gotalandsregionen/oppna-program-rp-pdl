package se.vgregion.domain.pdl;

import se.vgregion.domain.pdl.decorators.WithOutcome;
import se.vgregion.domain.pdl.decorators.WithBlock;
import se.vgregion.domain.pdl.decorators.WithInfoType;

import java.io.Serializable;
import java.util.ArrayList;

public class PdlReport implements Serializable {
    private static final long serialVersionUID = -597284170511725549L;
    public final WithOutcome<CheckedConsent> consent;
    public final WithOutcome<Boolean> hasRelationship;
    public final WithOutcome<ArrayList<WithInfoType<WithBlock<CareSystem>>>> systems;

    public PdlReport(
            WithOutcome<ArrayList<WithInfoType<WithBlock<CareSystem>>>> checkedSystems,
            WithOutcome<CheckedConsent> checkedConsent,
            WithOutcome<Boolean> hasRelationship
    ) {
        this.hasRelationship = hasRelationship;
        this.systems = checkedSystems;
        this.consent = checkedConsent;
    }

    public PdlReport withBlocks(WithOutcome<ArrayList<WithInfoType<WithBlock<CareSystem>>>> unblockedInformation) {
        return new PdlReport(
                unblockedInformation,
                consent,
                hasRelationship);
    }

    public PdlReport withRelationship(WithOutcome<Boolean> newHasRelationship) {
        return new PdlReport(
                systems,
                consent,
                newHasRelationship);
    }

    public PdlReport withConsent(WithOutcome<CheckedConsent> newConsent) {
        return new PdlReport(
                systems,
                newConsent,
                hasRelationship);
    }

    public WithOutcome<ArrayList<WithInfoType<WithBlock<CareSystem>>>> getSystems() {
        return systems;
    }

    public WithOutcome<CheckedConsent> getConsent() {
        return consent;
    }


    public WithOutcome<Boolean> getHasRelationship() {
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
