package se.vgregion.service.search;

import se.vgregion.domain.decorators.Maybe;
import se.vgregion.domain.pdl.CareProviderUnit;

public interface HsaUnitMapper {
    public Maybe<CareProviderUnit> toCareProviderUnit(String careProviderHsaId, String careUnitHsaId);
}
