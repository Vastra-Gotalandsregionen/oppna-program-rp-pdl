package se.vgregion.domain.systems;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.vgregion.domain.assignment.Assignment;
import se.vgregion.domain.decorators.*;
import se.vgregion.domain.pdl.InformationType;
import se.vgregion.domain.pdl.PdlReport;

import java.io.Serializable;
import java.util.*;

public class CareSystemsReport implements Serializable {
    private static final long serialVersionUID = 734432845857726758L;
    private static final Logger LOGGER = LoggerFactory.getLogger(CareSystemsReport.class.getName());

    public final WithOutcome<TreeMap<InfoTypeState<InformationType>, ArrayList<SystemState<CareSystem>>>> aggregatedSystems;
    public final boolean containsSameCareUnit;
    public final boolean availablePatientInformation;
    public final boolean selectedInfoResource;

    public CareSystemsReport(Assignment currentAssignment, PdlReport pdlReport) {

        ArrayList<WithInfoType<WithBlock<CareSystem>>> careSystems =
            filterByAssignment(
                    currentAssignment,
                    pdlReport.systems.value
            );

        ArrayList <WithInfoType<WithVisibility<WithBlock<CareSystem>>>> categorizedSystems =
            categorizeSystems(
                currentAssignment,
                careSystems
            );

        // Aggregate into a map by information decorator.
        TreeMap<InformationType, ArrayList<SystemState<CareSystem>>> aggregatedSystems =
                aggregateByInfotype(categorizedSystems);

        // Calculate the InfoTypeState based on entries in list
        TreeMap<InfoTypeState<InformationType>,ArrayList<SystemState<CareSystem>>> infoTypeStateMap =
                calcInfoTypeState(aggregatedSystems);

        containsSameCareUnit = containsSameCareUnit(infoTypeStateMap);

        // Open up those with same care unit, if they exist
        TreeMap<InfoTypeState<InformationType>,ArrayList<SystemState<CareSystem>>> lowestOpenedUp =
                (containsSameCareUnit) ? lowestOpenedUp(infoTypeStateMap) : infoTypeStateMap;

        availablePatientInformation = lowestOpenedUp.size() > 0;

        this.selectedInfoResource = false;
        this.aggregatedSystems = pdlReport.systems.mapValue(lowestOpenedUp);
    }


    private TreeMap<InfoTypeState<InformationType>, ArrayList<SystemState<CareSystem>>> lowestOpenedUp(
            TreeMap<InfoTypeState<InformationType>, ArrayList<SystemState<CareSystem>>> infoTypeStateMap
    ) {
        TreeMap<InfoTypeState<InformationType>, ArrayList<SystemState<CareSystem>>> lowestOpenedUp =
                new TreeMap<InfoTypeState<InformationType>, ArrayList<SystemState<CareSystem>>>(infoTypeComparator);

        for(InfoTypeState<InformationType> key: infoTypeStateMap.keySet()) {
            if(key.lowestVisibility == Visibility.SAME_CARE_UNIT) {
                lowestOpenedUp.put(key.showSameCareUnit(), infoTypeStateMap.get(key));
            } else {
                lowestOpenedUp.put(key, infoTypeStateMap.get(key));
            }
        }

        return lowestOpenedUp;
    }

    private CareSystemsReport(
            WithOutcome <TreeMap<InfoTypeState<InformationType>,
            ArrayList<SystemState<CareSystem>>>> aggregatedSystems,
            boolean selectedInfoResource
    ) {
        this.aggregatedSystems = aggregatedSystems;
        this.containsSameCareUnit = containsSameCareUnit(aggregatedSystems.value);
        this.availablePatientInformation = aggregatedSystems.value.size() > 0;
        this.selectedInfoResource = selectedInfoResource;
    }

    private boolean containsSameCareUnit(TreeMap<InfoTypeState<InformationType>, ArrayList<SystemState<CareSystem>>> systems) {
        for(InfoTypeState<InformationType> key : systems.keySet()) {
            for(SystemState<CareSystem> sys : systems.get(key)) {
                if(sys.getVisibility() ==  Visibility.SAME_CARE_UNIT) {
                    return true;
                }
            }
        }

        return false;
    }

    private static Comparator<SystemState<CareSystem>> systemNameDescComparator = new Comparator<SystemState<CareSystem>>() {
        @Override
        public int compare(SystemState<CareSystem> o1, SystemState<CareSystem> o2) {
            String n1 = o1.value.careProviderDisplayName + " " + o1.value.careUnitDisplayName;
            String n2 = o2.value.careProviderDisplayName + " " + o2.value.careUnitDisplayName;
            return n1.compareToIgnoreCase(n2);
        }
    };

