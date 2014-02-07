package se.vgregion.service.agreement;

import org.springframework.stereotype.Service;
import se.vgregion.service.search.CareAgreement;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

@Service
public class CareAgreementMock implements CareAgreement {

    private static final Set<String> careProviderAgreement;

    static {
        TreeSet<String> agreements = new TreeSet<String>();
        agreements.add(VGR);
        careProviderAgreement = Collections.unmodifiableSet(agreements);
    }

    @Override
    public boolean hasCareAgreement(String careProviderHsaId) {
        return careProviderAgreement.contains(careProviderHsaId);
    }

    @Override
    public Set<String> careProvidersWithAgreement() {
        return careProviderAgreement;
    }

}