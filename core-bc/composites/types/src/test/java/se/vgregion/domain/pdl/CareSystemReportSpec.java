package se.vgregion.domain.pdl;

import org.junit.Test;
import se.vgregion.domain.pdl.decorators.WithBlock;
import se.vgregion.domain.pdl.decorators.WithFallback;
import se.vgregion.domain.pdl.decorators.WithInfoType;

import java.util.ArrayList;
import java.util.EnumSet;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class CareSystemReportSpec {

    @Test
    public void CareSystemReportShouldSegmentSystems() throws Exception {
        PdlContext ctx = new PdlContext(
                "SE2321000131-E000000000001",
                "SE2321000131-S000000010252",
                "SE2321000131-P000000069215",
                "Sammanhållen Journalföring",
                "SE2321000131-S000000010452"
        );

        CareSystem sameUnit1 = new CareSystem("Same Unit 1", ctx.careProviderHsaId, ctx.careUnitHsaId);
        CareSystem sameUnit2 = new CareSystem("Same Unit 2", ctx.careProviderHsaId, ctx.careUnitHsaId);

        String otherCareUnitHsaId = "SE2321000131-S000000010251";
        CareSystem otherUnit1 = new CareSystem("Other Unit 1", ctx.careProviderHsaId, otherCareUnitHsaId);
        CareSystem otherUnit2 = new CareSystem("Other Unit 2", ctx.careProviderHsaId, otherCareUnitHsaId);

        String otherCareProviderHsaId = "SE2321000131-E000000000011";
        CareSystem otherProvider1 = new CareSystem("Other Provider 1", otherCareProviderHsaId, ctx.careUnitHsaId);
        CareSystem otherProvider2 = new CareSystem("Other Provider 2", otherCareProviderHsaId, otherCareUnitHsaId);

        ArrayList<WithInfoType<WithBlock<CareSystem>>> sourceSystems = new ArrayList<WithInfoType<WithBlock<CareSystem>>>();
        sourceSystems.add(wrapSystem(InformationType.LAK, false, sameUnit1));
        sourceSystems.add(wrapSystem(InformationType.LAK, false, sameUnit2));
        sourceSystems.add(wrapSystem(InformationType.LAK, false, otherUnit1));
        sourceSystems.add(wrapSystem(InformationType.UPP, true, otherUnit2));
        sourceSystems.add(wrapSystem(InformationType.LAK, false, otherProvider1));
        sourceSystems.add(wrapSystem(InformationType.FUN, true, otherProvider2));

        WithFallback<ArrayList<WithInfoType<WithBlock<CareSystem>>>> systems =
                WithFallback.success(sourceSystems);

        WithFallback<CheckedConsent> consent =
                WithFallback.success(
                    new CheckedConsent(
                        PdlReport.ConsentType.Consent,
                        false
                    )
                );

        WithFallback<Boolean> relationship =
                WithFallback.fallback(false);

        PdlReport mockReport = new PdlReport(
            systems,
            consent,
            relationship
        );

        CareSystemsReport report = new CareSystemsReport(ctx, mockReport);

        assertFalse(report.systems.fallback);
        assertEquals(3, report.systems.value.size());
        assertEquals(4, report.systems.value.get(InformationType.LAK).size());
        assertEquals(1, report.systems.value.get(InformationType.UPP).size());
        assertEquals(1, report.systems.value.get(InformationType.FUN).size());
        assertEquals(EnumSet.of(InformationType.LAK), report.onlySameCareUnit);
        assertEquals(EnumSet.of(InformationType.LAK, InformationType.UPP), report.includeOtherCareUnit);
        assertEquals(EnumSet.of(InformationType.LAK, InformationType.UPP, InformationType.FUN), report.includeOtherCareProvider);

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
