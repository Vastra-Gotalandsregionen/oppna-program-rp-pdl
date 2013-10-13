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
}
