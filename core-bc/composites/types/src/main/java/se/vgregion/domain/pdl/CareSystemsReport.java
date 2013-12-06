package se.vgregion.domain.pdl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.vgregion.domain.pdl.decorators.*;

import java.io.Serializable;
import java.util.*;

public class CareSystemsReport implements Serializable {
    private static final long serialVersionUID = 734432845857726758L;
    private static final Logger LOGGER = LoggerFactory.getLogger(CareSystemsReport.class.getName());

    public final WithOutcome<TreeMap<InfoTypeState<InformationType>, ArrayList<SystemState<CareSystem>>>> aggregatedSystems;
    public final Map<Visibility, Boolean> containsBlockedInfoTypes;

    public CareSystemsReport(WithAccess<PdlContext> ctx, PdlReport pdlReport) {

        ArrayList<WithInfoType<WithBlock<CareSystem>>> careSystems = (ctx.otherProviders) ?
                pdlReport.systems.value : removeOtherProviders(ctx.value, pdlReport.systems.value) ;

        ArrayList <WithInfoType<WithVisibility<WithBlock<CareSystem>>>> categorizedSystems =
                categorizeSystems(ctx.value, careSystems);

        // Aggregate into a map by information type.
        TreeMap<InformationType, ArrayList<SystemState<CareSystem>>> aggregatedSystems =
                aggregateByInfotype(categorizedSystems);

        // Calculate the InfoTypeState based on entries in list
        TreeMap<InfoTypeState<InformationType>,ArrayList<SystemState<CareSystem>>> infoTypeStateMap =
                calcInfoTypeState(aggregatedSystems);

        containsBlockedInfoTypes = containsOnlyBlockedInfoTypes(infoTypeStateMap);

        this.aggregatedSystems = pdlReport.systems.mapValue(infoTypeStateMap);
    }

    private CareSystemsReport(
            WithOutcome <TreeMap<InfoTypeState<InformationType>, ArrayList<SystemState<CareSystem>>>> aggregatedSystems,
            Map<Visibility, Boolean> containsBlockedInfoTypes
    ) {
        this.aggregatedSystems = aggregatedSystems;
        this.containsBlockedInfoTypes = containsBlockedInfoTypes;
    }

    /**
     * Find out if there is one info type that contain only blocked information.
     * @param systems
     * @return
     */
    private Map<Visibility, Boolean> containsOnlyBlockedInfoTypes(
            TreeMap<InfoTypeState<InformationType>, ArrayList<SystemState<CareSystem>>> systems
    ) {
        Map<Visibility, Boolean> result = new HashMap<Visibility, Boolean>();

        for(Visibility v : EnumSet.allOf(Visibility.class)) {
            result.put(v, false); // make sure we have a value.
            for(InfoTypeState<InformationType> key : systems.keySet()) {
                if(
                    v.compareTo(key.lowestVisibility) >= 0 &&
                    key.containsOnlyBlocked.containsKey(v) &&
                    key.containsOnlyBlocked.get(v)
                ) {
                    result.put(v, true);
                }
            }
        }

        return result;
    }

    private TreeMap<InfoTypeState<InformationType>, ArrayList<SystemState<CareSystem>>> calcInfoTypeState(
            TreeMap<InformationType, ArrayList<SystemState<CareSystem>>> aggregatedSystems
    ) {
        TreeMap<InfoTypeState<InformationType>, ArrayList<SystemState<CareSystem>>> infoTypeStateMap =
                new TreeMap<InfoTypeState<InformationType>, ArrayList<SystemState<CareSystem>>>(infoTypeComparator);

        for(Map.Entry<InformationType, ArrayList<SystemState<CareSystem>>> entry : aggregatedSystems.entrySet()) {
            InformationType key = entry.getKey();
            ArrayList <SystemState<CareSystem>> value = entry.getValue();
            Visibility lowestVisibility = Visibility.OTHER_CARE_PROVIDER;
            boolean containsBlocked = false;
            Map<Visibility, Boolean> containsOnlyBlocked = new HashMap<Visibility, Boolean>();
            for(SystemState<CareSystem> v : value) {
                if(v.blocked) {
                    containsBlocked = true;
                }

                if(!containsOnlyBlocked.containsKey(v.visibility)) {
                    containsOnlyBlocked.put(v.visibility, true);
                }

                // Does this info type contain only blocked information?
                containsOnlyBlocked.put(
                    v.visibility,
                    containsOnlyBlocked.get(v.visibility) & v.blocked
                );

                if(v.visibility.compareTo(lowestVisibility) < 0) {
                    lowestVisibility = v.visibility;
                }
            }

            InfoTypeState<InformationType> newKey = InfoTypeState.init(
                lowestVisibility,
                containsBlocked,
                containsOnlyBlocked,
                key
            );

            infoTypeStateMap.put(newKey, value);
        }

        return infoTypeStateMap;
    }

