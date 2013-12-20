package se.vgregion.service;

import se.vgregion.domain.assignment.Access;
import se.vgregion.domain.assignment.Assignment;
import se.vgregion.domain.pdl.PdlContext;

import java.util.Arrays;
import java.util.TreeMap;
import java.util.TreeSet;

public class MockContext {

    private MockContext() {
        // Utility class, private constructor.
    }

    public static final String SJF = "SJF";
    public static final String VE = "VE";
    public static final String careProviderHsaId = "SE2321000131-E000000000001";
    public static final String careUnitHsaId = "SE2321000131-E000000010252";
    public static final String otherCareUnitHsaId = "SE2321000131-E000000010251";
    public static final String otherCareProviderHsaId = "SE2321000131-E000000000011";

    public static PdlContext getMockContext() {

        TreeMap<String, Assignment> assignments = new TreeMap<String, Assignment>();

        TreeSet<Access> otherProviders = new TreeSet<Access>(
                Arrays.asList(
                        Access.fromMiuRights("Läsa;lko;SJF").value,
                        Access.fromMiuRights("Läsa;fun;SJF").value
                )
        );

        TreeSet<Access> sameProviders = new TreeSet<Access>(
                Arrays.asList(
                        Access.fromMiuRights("Läsa;und;SE2321000131-S000000010252").value,
                        Access.fromMiuRights("Läsa;lak;"+otherCareUnitHsaId).value,
                        Access.fromMiuRights("Läsa;upp;"+otherCareUnitHsaId).value
                )
        );
        assignments.put(
                SJF,
                new Assignment(
                        SJF,
                        "Sammanhållen Journalföring",
                        otherCareProviderHsaId,
                        otherCareUnitHsaId,
                        "careProviderDisplayNameOther",
                        "careUnitDisplayNameOther",
                        otherProviders
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
