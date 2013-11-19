package se.vgregion.domain.pdl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.TreeMap;

public class CareSystemsReport implements Serializable {
    private static final long serialVersionUID = 734432845857726758L;
    private static final Logger LOGGER = LoggerFactory.getLogger(CareSystemsReport.class.getName());

    public final WithFallback<TreeMap<InformationType, ArrayList<WithVisibility<WithBlock<CareSystem>>>>> systems;
    public final EnumSet<InformationType> onlySameCareUnit;
    public final EnumSet<InformationType> includeOtherCareUnit;
    public final EnumSet<InformationType> includeOtherCareProvider;

    public CareSystemsReport(PdlContext ctx, PdlReport pdlReport) {

        ArrayList<WithInfoType<WithVisibility<WithBlock<CareSystem>>>> categorizedSystems =
                categorizeSystems(ctx, pdlReport.systems.value);

        // Sets that increasingly contains information types for different categories
        onlySameCareUnit = infoTypeByCategory(categorizedSystems, EnumSet.of(Visibility.SAME_CARE_UNIT));
        includeOtherCareUnit = infoTypeByCategory(categorizedSystems, EnumSet.of(Visibility.SAME_CARE_UNIT, Visibility.OTHER_CARE_UNIT));
        includeOtherCareProvider = infoTypeByCategory(categorizedSystems, EnumSet.allOf(Visibility.class));

        // Aggregate into a map by information type.
        TreeMap<InformationType, ArrayList<WithVisibility<WithBlock<CareSystem>>>> aggregatedSystems =
                aggregateByInfotype(categorizedSystems);

        // Re-add fallback information.
        systems = pdlReport.systems.mapValue(aggregatedSystems);
    }

    private static TreeMap<InformationType, ArrayList<WithVisibility<WithBlock<CareSystem>>>> aggregateByInfotype(
            ArrayList<WithInfoType<WithVisibility<WithBlock<CareSystem>>>> systemsWithBlocks
    ) {

        TreeMap<InformationType, ArrayList<WithVisibility<WithBlock<CareSystem>>>> categorizedSystems =
                new TreeMap<InformationType, ArrayList<WithVisibility<WithBlock<CareSystem>>>>();

        for (WithInfoType<WithVisibility<WithBlock<CareSystem>>> system : systemsWithBlocks) {
            ArrayList<WithVisibility<WithBlock<CareSystem>>> infoSystemList =
                    getOrCreateList(categorizedSystems, system.informationType);

            infoSystemList.add(system.value);
            categorizedSystems.put(system.informationType, infoSystemList);
        }

        return categorizedSystems;
    }

    private static  ArrayList<WithVisibility<WithBlock<CareSystem>>> getOrCreateList(
            TreeMap<InformationType, ArrayList<WithVisibility<WithBlock<CareSystem>>>> categorizedSystems,
            InformationType informationType
    ) {
        return (categorizedSystems.containsKey(informationType)) ?
                categorizedSystems.get(informationType) : new ArrayList<WithVisibility<WithBlock<CareSystem>>>();
    }

    private EnumSet<InformationType> infoTypeByCategory(
            ArrayList<WithInfoType<WithVisibility<WithBlock<CareSystem>>>> categorizedSystems,
            EnumSet<Visibility> categories
    ) {
        List<InformationType> infoTypes = new ArrayList<InformationType>();

        for (WithInfoType<WithVisibility<WithBlock<CareSystem>>> sys : categorizedSystems) {
            if (categories.contains(sys.value.visibility)) {
                infoTypes.add(sys.informationType);
            }
        }

        return EnumSet.copyOf(infoTypes);
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

    public WithFallback<TreeMap<InformationType, ArrayList<WithVisibility<WithBlock<CareSystem>>>>> getSystems() {
        return systems;
    }

    public EnumSet<InformationType> getOnlySameCareUnit() {
        return onlySameCareUnit;
    }

    public EnumSet<InformationType> getIncludeOtherCareUnit() {
        return includeOtherCareUnit;
    }

    public EnumSet<InformationType> getIncludeOtherCareProvider() {
        return includeOtherCareProvider;
    }

    @Override
    public String toString() {
        return "CareSystemsReport{" +
                "systems=" + systems +
                ", onlySameCareUnit=" + onlySameCareUnit +
                ", includeOtherCareUnit=" + includeOtherCareUnit +
                ", includeOtherCareProvider=" + includeOtherCareProvider +
                '}';
    }
}
