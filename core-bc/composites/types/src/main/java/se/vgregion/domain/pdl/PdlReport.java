package se.vgregion.domain.pdl;

import java.util.List;

public class PdlReport {

    public enum ConsentType {
        CONSENT, EMERGENCY, FALLBACK
    }

    public final boolean hasBlocks;
    public final List<CheckedBlock> blocks;
    public final boolean hasConsent;
    public final ConsentType consentType;
    public final boolean hasRelationship;

    public PdlReport(List<CheckedBlock> checkedBlocks, CheckedConsent checkedConsent, boolean hasRelationship) {
        this.hasRelationship = hasRelationship;
        hasBlocks = containsBlocked(checkedBlocks);
        blocks = checkedBlocks;
        this.hasConsent = checkedConsent.hasConsent;
        this.consentType = checkedConsent.consentType;
    }

    private boolean containsBlocked(List<CheckedBlock> checkedBlocks) {
        for( CheckedBlock b : checkedBlocks ) {
            if(b.blocked == CheckedBlock.BlockStatus.BLOCKED) {
                return true;
            }
        }
        return false;
    }

    public boolean isHasBlocks() {
        return hasBlocks;
    }

    public List<CheckedBlock> getBlocks() {
        return blocks;
    }

    public boolean isHasConsent() {
        return hasConsent;
    }

    public ConsentType getConsentType() {
        return consentType;
    }

    public boolean isHasRelationship() {
        return hasRelationship;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PdlReport)) return false;

        PdlReport pdlReport = (PdlReport) o;

        if (hasBlocks != pdlReport.hasBlocks) return false;
        if (hasConsent != pdlReport.hasConsent) return false;
        if (hasRelationship != pdlReport.hasRelationship) return false;
        if (!blocks.equals(pdlReport.blocks)) return false;
        if (consentType != pdlReport.consentType) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (hasBlocks ? 1 : 0);
        result = 31 * result + blocks.hashCode();
        result = 31 * result + (hasConsent ? 1 : 0);
        result = 31 * result + consentType.hashCode();
        result = 31 * result + (hasRelationship ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PdlReport{" +
                "hasBlocks=" + hasBlocks +
                ", blocks=" + blocks +
                ", hasConsent=" + hasConsent +
                ", consentType=" + consentType +
                ", hasRelationship=" + hasRelationship +
                '}';
    }
}
