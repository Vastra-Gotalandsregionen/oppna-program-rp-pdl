package se.vgregion.domain.pdl;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class AssignmentAccess implements Serializable {
    private static final long serialVersionUID = 6955766363635374811L;

    public final String assignmentDisplayName;
    public final boolean otherProviders;
    public final List<Access> access;

    public AssignmentAccess(String assignmentDisplayName, List<Access> access) {
        this.assignmentDisplayName = assignmentDisplayName;
        this.otherProviders = hasOtherProviders(access);
        this.access = Collections.unmodifiableList(access);
    }

    private boolean hasOtherProviders(List<Access> access) {
        for(Access a : access) {
            if( !a.sameProvider) {
                return true;
            }
        }
        return false;
    }

    public String getAssignmentDisplayName() {
        return assignmentDisplayName;
    }

    public boolean isOtherProviders() {
        return otherProviders;
    }

    public List<Access> getAccess() {
        return access;
    }

    @Override
    public String toString() {
        return "AssignmentAccess{" +
                "assignmentDisplayName='" + assignmentDisplayName + '\'' +
                ", otherProviders=" + otherProviders +
                ", access=" + access +
                '}';
    }
}
