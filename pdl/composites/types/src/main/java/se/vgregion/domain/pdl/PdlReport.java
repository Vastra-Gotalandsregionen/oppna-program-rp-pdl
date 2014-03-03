package se.vgregion.domain.pdl;

import se.vgregion.domain.decorators.Outcome;
import se.vgregion.domain.decorators.WithOutcome;
import se.vgregion.domain.decorators.WithBlock;
import se.vgregion.domain.decorators.WithInfoType;
import se.vgregion.domain.systems.CareSystem;

import java.io.Serializable;
import java.util.ArrayList;

public class PdlReport implements Serializable {
    private static final long serialVersionUID = -597284170511725549L;
    public final WithOutcome<CheckedConsent> consent;
    public final WithOutcome<Boolean> hasRelationship;
    public final WithOutcome<ArrayList<WithInfoType<WithBlock<CareSystem>>>> systems;
    public final boolean hasNonSuccessOutcome;
    public final boolean hasPatientInformation;

    public PdlReport(
            WithOutcome<ArrayList<WithInfoType<WithBlock<CareSystem>>>> checkedSystems,
            WithOutcome<CheckedConsent> checkedConsent,
            WithOutcome<Boolean> hasRelationship
    ) {
        this.hasRelationship = hasRelationship;
        this.systems = checkedSystems;
        this.consent = checkedConsent;

        this.hasPatientInformation = systems.value.size() > 0;

        this.hasNonSuccessOutcome =
                this.hasRelationship.outcome != Outcome.SUCCESS ||
                this.systems.outcome != Outcome.SUCCESS ||
                this.consent.outcome != Outcome.SUCCESS;
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

    public boolean isHasNonSuccessOutcome() {
        return hasNonSuccessOutcome;
    }

    public boolean isHasPatientInformation() {
        return hasPatientInformation;
    }

    public static PdlReport defaultReport(WithOutcome<ArrayList<WithInfoType<CareSystem>>> careSystems) {
        ArrayList<WithInfoType<WithBlock<CareSystem>>> checkedSystems =
                new ArrayList<WithInfoType<WithBlock<CareSystem>>>();

        for(WithInfoType<CareSystem> system: careSystems.value) {
            WithBlock<CareSystem> withBlock = WithBlock.unblocked(system.value);
            WithInfoType<WithBlock<CareSystem>> withInfoType = system.mapValue(withBlock);
            checkedSystems.add(withInfoType);
        }
        WithOutcome<ArrayList<WithInfoType<WithBlock<CareSystem>>>> checkedSystemsOutcome =
                careSystems.mapValue(checkedSystems);


        WithOutcome<CheckedConsent> checkedConsent = WithOutcome.success(new CheckedConsent(ConsentType.None,true));

        WithOutcome<Boolean> hasRelationship = WithOutcome.success(true);

        return new PdlReport(
                checkedSystemsOutcome,
                checkedConsent,
                hasRelationship
        );
    }


    @Override
    public String toString() {
        return "PdlReport{" +
                "consent=" + consent +
                ", hasRelationship=" + hasRelationship +
                ", systems=" + systems +
                ", hasNonSuccessOutcome=" + hasNonSuccessOutcome +
                ", hasPatientInformation=" + hasPatientInformation +
                '}';
    }

    public enum ConsentType {
        Consent, Emergency, None
    }
}