    private TreeMap<InfoTypeState<InformationType>, ArrayList<SystemState<CareSystem>>> calcInfoTypeState(
            TreeMap<InformationType, ArrayList<SystemState<CareSystem>>> aggregatedSystems
    ) {
        TreeMap<InfoTypeState<InformationType>, ArrayList<SystemState<CareSystem>>> infoTypeStateMap =
                new TreeMap<InfoTypeState<InformationType>, ArrayList<SystemState<CareSystem>>>(infoTypeComparator);

        for(Map.Entry<InformationType, ArrayList<SystemState<CareSystem>>> entry : aggregatedSystems.entrySet()) {
            InformationType key = entry.getKey();
            ArrayList<SystemState<CareSystem>> systems = entry.getValue();
            Collections.sort(systems, systemNameDescComparator);

            Visibility lowestVisibility = Visibility.OTHER_CARE_PROVIDER;
            Map<Visibility, Boolean> containsBlocked = new HashMap<Visibility, Boolean>();
            boolean containsOtherUnits = false;
            boolean containsOtherProviders = false;

            // Pre-pouplate Visibility with false values.
            for(Visibility v : Visibility.values()) {
                containsBlocked.put(v, false);
            }

            for(SystemState<CareSystem> v : systems) {

                // Does this info decorator contain blocked information?
                containsBlocked.put(
                        v.visibility,
                        containsBlocked.get(v.visibility) | v.blocked
                );

                if(v.visibility.compareTo(lowestVisibility) < 0) {
                    lowestVisibility = v.visibility;
                }

                containsOtherUnits |= v.visibility == Visibility.OTHER_CARE_UNIT;
                containsOtherProviders |= v.visibility == Visibility.OTHER_CARE_PROVIDER;
            }

            InfoTypeState<InformationType> newKey = InfoTypeState.deselected(
                    lowestVisibility,
                    containsBlocked,
                    key
            );

            InfoTypeState<InformationType> mappedKey = newKey.mapContains(
                    containsOtherUnits,
                    containsOtherProviders
            );

            infoTypeStateMap.put(mappedKey, systems);
        }

        return infoTypeStateMap;
    }

    public CareSystemsReport selectInfoResource(String id) {
        TreeMap<InfoTypeState<InformationType>, ArrayList<SystemState<CareSystem>>> newSystems =
                new TreeMap<InfoTypeState<InformationType>, ArrayList<SystemState<CareSystem>>>(infoTypeComparator);

        for(InfoTypeState<InformationType> key : aggregatedSystems.value.keySet()) {
            if(key.id.equals(id)) {
                newSystems.put(key.select(), aggregatedSystems.value.get(key));
            } else {
                newSystems.put(key, aggregatedSystems.value.get(key));
            }
        }

        return new CareSystemsReport(
            aggregatedSystems.mapValue(newSystems),
            this.selectedInfoResource
        );
    }

    public CareSystemsReport showBlocksForInfoResource(String id) {
        TreeMap<InfoTypeState<InformationType>, ArrayList<SystemState<CareSystem>>> newSystems =
                new TreeMap<InfoTypeState<InformationType>, ArrayList<SystemState<CareSystem>>>(infoTypeComparator);

        for(InfoTypeState<InformationType> key : aggregatedSystems.value.keySet()) {
            if(key.id.equals(id)) {
                newSystems.put(key.viewBlocked(), aggregatedSystems.value.get(key));
            } else {
                newSystems.put(key, aggregatedSystems.value.get(key));
            }
        }

        return new CareSystemsReport(
            aggregatedSystems.mapValue(newSystems),
            this.selectedInfoResource
        );
    }

    public CareSystemsReport toggleInformation(String id, boolean confirmed) {
        TreeMap<InfoTypeState<InformationType>, ArrayList<SystemState<CareSystem>>> newSystems =
                new TreeMap<InfoTypeState<InformationType>, ArrayList<SystemState<CareSystem>>>(infoTypeComparator);

        boolean selected = false;

        for(InfoTypeState<InformationType> key : aggregatedSystems.value.keySet()) {
            ArrayList<SystemState<CareSystem>> sysList =
                    new ArrayList<SystemState<CareSystem>>();


            for(SystemState<CareSystem> uis : aggregatedSystems.value.get(key)) {
                if(uis.id.equals(id)){
                    SystemState<CareSystem> newState = (uis.selected) ? uis.deselect() : uis.select();
                    if(confirmed && uis.needConfirmation && uis.blocked) {
                        newState = newState.unblock();
                    } else if (!confirmed && !uis.needConfirmation && uis.blocked){
                        newState = newState.needConfirmation();
                    } else if (!confirmed && uis.needConfirmation && uis.blocked) {
                        newState = newState.cancelConfirmation();
                    }
                    sysList.add(newState);
                    selected |= newState.selected;
                } else {
                    sysList.add(uis);
                    selected |= uis.selected;
                }
            }

            newSystems.put(key, sysList);
        }

        CareSystemsReport report = new CareSystemsReport(
                aggregatedSystems.mapValue(newSystems),
                selected
        );

        return report;
    }

