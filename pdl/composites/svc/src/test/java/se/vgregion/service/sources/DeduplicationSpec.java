package se.vgregion.service.sources;

import org.junit.Test;
import se.vgregion.domain.decorators.WithInfoType;
import se.vgregion.domain.pdl.InformationType;
import se.vgregion.domain.systems.CareSystem;
import se.vgregion.domain.systems.CareSystemViewer;
import se.vgregion.events.context.SourceReferences;
import se.vgregion.events.context.sources.radiology.RadiologySourceRefs;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class DeduplicationSpec {

    @Test
    public void deduplicateSpecification() {
        ArrayList<WithInfoType<CareSystem>> testSystems = new ArrayList<WithInfoType<CareSystem>>();

        String providerId = "PROVIDER-1";
        String unitId = "UNIT-1";

        Map<String, SourceReferences> sr1 = new HashMap<String, SourceReferences>();
        RadiologySourceRefs rsr1 = new RadiologySourceRefs(
                new Date(),
                2,
                "care unit",
                unitId,
                "",
                "",
                "BROKERID-1"
        );
        sr1.put(rsr1.id, rsr1);

        Map<String, SourceReferences> sr2 = new HashMap<String, SourceReferences>();
        RadiologySourceRefs rsr2 = new RadiologySourceRefs(
                new Date(),
                2,
                "care unit",
                unitId,
                "",
                "",
                "BROKERID-2"
        );
        sr2.put(rsr2.id, rsr2);

        CareSystem cs1 = new CareSystem(
                CareSystemViewer.BFR,
                providerId,
                "",
                unitId,
                "",
                sr1
        );

        CareSystem cs2 = new CareSystem(
                CareSystemViewer.BFR,
                providerId,
                "",
                unitId,
                "",
                sr2
        );

        CareSystem cs3 = new CareSystem(
                CareSystemViewer.BFR,
                providerId,
                "",
                unitId+"2",
                "",
                sr2
        );

        testSystems.add(new WithInfoType<CareSystem>(InformationType.UND, cs1));
        testSystems.add(new WithInfoType<CareSystem>(InformationType.UND, cs2));
        testSystems.add(new WithInfoType<CareSystem>(InformationType.UND, cs3));
        ArrayList<WithInfoType<CareSystem>> result = Deduplication.deduplicate(testSystems);

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).value.references.size());
        assertEquals(2, result.get(1).value.references.size());

    }


}
