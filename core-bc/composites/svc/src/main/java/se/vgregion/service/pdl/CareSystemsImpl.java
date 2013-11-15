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
                                                "SE2321000131-E000000000001", // TODO 20131105 : Magnus Anderss > List with care providers
                                                "SE2321000131-S000000010252"      // TODO 20131105 : Magnus Anderss > List with care units
                                                // TODO 20131105 : Magnus Anderss > List with employee assigments

                                        ),
                                        new CareSystem(
                                                "System X",
                                                Arrays.asList(Engagement.InformationType.LAK),
                                                "SE2321000131-E000000000001",
                                                "SE2321000131-S000000010252"
                                        ),
                                        new CareSystem(
                                                "Other Care Unit",
                                                Arrays.asList(Engagement.InformationType.LAK),
                                                "SE2321000131-E000000000001",
                                                "SE2321000131-S000000010254"
                                        ),
                                        new CareSystem(
                                                "Other Care Giver System",
                                                Arrays.asList(Engagement.InformationType.UPP),
                                                "SE2321000132-E000000000001",
                                                "SE2321000132-S000000010254"
                                        )
                                )
                );
    }
}

