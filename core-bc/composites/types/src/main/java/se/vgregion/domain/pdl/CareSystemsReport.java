package se.vgregion.domain.pdl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.vgregion.domain.pdl.decorators.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.TreeMap;

public class CareSystemsReport implements Serializable {
    private static final long serialVersionUID = 734432845857726758L;
    private static final Logger LOGGER = LoggerFactory.getLogger(CareSystemsReport.class.getName());

    public final WithOutcome<TreeMap<WithSelection<InformationType>, ArrayList<UserInteractionState<CareSystem>>>> aggregatedSystems;

    public CareSystemsReport(WithAccess<PdlContext> ctx, PdlReport pdlReport) {

        ArrayList<WithInfoType<WithBlock<CareSystem>>> careSystems = (ctx.otherProviders) ?
                pdlReport.systems.value : removeOtherProviders(ctx.value, pdlReport.systems.value) ;

        ArrayList <WithInfoType<WithVisibility<WithBlock<CareSystem>>>> categorizedSystems =
                categorizeSystems(ctx.value, careSystems);

        // Aggregate into a map by information type.
        TreeMap<WithSelection<InformationType>, ArrayList<UserInteractionState<CareSystem>>> aggregatedSystems =
                aggregateByInfotype(categorizedSystems);

        this.aggregatedSystems = pdlReport.systems.mapValue(aggregatedSystems);
    }

    private CareSystemsReport(WithOutcome<TreeMap<WithSelection<InformationType>, ArrayList<UserInteractionState<CareSystem>>>> aggregatedSystems) {
        this.aggregatedSystems = aggregatedSystems;
    }

    public CareSystemsReport selectInfoResource(String id) {
        TreeMap<WithSelection<InformationType>, ArrayList<UserInteractionState<CareSystem>>> newSystems =
                new TreeMap<WithSelection<InformationType>, ArrayList<UserInteractionState<CareSystem>>>();

        for(WithSelection<InformationType> key : aggregatedSystems.value.keySet()) {
            if(key.id.equals(id)) {
                newSystems.put(key.select(), aggregatedSystems.value.get(key));
            } else {
                newSystems.put(key, aggregatedSystems.value.get(key));
            }
        }
        return new CareSystemsReport(aggregatedSystems.mapValue(newSystems));
    }

    public CareSystemsReport toggleInformation(String id) {
        TreeMap<WithSelection<InformationType>, ArrayList<UserInteractionState<CareSystem>>> newSystems =
                new TreeMap<WithSelection<InformationType>, ArrayList<UserInteractionState<CareSystem>>>();

        for(WithSelection<InformationType> key : aggregatedSystems.value.keySet()) {
            ArrayList<UserInteractionState<CareSystem>> sysList =
                    new ArrayList<UserInteractionState<CareSystem>>();

            for(UserInteractionState<CareSystem> uis : aggregatedSystems.value.get(key)) {
                if(uis.id.equals(id)){
                    UserInteractionState<CareSystem> newSelection = (uis.selected) ? uis.deselect() : uis.select();
                    sysList.add(newSelection);
                } else {
                    sysList.add(uis);
                }
            }

            newSystems.put(key, sysList);
        }
        return new CareSystemsReport(aggregatedSystems.mapValue(newSystems));
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
            if (o1 instanceof WithSelection && o2 instanceof WithSelection) {
                WithSelection<InformationType> ws1 = (WithSelection<InformationType>) o1;
                WithSelection<InformationType> ws2 = (WithSelection<InformationType>) o2;
                return ws1.value.compareTo(ws2.value);
            }

            if(o1 instanceof InformationType || o2 instanceof WithSelection) {
                InformationType it1 = (InformationType)o1;
                WithSelection<InformationType>ws2 =  (WithSelection<InformationType>) o2;
                return it1.compareTo(ws2.value);
            }

            throw new ClassCastException();
        }
    };

    private static TreeMap<WithSelection<InformationType>, ArrayList<UserInteractionState<CareSystem>>> aggregateByInfotype(
            ArrayList<WithInfoType<WithVisibility<WithBlock<CareSystem>>>> systemsWithBlocks
    ) {

        TreeMap<WithSelection<InformationType>, ArrayList<UserInteractionState<CareSystem>>> categorizedSystems =
                new TreeMap<WithSelection<InformationType>, ArrayList<UserInteractionState<CareSystem>>>(infoTypeComparator);

        for (WithInfoType<WithVisibility<WithBlock<CareSystem>>> system : systemsWithBlocks) {
            UserInteractionState<CareSystem> sysState = UserInteractionState.flattenAddSelection(system.value);
            WithSelection<InformationType> deselectedInfoType = WithSelection.getDeselected(system.informationType);

            ArrayList<UserInteractionState<CareSystem>> infoSystemList = getOrCreateList(categorizedSystems, deselectedInfoType);

            infoSystemList.add(sysState);
            categorizedSystems.put(deselectedInfoType, infoSystemList);
        }

        return categorizedSystems;
    }

    private static  ArrayList<UserInteractionState<CareSystem>> getOrCreateList(
            TreeMap<WithSelection<InformationType>, ArrayList<UserInteractionState<CareSystem>>> categorizedSystems,
            WithSelection<InformationType> informationType
    ) {
        return (categorizedSystems.containsKey(informationType)) ?
                categorizedSystems.get(informationType) : new ArrayList<UserInteractionState<CareSystem>>();
    }

    private TreeMap<WithSelection<InformationType>, ArrayList<UserInteractionState<CareSystem>>> infoTypeByVisibility(
            TreeMap<
                    WithSelection<InformationType>,
                    ArrayList<UserInteractionState<CareSystem>>> categorizedSystems,
            EnumSet<Visibility> visibility
    ) {
        TreeMap<WithSelection<InformationType>, ArrayList<UserInteractionState<CareSystem>>> filtered =
                new TreeMap<WithSelection<InformationType>, ArrayList<UserInteractionState<CareSystem>>>();

        for(WithSelection<InformationType> sysKey : categorizedSystems.keySet()) {
            ArrayList<UserInteractionState<CareSystem>> list = categorizedSystems.get(sysKey);
            ArrayList<UserInteractionState<CareSystem>> filteredList = new ArrayList<UserInteractionState<CareSystem>>();
            for(UserInteractionState<CareSystem> sys : list) {
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

    public WithOutcome<TreeMap<WithSelection<InformationType>, ArrayList<UserInteractionState<CareSystem>>>> getAggregatedSystems() {
        return aggregatedSystems;
    }

    @Override
    public String toString() {
        return "CareSystemsReport{" +
                "aggregatedSystems=" + aggregatedSystems +
                '}';
    }
}
