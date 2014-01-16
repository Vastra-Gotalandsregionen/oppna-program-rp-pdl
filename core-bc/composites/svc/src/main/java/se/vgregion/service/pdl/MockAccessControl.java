package se.vgregion.service.pdl;


import org.springframework.stereotype.Service;
import se.vgregion.domain.assignment.Access;
import se.vgregion.domain.assignment.Assignment;
import se.vgregion.domain.decorators.WithOutcome;
import se.vgregion.domain.pdl.PdlContext;

import java.util.Arrays;
import java.util.TreeMap;
import java.util.TreeSet;

@Service("MockAccessControl")
public class MockAccessControl implements AccessControl {

    public static final String SJF = "SJF";
    public static final String VE = "VE";
    public static final String VG = "VG";
    public static final String careProviderHsaId = "SE2321000131-E000000000001";
    public static final String careUnitHsaId = "SE2321000131-E000000010252";

    @Override
    public WithOutcome<PdlContext> getContextByEmployeeId(String hsaId) {
        return WithOutcome.success(getMockContext());
    }

    public static PdlContext getMockContext() {

        TreeMap<String, Assignment> assignments = new TreeMap<String, Assignment>();

        TreeSet<Access> otherProviders = new TreeSet<Access>(
                Arrays.asList(
                        Access.fromMiuRights("Läsa;lak;SJF"),
                        Access.fromMiuRights("Läsa;und;SJF"),
                        Access.fromMiuRights("Läsa;upp;SJF"),
                        Access.fromMiuRights("Läsa;vbe;SJF")
                )
        );


        TreeSet<Access> sameCareGiver = new TreeSet<Access>(
                Arrays.asList(
                        Access.fromMiuRights("Läsa;lak;VG"),
                        Access.fromMiuRights("Läsa;und;VG"),
                        Access.fromMiuRights("Läsa;upp;VG"),
                        Access.fromMiuRights("Läsa;vbe;VG")
                )
        );

        TreeSet<Access> sameProviders = new TreeSet<Access>(
                Arrays.asList(
                        Access.fromMiuRights("Läsa;und;VE"),
                        Access.fromMiuRights("Läsa;lak;VE"),
                        Access.fromMiuRights("Läsa;upp;VE"),
                        Access.fromMiuRights("Läsa;vbe;VE")
                )
        );
        assignments.put(
                SJF,
                new Assignment(
                        SJF,
                        "Sammanhållen Journalföring",
                        careProviderHsaId,
                        careUnitHsaId,
                        "careProviderDisplayNameOther",
                        "careUnitDisplayNameOther",
                        otherProviders
                )
        );

        assignments.put(
                VG,
                new Assignment(
                        VG,
                        "Vård och behandling - Utökad",
                        careProviderHsaId,
                        careUnitHsaId,
                        "careProviderDisplayNameSame",
                        "careUnitDisplayNameSame",
                        sameCareGiver
                )
        );

        assignments.put(
                VE,
                new Assignment(
                        VE,
                        "Vård och behandling",
                        careProviderHsaId,
                        careUnitHsaId,
                        "careProviderDisplayNameSame",
                        "careUnitDisplayNameSame",
                        sameProviders
                )
        );


        return new PdlContext(
                "Ludvig Läkare",
                "SE2321000131-P000000069215",
                assignments
        );
    }

}
