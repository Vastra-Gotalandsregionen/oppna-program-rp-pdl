package se.vgregion.domain.pdl;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CareSystemsReport {
    public final boolean hasSameUnit;
    public final List<WithBlocks<CareSystem>> sameUnit;
    public final boolean hasSameCareProvider;
    public final List<WithBlocks<CareSystem>> sameCareProvider;
    public final boolean hasOtherCareProviders;
    public final List<WithBlocks<CareSystem>> otherCareProviders;

    public CareSystemsReport(PdlContext ctx, PdlReport pdlReport, List<CareSystem> careSystems) {

        List<WithBlocks<CareSystem>> blockedSystems = decorateCareSystems(careSystems, pdlReport);

        sameUnit = filterSameUnit(ctx.careUnitHsaId, blockedSystems);
        hasSameUnit = sameUnit.size() > 0;
        sameCareProvider = filterSameCareProvider( ctx.careProviderHsaId, ctx.careUnitHsaId, blockedSystems);
        hasSameCareProvider = sameCareProvider.size() > 0;
        otherCareProviders = filterOtherCareProviders(ctx.careProviderHsaId, blockedSystems);
        hasOtherCareProviders = otherCareProviders.size() > 0;
    }

    private List<WithBlocks<CareSystem>> decorateCareSystems(
            List<CareSystem> careSystems,
            PdlReport pdlReport
    ) {
        List<WithBlocks<CareSystem>> decorated = new ArrayList<WithBlocks<CareSystem>>();
        for( CareSystem cs : careSystems ) {
            boolean block = shouldBlock(cs, pdlReport);

            decorated.add(new WithBlocks<CareSystem>(cs, block));
        }
        return decorated;
    }

    private boolean shouldBlock(CareSystem cs, PdlReport pdlReport) {
        for(CheckedBlock cb : pdlReport.blocks ) {
            boolean systemHasInformation = cs.informationTypes.contains( cb.engagement.informationType );
            boolean isBlocked = cb.blocked == CheckedBlock.BlockStatus.BLOCKED;

            if(systemHasInformation && isBlocked) {
               return true;
            }
        }
        return false;
    }

    private List<WithBlocks<CareSystem>> filterOtherCareProviders(
            String careProviderHsaId,
            List<WithBlocks<CareSystem>> careSystems
    ) {
        List<WithBlocks<CareSystem>> filtered = new ArrayList<WithBlocks<CareSystem>>();
        for (WithBlocks<CareSystem> sys : careSystems) {
            if(!sys.value.careProviderHsaId.equals(careProviderHsaId)){
                filtered.add(sys);
            }
        }
        return Collections.unmodifiableList(filtered);

    }

    private List<WithBlocks<CareSystem>> filterSameCareProvider(
            String careProviderHsaId,
            String careUnitHsaId,
            List<WithBlocks<CareSystem>> careSystems
    ) {
        List<WithBlocks<CareSystem>> filtered = new ArrayList<WithBlocks<CareSystem>>();
        for (WithBlocks<CareSystem> sys : careSystems) {
            boolean sameCareProvider = sys.value.careProviderHsaId.equals(careProviderHsaId);
            boolean otherCareUnit = !sys.value.careUnitHsaId.equals(careUnitHsaId);

            if( sameCareProvider && otherCareUnit ) {
                filtered.add(sys);
            }
        }
        return Collections.unmodifiableList(filtered);
    }

    private List<WithBlocks<CareSystem>> filterSameUnit(
            String careUnitHsaId,
            List<WithBlocks<CareSystem>> careSystems
    ) {
        List<WithBlocks<CareSystem>> filtered = new ArrayList<WithBlocks<CareSystem>>();
        for (WithBlocks<CareSystem> sys : careSystems) {
            if(sys.value.careUnitHsaId.equals(careUnitHsaId)) {
                filtered.add(sys);
            }
        }
        return Collections.unmodifiableList(filtered);
    }

    public boolean isHasSameUnit() {
        return hasSameUnit;
    }

    public List<WithBlocks<CareSystem>> getSameUnit() {
        return sameUnit;
    }

    public boolean isHasSameCareProvider() {
        return hasSameCareProvider;
    }

    public List<WithBlocks<CareSystem>> getSameCareProvider() {
        return sameCareProvider;
    }

    public boolean isHasOtherCareProviders() {
        return hasOtherCareProviders;
    }

    public List<WithBlocks<CareSystem>> getOtherCareProviders() {
        return otherCareProviders;
    }

    @Override
    public String toString() {
        return "CareSystemsReport{" +
                "hasSameUnit=" + hasSameUnit +
                ", sameUnit=" + sameUnit +
                ", hasSameCareProvider=" + hasSameCareProvider +
                ", sameCareProvider=" + sameCareProvider +
                ", hasOtherCareProviders=" + hasOtherCareProviders +
                ", otherCareProviders=" + otherCareProviders +
                '}';
    }
}
