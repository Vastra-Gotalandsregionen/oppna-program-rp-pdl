package se.vgregion.portal.controller.pdl;

import se.vgregion.domain.decorators.InfoTypeState;
import se.vgregion.domain.decorators.SystemState;
import se.vgregion.domain.systems.CareSystem;
import se.vgregion.domain.pdl.InformationType;
import se.vgregion.domain.systems.Visibility;
import se.vgregion.portal.controller.pdl.common.PdlUserState;

public class DisplayInformation {
    private DisplayInformation() {
        // Util class, private constructor
    }

    public static boolean expandInfoType(InfoTypeState<InformationType> infoType, PdlUserState state) {
        boolean lowestVisibility = state.getCurrentVisibility().compareTo(infoType.lowestVisibility) >= 0;
        boolean hasRelationship = state.getPdlReport().hasRelationship.value;

        boolean visible = state.isPatientInformationExist() && lowestVisibility && hasRelationship;

        switch(state.getCurrentVisibility()) {
            case SAME_CARE_UNIT:
                boolean selectedOrCareUnit = (infoType.selected || infoType.showSameCareUnit);
                return visible && selectedOrCareUnit;
            case OTHER_CARE_UNIT:
                return visible && state.getCtx().value.currentAssignment.isOtherUnits();
            case OTHER_CARE_PROVIDER:
                return visible && state.getCtx().value.currentAssignment.isOtherProviders();
            case NOT_VISIBLE:
                return false;
        }

        throw new IllegalArgumentException("Unable to gather enough information to calculate visibility.");
    }

    public static boolean displayShowMore(
            InfoTypeState<InformationType> infoType,
            PdlUserState state
    ) {
        return displayOtherUnits(infoType, state) || displayOtherProviders(infoType, state) ;
    }

    public static boolean displayOtherUnits(
            InfoTypeState<InformationType> infoType,
            PdlUserState state
    ) {
        return state.getCtx().value.currentAssignment.otherUnits && infoType.containsOtherUnits;
    }

    public static boolean displayOtherProviders(InfoTypeState<InformationType> infoType, PdlUserState state) {
        return state.getCtx().value.currentAssignment.otherProviders && infoType.containsOtherProviders;
    }

    public static boolean displayCareUnit(
            InfoTypeState<InformationType> infoType,
            SystemState<CareSystem> system,
            PdlUserState state
    ) {
        boolean isSelectedNotSameUnit = infoType.selected || system.visibility == Visibility.SAME_CARE_UNIT;
        boolean infotypeExpanded = expandInfoType(infoType, state);
        boolean lowestVisibility = state.getCurrentVisibility().compareTo(system.visibility) >= 0;
        boolean isUnblocked = infoType.viewBlocked || !(infoType.containsBlocked.get(system.visibility) && system.initiallyBlocked && system.blocked);

        return state.isPatientInformationExist() && infotypeExpanded && isSelectedNotSameUnit && lowestVisibility && isUnblocked;
    }

    public static boolean displayUnblockConfirmation(
        SystemState<CareSystem> system,
        PdlUserState state
    ) {
        return system.blocked &&
               system.needConfirmation &&
               !state.getCtx().value.currentAssignment.isOtherProviders();
    }

    public static boolean displayUnblockInformation(
            SystemState<CareSystem> system,
            PdlUserState state
    ) {
        return  system.blocked &&
                system.needConfirmation &&
                state.getCtx().value.currentAssignment.isOtherProviders();
    }

    public static boolean displayBlockedAction(
            InfoTypeState<InformationType> infoType,
            PdlUserState state
    ) {
        return  infoType.containsBlocked.get(state.getCurrentVisibility()) &&
                !infoType.viewBlocked &&
                infoType.selected;
    }
}
