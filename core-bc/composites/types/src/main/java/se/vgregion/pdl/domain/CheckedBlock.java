package se.vgregion.pdl.domain;

public class CheckedBlock {
    public enum BlockStatus { OK, BLOCKED }

    public final PatientEngagement engagement;
    public final BlockStatus blocked;

    public CheckedBlock(PatientEngagement engagement, BlockStatus blocked) {
        this.engagement = engagement;
        this.blocked = blocked;
    }
}



