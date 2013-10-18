package se.vgregion.service.pdl;

import org.springframework.stereotype.Service;
import se.vgregion.domain.pdl.CareSystem;
import se.vgregion.domain.pdl.Engagement;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class CareSystemsImpl implements CareSystems {
    @Override
    public List<CareSystem> byInformationType(List<Engagement.InformationType> information) {
        return Collections
                .unmodifiableList(
                        Arrays
                                .asList(
                                        new CareSystem("Bild- och funktionsregistret", Engagement.InformationType.LAK, "careProviderHsaId", "careUnitHsaId"),
                                        new CareSystem("System X", Engagement.InformationType.LAK, "careProviderHsaId", "careUnitHsaId")
                                )
                );
    }
}