    public CareSystemsReport selectInfoResource(String id) {
        TreeMap<InfoTypeState<InformationType>, ArrayList<SystemState<CareSystem>>> newSystems =
                new TreeMap<InfoTypeState<InformationType>, ArrayList<SystemState<CareSystem>>>();

        for(InfoTypeState<InformationType> key : aggregatedSystems.value.keySet()) {
            if(key.id.equals(id)) {
                newSystems.put(key.select(), aggregatedSystems.value.get(key));
            } else {
                newSystems.put(key, aggregatedSystems.value.get(key));
            }
        }
        return new CareSystemsReport(aggregatedSystems.mapValue(newSystems), containsBlockedInfoTypes);
    }

    public CareSystemsReport showBlocksForInfoResource(String id) {
        TreeMap<InfoTypeState<InformationType>, ArrayList<SystemState<CareSystem>>> newSystems =
                new TreeMap<InfoTypeState<InformationType>, ArrayList<SystemState<CareSystem>>>();

        for(InfoTypeState<InformationType> key : aggregatedSystems.value.keySet()) {
            if(key.id.equals(id)) {
                newSystems.put(key.viewBlocked(), aggregatedSystems.value.get(key));
            } else {
                newSystems.put(key, aggregatedSystems.value.get(key));
            }
        }
        return new CareSystemsReport(aggregatedSystems.mapValue(newSystems), containsBlockedInfoTypes);
    }

    public CareSystemsReport toggleInformation(String id, boolean blocked) {
        TreeMap<InfoTypeState<InformationType>, ArrayList<SystemState<CareSystem>>> newSystems =
                new TreeMap<InfoTypeState<InformationType>, ArrayList<SystemState<CareSystem>>>();

        for(InfoTypeState<InformationType> key : aggregatedSystems.value.keySet()) {
            ArrayList<SystemState<CareSystem>> sysList =
                    new ArrayList<SystemState<CareSystem>>();

            for(SystemState<CareSystem> uis : aggregatedSystems.value.get(key)) {
                if(uis.id.equals(id)){
                    SystemState<CareSystem> newState = (uis.selected) ? uis.deselect() : uis.select();
                    if(blocked) {
                        newState = newState.unblock();
                    }
                    sysList.add(newState);
                } else {
                    sysList.add(uis);
                }
            }

            newSystems.put(key, sysList);
        }
        return new CareSystemsReport(aggregatedSystems.mapValue(newSystems), containsBlockedInfoTypes);
    }

    public CareSystemsReport showBlocksForInfoType(Visibility visibility) {
        HashMap<Visibility, Boolean> newContainsBlockedInfoTypes = new HashMap<Visibility, Boolean>();
        for(Visibility v : EnumSet.allOf(Visibility.class)) {
            if(v == visibility) {
                newContainsBlockedInfoTypes.put(v, false);
            } else {
                newContainsBlockedInfoTypes.put(v, containsBlockedInfoTypes.get(v));
            }
        }

        TreeMap<InfoTypeState<InformationType>, ArrayList<SystemState<CareSystem>>> newAggregatedSystems =
                new TreeMap<InfoTypeState<InformationType>, ArrayList<SystemState<CareSystem>>>();

        for(InfoTypeState<InformationType> k : aggregatedSystems.value.keySet()) {
            newAggregatedSystems.put(k.showBlockedInfoType(visibility), aggregatedSystems.value.get(k));
        }

        WithOutcome<TreeMap<InfoTypeState<InformationType>, ArrayList<SystemState<CareSystem>>>> result =
                aggregatedSystems.mapValue(newAggregatedSystems);

        return new CareSystemsReport(result, newContainsBlockedInfoTypes);
    }

