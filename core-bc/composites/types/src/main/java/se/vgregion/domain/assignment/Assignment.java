package se.vgregion.domain.assignment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.vgregion.domain.pdl.CareSystem;
import se.vgregion.domain.pdl.Visibility;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

public class Assignment implements Serializable {
    private static final long serialVersionUID = 6955766363635374811L;
    private static final Logger LOGGER = LoggerFactory.getLogger(Assignment.class.getName());

    public final String assignmentHsaId;
    public final String assignmentDisplayName;
    public final String careProviderHsaId;
    public final String careUnitHsaId;
    public final String careProviderDisplayName;
    public final String careUnitDisplayName;
    private final Set<Access> access;

    public final boolean otherProviders;
    public final boolean otherUnits;


    public Assignment(
            String assignmentHsaId,
            String assignmentDisplayName,
            String careProviderHsaId,
            String careUnitHsaId,
            String careProviderDisplayName,
            String careUnitDisplayName,
            Set<Access> access
            ) {
        this.assignmentHsaId = assignmentHsaId;
        this.assignmentDisplayName = assignmentDisplayName;
        this.careProviderHsaId = careProviderHsaId;
        this.careUnitHsaId = careUnitHsaId;
        this.careProviderDisplayName = careProviderDisplayName;
        this.careUnitDisplayName = careUnitDisplayName;
        this.access = Collections.unmodifiableSet(access);

        this.otherProviders = checkOtherProviders();
        this.otherUnits = (!otherProviders) && checkOtherUnits();
    }

    private boolean checkOtherUnits() {
        for(Access a : access) {
            if(a.hasHsaId) {
                return true;
            } else if(a.scope.equalsIgnoreCase((AccessScope.VG.toString()))) {
                return true;
            }
        }
        return false;
    }

    private boolean checkOtherProviders() {
        for(Access a : access) {
            if(a.scope.equalsIgnoreCase(AccessScope.SJF.toString())) {
                return true;
            }
        }
        return false;
    }

    public boolean shouldBeIncluded(CareSystem careSystem) {

        Visibility systemVisibility = careSystem.getVisibilityFor(careProviderHsaId, careUnitHsaId);

        for(Access a : access) {
            if(a.hasHsaId) {
                boolean accessCareUnit = a.scope.equalsIgnoreCase(careSystem.careUnitHsaId);
                boolean checkVisibility = Visibility.OTHER_CARE_UNIT == systemVisibility;
                return checkVisibility && accessCareUnit;
            } else if(a.scope.equalsIgnoreCase(AccessScope.VE.toString())) {
                return Visibility.SAME_CARE_UNIT == systemVisibility;
            } else if(a.scope.equalsIgnoreCase((AccessScope.VG.toString()))) {
                return Visibility.OTHER_CARE_UNIT == systemVisibility;
            } else if(a.scope.equalsIgnoreCase((AccessScope.SJF.toString()))) {
                return Visibility.OTHER_CARE_PROVIDER == systemVisibility;
            } else {
                LOGGER.error("Got unknown format for scope " + a.scope + ", skipping access " + a + ".");
            }
        }

        return false;
    }

    public String getAssignmentHsaId() {
        return assignmentHsaId;
    }

    public String getAssignmentDisplayName() {
        return assignmentDisplayName;
    }

    public String getCareProviderHsaId() {
        return careProviderHsaId;
    }

    public String getCareUnitHsaId() {
        return careUnitHsaId;
    }

    public String getCareProviderDisplayName() {
        return careProviderDisplayName;
    }

    public String getCareUnitDisplayName() {
        return careUnitDisplayName;
    }

    public boolean isOtherProviders() {
        return otherProviders;
    }

    public boolean isOtherUnits() {
        return otherUnits;
    }

    @Override
    public String toString() {
        return "Assignment{" +
                "assignmentHsaId='" + assignmentHsaId + '\'' +
                ", assignmentDisplayName='" + assignmentDisplayName + '\'' +
                ", careProviderHsaId='" + careProviderHsaId + '\'' +
                ", careUnitHsaId='" + careUnitHsaId + '\'' +
                ", careProviderDisplayName='" + careProviderDisplayName + '\'' +
                ", careUnitDisplayName='" + careUnitDisplayName + '\'' +
                ", access=" + access +
                ", otherProviders=" + otherProviders +
                ", otherUnits=" + otherUnits +
                '}';
    }
}
