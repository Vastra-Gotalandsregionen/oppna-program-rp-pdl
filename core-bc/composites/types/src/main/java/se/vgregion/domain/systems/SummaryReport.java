package se.vgregion.domain.systems;

import se.vgregion.domain.decorators.InfoTypeState;
import se.vgregion.domain.decorators.SystemState;
import se.vgregion.domain.decorators.WithInfoType;
import se.vgregion.domain.pdl.InformationType;
import se.vgregion.events.context.SourceReferences;

import java.io.Serializable;
import java.util.*;

public class SummaryReport implements Serializable {

    private static final long serialVersionUID = 1386731884321064408L;


    public Map<CareSystemViewer, ArrayList<WithInfoType<ArrayList<CareSystem>>>> getCareSystems() {
        return careSystems;
    }

    public final Map<CareSystemViewer, ArrayList<WithInfoType<ArrayList<CareSystem>>>> careSystems =
            new TreeMap<CareSystemViewer, ArrayList<WithInfoType<ArrayList<CareSystem>>>>();

    public final List<SourceReferences> referencesList;

    public SummaryReport(TreeMap<InfoTypeState<InformationType>, ArrayList<SystemState<CareSystem>>> aggregatedSystems) {

        ArrayList<SourceReferences> refList = new ArrayList<SourceReferences>();

        for(InfoTypeState<InformationType> key : aggregatedSystems.keySet()) {
            for(SystemState<CareSystem> system : aggregatedSystems.get(key)) {
                if(system.selected) {
                    CareSystemViewer source = system.value.source;

                    WithInfoType<ArrayList<CareSystem>> systems =
                            getOrCreateEntry(source, key.value);

                    systems.value.add(system.value);
                }
            }
        }

        List<SourceReferences> refs = new ArrayList<SourceReferences>();

        for(CareSystemViewer key : careSystems.keySet()) {
            refs.addAll(getSourceRefs(careSystems.get(key)));
        }

        referencesList = Collections.unmodifiableList(refs);
    }

    private List<SourceReferences> getSourceRefs(ArrayList<WithInfoType<ArrayList<CareSystem>>> withInfoTypes) {
        List<SourceReferences> refs = new ArrayList<SourceReferences>();
        for(WithInfoType<ArrayList<CareSystem>> infoType : withInfoTypes) {
            for(CareSystem cs : infoType.value) {
                List<SourceReferences> rf = cs.references;
                refs.addAll(rf);
            }
        }
        return Collections.unmodifiableList(refs);
    }

    private WithInfoType<ArrayList<CareSystem>> getOrCreateEntry(CareSystemViewer source, InformationType informationType) {
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

    @Override
    public String toString() {
        return "SummaryReport{" +
                "careSystems=" + careSystems +
                '}';
    }
}
