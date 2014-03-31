package se.vgregion.service.search;

import se.vgregion.domain.decorators.WithOutcome;

public interface LdapService {
    WithOutcome<String> getHsaIdByVgrId(String vgrId);
}
