package se.vgregion.domain.pdl;

import se.vgregion.domain.decorators.InfoTypeState;
import se.vgregion.domain.decorators.SystemState;
import se.vgregion.domain.decorators.WithInfoType;

import java.io.Serializable;
import java.util.*;

public class SummaryReport implements Serializable {

    private static final long serialVersionUID = 1386731884321064408L;

    @Override
    public String toString() {
        return "SummaryReport{" +
                "careSystems=" + careSystems +
                '}';
    }

    public Map<CareSystemSource, ArrayList<WithInfoType<ArrayList<CareSystem>>>> getCareSystems() {
        return careSystems;
    }

    public final Map<CareSystemSource, ArrayList<WithInfoType<ArrayList<CareSystem>>>> careSystems =
            new TreeMap<CareSystemSource, ArrayList<WithInfoType<ArrayList<CareSystem>>>>();

    public SummaryReport(TreeMap<InfoTypeState<InformationType>, ArrayList<SystemState<CareSystem>>> aggregatedSystems) {

        TreeSet<WithInfoType<ArrayList<CareSystem>>> infoCareUnits =
                new TreeSet<WithInfoType<ArrayList<CareSystem>>>();


        for(InfoTypeState<InformationType> key : aggregatedSystems.keySet()) {
            for(SystemState<CareSystem> system : aggregatedSystems.get(key)) {
                if(system.selected) {
                    CareSystemSource source = system.value.source;
                    WithInfoType<ArrayList<CareSystem>> systems =
                            getOrCreateEntry(source, key.value);

                    systems.value.add(system.value);
                }
            }
        }

    }

    private WithInfoType<ArrayList<CareSystem>> getOrCreateEntry(CareSystemSource source, InformationType informationType) {
        if(!careSystems.containsKey(source)) {
            careSystems.put(source, new ArrayList<WithInfoType<ArrayList<CareSystem>>>());
        }

        ArrayList<WithInfoType<ArrayList<CareSystem>>> systems = careSystems.get(source);

        WithInfoType<ArrayList<CareSystem>> lookup =
                new WithInfoType<ArrayList<CareSystem>>(informationType, new ArrayList<CareSystem>());

        if (systems.contains(lookup)) {
            return systems.get(systems.indexOf(lookup));
        } else {
            systems.add(lookup);
            return lookup;
        }

    }
}
