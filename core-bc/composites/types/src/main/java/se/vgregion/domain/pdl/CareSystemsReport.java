package se.vgregion.domain.pdl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.vgregion.domain.pdl.decorators.*;

import java.io.Serializable;
import java.util.*;

public class CareSystemsReport implements Serializable {
    private static final long serialVersionUID = 734432845857726758L;
    private static final Logger LOGGER = LoggerFactory.getLogger(CareSystemsReport.class.getName());

    public final WithOutcome<TreeMap<WithSelection<InformationType>, ArrayList<WithVisibility<WithBlock<CareSystem>>>>> onlySameCareUnit;
    public final WithOutcome<TreeMap<WithSelection<InformationType>, ArrayList<WithVisibility<WithBlock<CareSystem>>>>> includeOtherCareUnit;
    public final WithOutcome<TreeMap<WithSelection<InformationType>, ArrayList<WithVisibility<WithBlock<CareSystem>>>>> includeOtherCareProvider;

    public CareSystemsReport(WithAccess<PdlContext> ctx, PdlReport pdlReport) {

        ArrayList<WithInfoType<WithBlock<CareSystem>>> careSystems = (ctx.otherProviders) ?
                pdlReport.systems.value : removeOtherProviders(ctx.value, pdlReport.systems.value) ;

        ArrayList <WithInfoType<WithVisibility<WithBlock<CareSystem>>>> categorizedSystems =
                categorizeSystems(ctx.value, careSystems);

        // Aggregate into a map by information type.
        TreeMap<WithSelection<InformationType>, ArrayList<WithVisibility<WithBlock<CareSystem>>>> aggregatedSystems =
                aggregateByInfotype(categorizedSystems);

        // Sets that increasingly contains information types for different categories
        onlySameCareUnit = pdlReport.systems.mapValue(
                infoTypeByVisibility(
                        aggregatedSystems,
                        EnumSet.of(Visibility.SAME_CARE_UNIT)
                )
        );

        includeOtherCareUnit = pdlReport.systems.mapValue(
                infoTypeByVisibility(
                        aggregatedSystems,
                        EnumSet.of(Visibility.SAME_CARE_UNIT, Visibility.OTHER_CARE_UNIT)
                )
        );

        includeOtherCareProvider = pdlReport.systems.mapValue(
                infoTypeByVisibility(
                        aggregatedSystems,
                        EnumSet.allOf(Visibility.class)
                )
        );

        // Re-add fallback information.
        //systems = pdlReport.systems.mapValue(aggregatedSystems);
    }

    private ArrayList<WithInfoType<WithBlock<CareSystem>>> removeOtherProviders(PdlContext ctx, ArrayList<WithInfoType<WithBlock<CareSystem>>> systems) {
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

    private static TreeMap<WithSelection<InformationType>, ArrayList<WithVisibility<WithBlock<CareSystem>>>> aggregateByInfotype(
            ArrayList<WithInfoType<WithVisibility<WithBlock<CareSystem>>>> systemsWithBlocks
    ) {

        TreeMap<WithSelection<InformationType>, ArrayList<WithVisibility<WithBlock<CareSystem>>>> categorizedSystems =
                new TreeMap<WithSelection<InformationType>, ArrayList <WithVisibility<WithBlock<CareSystem>>>>(infoTypeComparator);

        for (WithInfoType<WithVisibility<WithBlock<CareSystem>>> system : systemsWithBlocks) {
            WithSelection<InformationType> deselectedInfoType = WithSelection.getDeselected(system.informationType);
            ArrayList<WithVisibility<WithBlock<CareSystem>>> infoSystemList =
                    getOrCreateList(categorizedSystems, deselectedInfoType);

            infoSystemList.add(system.value);
            categorizedSystems.put(deselectedInfoType, infoSystemList);
        }

        return categorizedSystems;
    }

    private static  ArrayList<WithVisibility<WithBlock<CareSystem>>> getOrCreateList(
            TreeMap<WithSelection<InformationType>, ArrayList<WithVisibility<WithBlock<CareSystem>>>> categorizedSystems,
            WithSelection<InformationType> informationType
    ) {
        return (categorizedSystems.containsKey(informationType)) ?
                categorizedSystems.get(informationType) : new ArrayList<WithVisibility<WithBlock<CareSystem>>>();
    }

    private TreeMap<WithSelection<InformationType>, ArrayList<WithVisibility<WithBlock<CareSystem>>>> infoTypeByVisibility(
            TreeMap<
                    WithSelection<InformationType>,
                    ArrayList<WithVisibility<WithBlock<CareSystem>>>> categorizedSystems,
            EnumSet<Visibility> visibility
    ) {
        TreeMap<WithSelection<InformationType>, ArrayList<WithVisibility<WithBlock<CareSystem>>>> filtered =
                new TreeMap<WithSelection<InformationType>, ArrayList<WithVisibility<WithBlock<CareSystem>>>>();

        for(WithSelection<InformationType> sysKey : categorizedSystems.keySet()) {
            ArrayList<WithVisibility<WithBlock<CareSystem>>> list = categorizedSystems.get(sysKey);
            ArrayList<WithVisibility<WithBlock<CareSystem>>> filteredList = new ArrayList<WithVisibility<WithBlock<CareSystem>>>();
            for(WithVisibility<WithBlock<CareSystem>> sys : list) {
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

    public WithOutcome<TreeMap<WithSelection<InformationType>, ArrayList<WithVisibility<WithBlock<CareSystem>>>>> getOnlySameCareUnit() {
        return onlySameCareUnit;
    }

    public WithOutcome<TreeMap<WithSelection<InformationType>, ArrayList<WithVisibility<WithBlock<CareSystem>>>>> getIncludeOtherCareUnit() {
        return includeOtherCareUnit;
    }

    public WithOutcome<TreeMap<WithSelection<InformationType>, ArrayList<WithVisibility<WithBlock<CareSystem>>>>> getIncludeOtherCareProvider() {
        return includeOtherCareProvider;
    }

    @Override
    public String toString() {
        return "CareSystemsReport{" +
                "onlySameCareUnit=" + onlySameCareUnit +
                ", includeOtherCareUnit=" + includeOtherCareUnit +
                ", includeOtherCareProvider=" + includeOtherCareProvider +
                '}';
    }
}
