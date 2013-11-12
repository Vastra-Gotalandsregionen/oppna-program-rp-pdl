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
                                        new CareSystem(
                                                "Bild- och funktionsregistret",
                                                Arrays.asList(Engagement.InformationType.LAK, Engagement.InformationType.UPP),
                                                "careProviderHsaId", // TODO 20131105 : Magnus Anderss > List with care providers
                                                "careUnitHsaId"      // TODO 20131105 : Magnus Anderss > List with care units
                                                // TODO 20131105 : Magnus Anderss > List with employee assigments

                                        ),
                                        new CareSystem("System X", Arrays.asList(Engagement.InformationType.LAK), "careProviderHsaId", "careUnitHsaId"),
                                        new CareSystem("Other Care Unit", Arrays.asList(Engagement.InformationType.LAK), "careProviderHsaId", "careUnitHsaId2"),
                                        new CareSystem("Other Care Giver", Arrays.asList(Engagement.InformationType.UPP), "careProviderHsaId2", "careUnitHsaId2")
                                )
                );
    }
}
