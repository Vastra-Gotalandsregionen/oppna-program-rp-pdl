package se.vgregion.service.pdl;

import org.springframework.stereotype.Service;
import se.vgregion.domain.pdl.CareSystem;
import se.vgregion.domain.pdl.InformationType;
import se.vgregion.domain.pdl.PdlContext;
import se.vgregion.domain.pdl.decorators.WithInfoType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service("CareSystemsImpl")
public class CareSystemsImpl implements CareSystems {

    @Override
    public List<WithInfoType<CareSystem>> byPatientId(PdlContext ctx, String patientId) {
        //noinspection unchecked
        return Collections
                .unmodifiableList(
                        Arrays
                            .asList(
                                    new WithInfoType<CareSystem>(
                                        InformationType.UNR,
                                        new CareSystem(
                                            "Bild- och funktionsregistret",
                                            "SE2321000131-E000000000001",
                                            "Capio",
                                            "SE2321000131-S000000010252",
                                            "Lundby närsjukhus, Ortopedi 2"
                                        )
                                    ),
                                    new WithInfoType<CareSystem>(
                                        InformationType.UNR,
                                        new CareSystem(
                                            "Bild- och funktionsregistret",
                                            "SE2321000131-E000000000001",
                                            "Capio",
                                            "SE2321000131-S000000020252",
                                            "Lundby närsjukhus, Ortopedi 1"
                                        )
                                    ),
                                    new WithInfoType<CareSystem>(
                                        InformationType.UNR,
                                        new CareSystem(
                                            "Bild- och funktionsregistret",
                                            "SE2321000131-E000000000001",
                                            "Capio",
                                            "SE2321000131-S000000030252",
                                            "Lundby närsjukhus, Barn och ungdomspsykatri"
                                        )
                                    ),
                                    new WithInfoType<CareSystem>(
                                        InformationType.UNR,
                                        new CareSystem(
                                            "Bild- och funktionsregistret",
                                            "SE2321000132-E000000000001",
                                            "VGR",
                                            "SE2321000132-S000000010251",
                                            "SU Sahlgrenska, Ortopedi 11"
                                        )
                                    ),
                                    new WithInfoType<CareSystem>(
                                        InformationType.UNR,
                                        new CareSystem(
                                            "Bild- och funktionsregistret",
                                            "SE2321000132-E000000000001",
                                            "VGR",
                                            "SE2321000132-S000000010254",
                                            "NU-sjukvården, Kirurgi 12"
                                        )
                                    )
                            )
                );
    }
}

