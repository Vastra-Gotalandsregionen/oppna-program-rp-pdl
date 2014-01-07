package se.vgregion.domain.assignment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.vgregion.domain.decorators.WithInfoType;
import se.vgregion.domain.pdl.CareSystem;
import se.vgregion.domain.pdl.InformationType;
import se.vgregion.domain.pdl.Visibility;

import java.io.Serializable;
import java.util.NavigableSet;
import java.util.TreeSet;

public class Assignment implements Serializable, Comparable<Assignment> {
    private static final long serialVersionUID = 6955766363635374811L;
    private static final Logger LOGGER = LoggerFactory.getLogger(Assignment.class.getName());

    public final String assignmentHsaId;
    public final String assignmentDisplayName;
    public final String careProviderHsaId;
    public final String careUnitHsaId;
    public final String careProviderDisplayName;
    public final String careUnitDisplayName;
    private final TreeSet<Access> access;

    public final boolean otherProviders;
    public final boolean otherUnits;


    public Assignment(
            String assignmentHsaId,
            String assignmentDisplayName,
            String careProviderHsaId,
            String careUnitHsaId,
            String careProviderDisplayName,
            String careUnitDisplayName,
            TreeSet<Access> access
            ) {
        this.assignmentHsaId = assignmentHsaId;
        this.assignmentDisplayName = assignmentDisplayName;
        this.careProviderHsaId = careProviderHsaId;
        this.careUnitHsaId = careUnitHsaId;
        this.careProviderDisplayName = careProviderDisplayName;
        this.careUnitDisplayName = careUnitDisplayName;
        this.access = access;

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


    public Visibility visibilityFor(CareSystem system) {
        boolean sameCareProvider = this.careProviderHsaId.equalsIgnoreCase(system.careProviderHsaId);
        boolean sameCareUnit = this.careUnitHsaId.equalsIgnoreCase(system.careUnitHsaId);
        if(!sameCareProvider) {
            return Visibility.OTHER_CARE_PROVIDER;
        } else if(sameCareUnit) {
            return Visibility.SAME_CARE_UNIT;
        } else {
            return Visibility.OTHER_CARE_UNIT;
        }
    }

    public boolean shouldBeIncluded(WithInfoType<CareSystem> careSystem) {
        return (access.size() > 0 ) && checkAllAccess(access.first(), careSystem);
    }

    // Recursively check each access against care system, if one or more access is granted the recursion is stopped
    private boolean checkAllAccess(Access nextAccess, WithInfoType<CareSystem> careSystem) {
        NavigableSet<Access> tail = access.tailSet(nextAccess, false); // Get the rest of the set, excluding the head

        boolean include =
                checkSameCareUnit(careSystem.value) ||
                checkInfoType(nextAccess, careSystem.informationType) &&
                checkScope(nextAccess, careSystem.value);

        // If we found that we should include the system or there are no more accesses left
        boolean includeOrEnd = include || tail.size() == 0;
        return (includeOrEnd) ? include : checkAllAccess(tail.first(), careSystem);
    }

    // If the the information resides in the same care unit it should ALWAYS be displayed
    private boolean checkSameCareUnit(CareSystem careSystem) {
        return Visibility.SAME_CARE_UNIT == visibilityFor(careSystem);

    }

    private boolean checkInfoType(Access access, InformationType informationType) {
        return access.infoType == informationType;
    }

    private boolean checkScope(Access access, CareSystem careSystem) {
        Visibility systemVisibility = visibilityFor(careSystem);

        if(access.hasHsaId) {
            boolean accessCareUnit = access.scope.equalsIgnoreCase(careSystem.careUnitHsaId);
            boolean checkVisibility = Visibility.OTHER_CARE_UNIT == systemVisibility;
            return checkVisibility && accessCareUnit;
        } else if(access.scope.equalsIgnoreCase(AccessScope.VE.toString())) {
            return Visibility.SAME_CARE_UNIT == systemVisibility;
        } else if(access.scope.equalsIgnoreCase((AccessScope.VG.toString()))) {
            return Visibility.OTHER_CARE_UNIT == systemVisibility;
        } else if(access.scope.equalsIgnoreCase((AccessScope.SJF.toString()))) {
            return Visibility.OTHER_CARE_PROVIDER == systemVisibility;
        } else {
            LOGGER.error("Got unknown format for scope " + access.scope + ", skipping access " + access + ".");
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

    @Override
    public int compareTo(Assignment o) {
        return this.assignmentHsaId.compareTo(o.assignmentHsaId) +
                this.assignmentDisplayName.compareTo(o.assignmentDisplayName) +
                this.careProviderHsaId.compareTo(o.careProviderHsaId) +
                this.careUnitHsaId.compareTo(o.careUnitHsaId) +
                this.careProviderDisplayName.compareTo(o.careProviderDisplayName) +
                this.careUnitDisplayName.compareTo(o.careUnitDisplayName);
    }

}
