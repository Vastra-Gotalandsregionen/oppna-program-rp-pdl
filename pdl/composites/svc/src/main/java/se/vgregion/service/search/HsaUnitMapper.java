package se.vgregion.service.search;

import se.vgregion.domain.decorators.Maybe;
import se.vgregion.domain.decorators.WithOutcome;
import se.vgregion.domain.systems.CareProviderUnit;

public interface HsaUnitMapper {
    Maybe<CareProviderUnit> toCareProviderUnit(String careProviderHsaId, String careUnitHsaId);
    WithOutcome<Maybe<CareProviderUnit>> toCareProviderUnit(String hsaUnitId);
}
