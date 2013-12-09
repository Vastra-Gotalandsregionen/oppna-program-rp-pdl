package se.vgregion.domain.pdl;

import org.junit.Before;
import org.junit.Test;
import se.vgregion.domain.pdl.decorators.InfoTypeState;
import se.vgregion.domain.pdl.decorators.WithBlock;
import se.vgregion.domain.pdl.decorators.WithInfoType;
import se.vgregion.domain.pdl.decorators.WithOutcome;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CareSystemReportSpec {

    private PdlReport mockReport;

    private PdlContext ctx;
    private String sameProvider;
    private String otherProvider;

    @Before
    public void setup() throws Exception
    {

        otherProvider = "SE2321000131-S000000010452";
        sameProvider = "SE2321000131-S000000020452";

        HashMap<String, AssignmentAccess> assignments = new HashMap<String, AssignmentAccess>();
        List<Access> otherProviders = Arrays.asList(Access.otherProvider("SE2321000131-E000000000011"));
        List<Access> sameProviders = Arrays.asList(Access.sameProvider("SE2321000131-S000000010252"), Access.sameProvider("SE2321000131-S000000010251"));
        assignments.put(otherProvider, new AssignmentAccess("Sammanhållen Journalföring", otherProviders));
        assignments.put(sameProvider, new AssignmentAccess("Vård och behandling", sameProviders));


        ctx = new PdlContext(
                "VGR",
                "SE2321000131-E000000000001",
                "Sahlgrenska, Radiologi 32",
                "SE2321000131-S000000010252",
                "Ludvig Läkare",
                "SE2321000131-P000000069215",
                assignments
        );

        CareSystem sameUnit1 = new CareSystem("Same Unit 1", ctx.careProviderHsaId, "VGR", ctx.careUnitHsaId, "Unit 1");
        CareSystem sameUnit2 = new CareSystem("Same Unit 2", ctx.careProviderHsaId, "VGR", ctx.careUnitHsaId, "Unit 1");

        String otherCareUnitHsaId = "SE2321000131-S000000010251";
        CareSystem otherUnit1 = new CareSystem("Other Unit 1", ctx.careProviderHsaId, "VGR", otherCareUnitHsaId, "Unit 2");
        CareSystem otherUnit2 = new CareSystem("Other Unit 2", ctx.careProviderHsaId, "VGR", otherCareUnitHsaId, "Unit 2");

        String otherCareProviderHsaId = "SE2321000131-E000000000011";
        CareSystem otherProvider1 = new CareSystem("Other Provider 1", otherCareProviderHsaId, "Capio Axess Lundby", ctx.careUnitHsaId, "Unit 3");
        CareSystem otherProvider2 = new CareSystem("Other Provider 2", otherCareProviderHsaId, "Capio Axess Lundby", otherCareUnitHsaId, "Unit 4");

        ArrayList<WithInfoType<WithBlock<CareSystem>>> sourceSystems = new ArrayList<WithInfoType<WithBlock<CareSystem>>>();
        sourceSystems.add(wrapSystem(InformationType.LAK, false, sameUnit1));
        sourceSystems.add(wrapSystem(InformationType.LAK, false, sameUnit2));
        sourceSystems.add(wrapSystem(InformationType.LAK, false, otherUnit1));
        sourceSystems.add(wrapSystem(InformationType.UPP, true, otherUnit2));
        sourceSystems.add(wrapSystem(InformationType.LAK, false, otherProvider1));
        sourceSystems.add(wrapSystem(InformationType.FUN, true, otherProvider2));

        WithOutcome<ArrayList<WithInfoType<WithBlock<CareSystem>>>> systems = WithOutcome.success(sourceSystems);

        WithOutcome<CheckedConsent> consent = WithOutcome.remoteFailure(
                new CheckedConsent(
                        PdlReport.ConsentType.Consent,
                        false
                )
        );

        WithOutcome<Boolean> relationship = WithOutcome.clientError(false);

        mockReport = new PdlReport(
                systems,
                consent,
                relationship
        );
    }

    @Test
    public void CareSystemReportShouldSegmentSystems() throws Exception {

        CareSystemsReport report = new CareSystemsReport(ctx, otherProvider , mockReport);

        assertEquals(Outcome.SUCCESS, report.aggregatedSystems.outcome);
        assertEquals(2, report.aggregatedSystems.value.size());

    }

    @Test
    public void CareSystemReportShouldRemoveOtherProviders() throws Exception {

        CareSystemsReport report = new CareSystemsReport(ctx, sameProvider, mockReport);

        assertEquals(Outcome.SUCCESS, report.aggregatedSystems.outcome);
        assertEquals(2, report.aggregatedSystems.value.size());
    }

    private WithInfoType<WithBlock<CareSystem>> wrapSystem(
            InformationType informationType,
            Boolean blocked,
            CareSystem system
    ) {
        WithBlock<CareSystem> blockSystem = null;

        if(blocked) blockSystem = WithBlock.blocked(system);
        else blockSystem = WithBlock.unblocked(system);

        return new WithInfoType<WithBlock<CareSystem>>(informationType, blockSystem);
    }

    @Test
    public void CareSystemsReportShouldIndicateBlockedInfoType() throws Exception {
        CareSystemsReport report = new CareSystemsReport(ctx, sameProvider, mockReport);

        assertTrue(report.containsBlockedInfoTypes.get(Visibility.OTHER_CARE_UNIT));

        int i = 0;
        for(InfoTypeState<InformationType> ss : report.aggregatedSystems.value.keySet()) {
            if(ss.value == InformationType.UPP) {
                Boolean test = ss.containsOnlyBlocked.get(ss.lowestVisibility);
                assertTrue(test);
                i++;
            }
        }

        assertEquals(1, i);

    }
}
