package se.vgregion.domain.pdl;

import org.junit.Before;
import org.junit.Test;
import se.vgregion.domain.pdl.decorators.WithAccess;
import se.vgregion.domain.pdl.decorators.WithBlock;
import se.vgregion.domain.pdl.decorators.WithInfoType;
import se.vgregion.domain.pdl.decorators.WithOutcome;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class CareSystemReportSpec {

    private PdlReport mockReport;

    private PdlContext ctx;

    @Before
    public void setup() throws Exception
    {
        ctx = new PdlContext(
                "VGR",
                "SE2321000131-E000000000001",
                "Sahlgrenska, Radiologi 32",
                "SE2321000131-S000000010252",
                "Ludvig Läkare",
                "SE2321000131-P000000069215",
                "Sammanhållen Journalföring",
                "SE2321000131-S000000010452"
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

        WithAccess<PdlContext> c = WithAccess.withOtherProviders(ctx);
        CareSystemsReport report = new CareSystemsReport(c, mockReport);

        assertEquals(Outcome.SUCCESS, report.onlySameCareUnit.outcome);
        assertEquals(Outcome.SUCCESS, report.includeOtherCareUnit.outcome);
        assertEquals(Outcome.SUCCESS, report.includeOtherCareProvider.outcome);
        assertEquals(1, report.onlySameCareUnit.value.size());
        assertEquals(2, report.includeOtherCareUnit.value.size());
        assertEquals(3, report.includeOtherCareProvider.value.size());

    }

    @Test
    public void CareSystemReportShouldRemoveOtherProviders() throws Exception {

        WithAccess<PdlContext> c = WithAccess.sameProvider(ctx);
        CareSystemsReport report = new CareSystemsReport(c, mockReport);

        assertEquals(Outcome.SUCCESS, report.includeOtherCareProvider.outcome);
        assertEquals(1, report.onlySameCareUnit.value.size());
        assertEquals(2, report.includeOtherCareUnit.value.size());
        assertEquals(2, report.includeOtherCareProvider.value.size());
        //assertEquals(report.includeOtherCareUnit, report.includeOtherCareProvider); // FIXME 2013-12-02 : Magnus Andersson > This is not working, something messed up in compareTo?
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
}