    private ArrayList<WithInfoType<WithBlock<CareSystem>>> removeOtherProviders(PdlContext ctx, ArrayList <WithInfoType<WithBlock<CareSystem>>> systems) {
        ArrayList<WithInfoType<WithBlock<CareSystem>>> filtered = new ArrayList<WithInfoType<WithBlock<CareSystem>>>();
        for( WithInfoType<WithBlock<CareSystem>> system : systems ){
            if(ctx.careProviderHsaId.equals(system.value.value.careProviderHsaId)) {
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
                    "One or more incompatible type in o1 [" +
                    o1.getClass().getCanonicalName() +
                    "] or o2 [" +
                    o2.getClass().getCanonicalName() +
                    "]. Only types InfoTypeState or InformationType supported."
            );
        }
    };

    private static TreeMap<InformationType, ArrayList<SystemState<CareSystem>>> aggregateByInfotype(
            ArrayList<WithInfoType<WithVisibility<WithBlock<CareSystem>>>> systemsWithBlocks
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

    private TreeMap<WithSelection<InformationType>, ArrayList<SystemState<CareSystem>>> infoTypeByVisibility(
            TreeMap<
                    WithSelection<InformationType>,
                    ArrayList<SystemState<CareSystem>>> categorizedSystems,
            EnumSet<Visibility> visibility
    ) {
        TreeMap<WithSelection<InformationType>, ArrayList<SystemState<CareSystem>>> filtered =
                new TreeMap<WithSelection<InformationType>, ArrayList<SystemState<CareSystem>>>();

        for(WithSelection<InformationType> sysKey : categorizedSystems.keySet()) {
            ArrayList<SystemState<CareSystem>> list = categorizedSystems.get(sysKey);
            ArrayList<SystemState<CareSystem>> filteredList = new ArrayList<SystemState<CareSystem>>();
            for(SystemState<CareSystem> sys : list) {
                if(visibility.contains(sys.visibility)) {
                    filteredList.add(sys);
                }
            }
            if(filteredList.size() > 0) {
                filtered.put(sysKey, filteredList);
            }
        }

        return filtered;
    }

    private ArrayList<WithInfoType<WithVisibility<WithBlock<CareSystem>>>> categorizeSystems(
            PdlContext ctx,
            ArrayList<WithInfoType<WithBlock<CareSystem>>> systems
    ) {

        ArrayList<WithInfoType<WithVisibility<WithBlock<CareSystem>>>> categorizedSystems =
                new ArrayList<WithInfoType<WithVisibility<WithBlock<CareSystem>>>>();

        for (WithInfoType<WithBlock<CareSystem>> sys : systems) {
            boolean isSameCareProvider = ctx.careProviderHsaId.equals(sys.value.value.careProviderHsaId);
            boolean isSameCareUnit = ctx.careUnitHsaId.equals(sys.value.value.careUnitHsaId);

            if (isSameCareProvider && isSameCareUnit) {
                withVisiblitiy(categorizedSystems, sys, Visibility.SAME_CARE_UNIT);
            } else if (isSameCareProvider) {
                withVisiblitiy(categorizedSystems, sys, Visibility.OTHER_CARE_UNIT);
            } else {
                withVisiblitiy(categorizedSystems, sys, Visibility.OTHER_CARE_PROVIDER);
            }
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

    public Map<Visibility, Boolean> getContainsBlockedInfoTypes() {
        return containsBlockedInfoTypes;
    }

    @Override
    public String toString() {
        return "CareSystemsReport{" +
                "aggregatedSystems=" + aggregatedSystems +
                ", containsBlockedInfoTypes=" + containsBlockedInfoTypes +
                '}';
    }
}
