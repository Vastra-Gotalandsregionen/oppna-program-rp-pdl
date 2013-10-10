package se.vgregion.pdl.domain;

import java.util.List;

public class PdlReport {
    public final boolean hasBlocks;
    public final List<CheckedBlock> blocks;

    public PdlReport(List<CheckedBlock> checkedBlocks) {
        hasBlocks = containsBlocked(checkedBlocks);
        blocks = checkedBlocks;
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
