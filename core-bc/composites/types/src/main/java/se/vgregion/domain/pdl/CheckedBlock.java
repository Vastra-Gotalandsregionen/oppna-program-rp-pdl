package se.vgregion.domain.pdl;

public class CheckedBlock {
    public enum BlockStatus { OK, BLOCKED }

    public final Engagement engagement;
    public final BlockStatus blocked;

    public CheckedBlock(Engagement engagement, BlockStatus blocked) {
        this.engagement = engagement;
        this.blocked = blocked;
    }

    public Engagement getEngagement() {
        return engagement;
    }

    public BlockStatus getBlocked() {
        return blocked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CheckedBlock)) return false;

        CheckedBlock that = (CheckedBlock) o;

        if (blocked != that.blocked) return false;
        if (!engagement.equals(that.engagement)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = engagement.hashCode();
        result = 31 * result + blocked.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "CheckedBlock{" +
                "engagement=" + engagement +
                ", blocked=" + blocked +
                '}';
    }
}



