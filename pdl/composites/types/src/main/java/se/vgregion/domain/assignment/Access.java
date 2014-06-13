package se.vgregion.domain.assignment;

import se.vgregion.domain.pdl.InformationType;

import java.io.Serializable;

public class Access implements Serializable, Comparable<Access> {
    private static final long serialVersionUID = -1535296580089278612L;
    public final boolean hasHsaId;
    public final AccessActivity accessActivity;
    public final InformationType infoType;
    public final String scope; // VE, VG, SJF or an HSA-ID

    private Access(boolean hasHsaId, AccessActivity accessActivity, InformationType infoType, String scope) {
        this.hasHsaId = hasHsaId;
        this.accessActivity = accessActivity;
        this.infoType = infoType;
        this.scope = scope;
    }

    public static Access fromMiuRights(String miuRight) {
        String[] rights = miuRight.split(";");
        if(rights.length != 3) throw new IllegalArgumentException("Miu rights for assignment must be a three values separated by a semicolon.");

        AccessActivity activity = AccessActivity.getByValue(rights[0]);
        InformationType informationType = InformationType.valueOfWithFallback(rights[1].toUpperCase());
        String scope = rights[2];
        boolean isHsaId = scope.length() > 3; // Assumption: Scope is a HSA-ID if longer than three characters (Other being VE, VG, SJF)

        return new Access(isHsaId, activity, informationType, scope);
    }

    @Override
    public int compareTo(Access o) {
        return this.accessActivity.compareTo(o.accessActivity) +
               this.infoType.compareTo(o.infoType) +
               this.scope.compareTo(o.scope);
    }

    @Override
    public String toString() {
        return "Access{" +
                "hasHsaId=" + hasHsaId +
                ", accessActivity=" + accessActivity +
                ", infoType=" + infoType +
                ", scope='" + scope + '\'' +
                '}';
    }
}
