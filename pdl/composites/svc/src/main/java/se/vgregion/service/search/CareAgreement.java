package se.vgregion.service.search;

import java.util.Set;

public interface CareAgreement {
    static final String VGR = "SE2321000131-E000000000001";

    public boolean hasCareAgreement(String careProviderHsaId);

    public Set<String> careProvidersWithAgreement();
}
