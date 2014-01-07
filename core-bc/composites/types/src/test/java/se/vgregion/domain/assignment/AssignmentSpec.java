package se.vgregion.domain.assignment;

import org.junit.Test;
import se.vgregion.domain.decorators.WithOutcome;

import java.util.TreeSet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AssignmentSpec {

    @Test
    public void mustCreateAssignmentsFromMiuRights() {
        String[] mius = {
            "Läsa;dia;SJF",
            "Läsa;fun;SJF",
            "Läsa;lkf;SJF",
            "Läsa;lkm;SJF",
            "Läsa;lko;SJF",
            "Läsa;pad;SJF",
            "Läsa;pat;SJF",
            "Läsa;und;SJF",
            "Läsa;upp;SJF",
            "Läsa;vbe;SJF",
            "Läsa;vko;SJF",
            "Läsa;voo;SJF",
            "Läsa;vot;SJF",
            "Läsa;vpo;SJF"
       };

        TreeSet<Access> assignments = new TreeSet<Access>();
        WithOutcome outcome = WithOutcome.success(null);

        for(String miu : mius) {
            assignments.add(Access.fromMiuRights(miu));
        }

        Assignment ass = new Assignment(
                "assignmentHsaId",
                "assignmentDisplayName",
                "careProviderHsaId",
                "careUnitHsaId",
                "careProviderDisplayName",
                "careUnitDisplayName",
                assignments
        );

        assertTrue(outcome.success);
        assertTrue(ass.otherProviders);
        assertFalse(ass.otherUnits);
    }
}
