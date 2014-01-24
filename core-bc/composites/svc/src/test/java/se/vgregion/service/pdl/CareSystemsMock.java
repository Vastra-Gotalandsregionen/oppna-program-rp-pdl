package se.vgregion.service.pdl;

import org.springframework.stereotype.Service;
import se.vgregion.domain.decorators.WithInfoType;
import se.vgregion.domain.decorators.WithOutcome;
import se.vgregion.domain.pdl.CareSystem;
import se.vgregion.domain.pdl.CareSystemSource;
import se.vgregion.domain.pdl.InformationType;
import se.vgregion.domain.pdl.PdlContext;

import java.util.ArrayList;

@Service("CareSystemsMock")
public class CareSystemsMock implements CareSystems {

    @Override
    public WithOutcome<ArrayList<WithInfoType<CareSystem>>> byPatientId(PdlContext ctx, String patientId) {
        //noinspection unchecked
        ArrayList<WithInfoType<CareSystem>> systems = new ArrayList<WithInfoType<CareSystem>>();
        systems.add(    new WithInfoType<CareSystem>(
                InformationType.LAK,
                new CareSystem(
                        CareSystemSource.BFR,
                        "SE2321000131-E000000000001",
                        "VGR",
                        "SE2321000131-S000000010252",
                        "Sahlgrenska, Radiologi 13"
                )
        )
        );
        systems.add(
                new WithInfoType<CareSystem>(
                        InformationType.UPP,
                        new CareSystem(
                                CareSystemSource.BFR,
                                "SE2321000131-E000000000001",
                                "VGR",
                                "SE2321000131-S000000010251",
                                "Östra Sjukhuset, Hud 32"
                        )
                )
        );
        systems.add(
                new WithInfoType<CareSystem>(
                        InformationType.LAK,
                        new CareSystem(
                                CareSystemSource.BFR,
                                "SE2321000131-E000000000001",
                                "VGR",
                                "SE2321000131-S000000010252",
                                "NU-Sjukvården, Psykiatri 65"
                        )
                )
        );
        systems.add(
                new WithInfoType<CareSystem>(
                        InformationType.LAK,
                        new CareSystem(
                                CareSystemSource.BFR,
                                "SE2321000131-E000000000001",
                                "VGR",
                                "SE2321000131-S000000010254",
                                "Östra Sjukhuset, Barn- och Ungdomspsykiatri 23"
                        )
                )
        );
        systems.add(
                new WithInfoType<CareSystem>(
                        InformationType.UPP,
                        new CareSystem(
                                CareSystemSource.BFR,
                                "SE2321000132-E000000000001",
                                "Capio Axess",
                                "SE2321000132-S000000010254",
                                "Lundby Sjukhus, Radiologi 54"
                        )
                )
        );

        WithOutcome<ArrayList<WithInfoType<CareSystem>>> systemsOutcome =
                WithOutcome.success(systems);
        return systemsOutcome;
    }
}
