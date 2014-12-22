package se.vgregion.service.hsa;


import org.springframework.stereotype.Service;
import se.vgregion.domain.assignment.Access;
import se.vgregion.domain.assignment.Assignment;
import se.vgregion.domain.decorators.WithOutcome;
import se.vgregion.domain.pdl.PdlContext;
import se.vgregion.service.search.AccessControl;

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
        return WithOutcome.success(getMockContext(hsaId));
    }

    public static PdlContext getMockContext(String hsaId) {

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
                        "SE5565189692-0001",
                        "SE5565189692-E0123",
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
//                        "SE2321000131-E000000000774", // Verksamhet Kirurgi Sahlgrenska (där har Tian Testberg info)
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
                        "SE2321000131-E000000000001",
                        "SE2321000131-E000000001052",
                        "careProviderDisplayNameSame",
                        "careUnitDisplayNameSame",
                        sameProviders
                )
        );

        return new PdlContext(
                "Ludvig Läkare",
                hsaId,
//                "SE2321000131-P000000069215", // Susanne Lindqvist
                assignments

        );
    }

}