    private ArrayList<WithInfoType<WithBlock<CareSystem>>> filterByAssignment(
            Assignment assignment,
            ArrayList<WithInfoType<WithBlock<CareSystem>>> systems
    ) {
        ArrayList<WithInfoType<WithBlock<CareSystem>>> filtered = new ArrayList<WithInfoType<WithBlock<CareSystem>>>();
        for( WithInfoType<WithBlock<CareSystem>> system : systems ){

            if(assignment.shouldBeIncluded(system.mapValue(system.value.value))) {
                filtered.add(system);
            }
        }
        return filtered;
    }

    private static Comparator infoTypeComparator = new Comparator() {
        @Override
        public int compare(Object o1, Object o2) {
            if (o1 instanceof InfoTypeState && o2 instanceof InfoTypeState) {

                InfoTypeState<InformationType> ws1 = (InfoTypeState<InformationType>) o1;
                InfoTypeState<InformationType> ws2 = (InfoTypeState<InformationType>) o2;
                return ws1.value.compareTo(ws2.value);
            } else if(o1 instanceof InformationType || o2 instanceof InfoTypeState) {

                InformationType it1 = (InformationType)o1;
                InfoTypeState<InformationType>ws2 =  (InfoTypeState<InformationType>) o2;
                return it1.compareTo(ws2.value);
            } else if(o1 instanceof InfoTypeState || o2 instanceof InformationType) {

                InfoTypeState<InformationType>ws1 =  (InfoTypeState<InformationType>) o1;
                InformationType it2 = (InformationType)o2;
                return ws1.value.compareTo(it2);
            }

            throw new ClassCastException(
                    "One or more incompatible decorator in o1 [" +
                    o1.getClass().getCanonicalName() +
                    "] or o2 [" +
                    o2.getClass().getCanonicalName() +
                    "]. Only types InfoTypeState or InformationType supported."
            );
        }
    };

    private static TreeMap<InformationType, ArrayList<SystemState<CareSystem>>> aggregateByInfotype(
            ArrayList <WithInfoType<WithVisibility<WithBlock<CareSystem>>>> systemsWithBlocks
    ) {

        TreeMap<InformationType, ArrayList<SystemState<CareSystem>>> categorizedSystems =
                new TreeMap<InformationType, ArrayList <SystemState<CareSystem>>>();

        for (WithInfoType<WithVisibility<WithBlock<CareSystem>>> system : systemsWithBlocks) {
            SystemState<CareSystem> sysState = SystemState.flattenAddSelection(system.value);

            ArrayList<SystemState<CareSystem>> infoSystemList = getOrCreateList(
                categorizedSystems,
                system.informationType
            );

            infoSystemList.add(sysState);

            categorizedSystems.put(system.informationType, infoSystemList);
        }

        return categorizedSystems;
    }

    private static  ArrayList<SystemState<CareSystem>> getOrCreateList(
        TreeMap<InformationType, ArrayList<SystemState<CareSystem>>> categorizedSystems,
        InformationType infoTypeState
    ) {
        return (categorizedSystems.containsKey(infoTypeState)) ?
            categorizedSystems.get(infoTypeState) : new ArrayList<SystemState<CareSystem>>();
    }

    private ArrayList<WithInfoType<WithVisibility<WithBlock<CareSystem>>>> categorizeSystems(
        Assignment currentAssignment,
        ArrayList<WithInfoType<WithBlock<CareSystem>>> systems
    ) {

        ArrayList<WithInfoType<WithVisibility<WithBlock<CareSystem>>>> categorizedSystems =
                new ArrayList<WithInfoType<WithVisibility<WithBlock<CareSystem>>>>();

        for (WithInfoType<WithBlock<CareSystem>> sys : systems) {
            withVisiblitiy(categorizedSystems, sys, currentAssignment.visibilityFor(sys.value.value));
        }

        return categorizedSystems;
    }

    private void withVisiblitiy(
        ArrayList<WithInfoType<WithVisibility<WithBlock<CareSystem>>>> categorizedSystems,
        WithInfoType<WithBlock<CareSystem>> sys,
        Visibility visibility
    ) {
        categorizedSystems.add(
            sys.mapValue(
                new WithVisibility<WithBlock<CareSystem>>(
                    visibility,
                    sys.value
                )
            )
        );
    }

    public WithOutcome<TreeMap<InfoTypeState<InformationType>, ArrayList<SystemState<CareSystem>>>> getAggregatedSystems() {
        return aggregatedSystems;
    }

    public boolean isContainsSameCareUnit() {
        return containsSameCareUnit;
    }

    public boolean isAvailablePatientInformation() {
        return availablePatientInformation;
    }

    public boolean isSelectedInfoResource() {
        return selectedInfoResource;
    }

    @Override
    public String toString() {
        return "CareSystemsReport{" +
                "aggregatedSystems=" + aggregatedSystems +
                ", containsSameCareUnit=" + containsSameCareUnit +
                ", availablePatientInformation=" + availablePatientInformation +
                ", selectedInfoResource=" + selectedInfoResource +
                '}';
    }
}
