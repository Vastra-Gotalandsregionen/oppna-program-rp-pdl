package se.vgregion.service.pdl;

import org.springframework.stereotype.Service;
import se.vgregion.domain.decorators.WithInfoType;
import se.vgregion.domain.decorators.WithOutcome;
import se.vgregion.domain.decorators.WithPatient;
import se.vgregion.domain.pdl.InformationType;
import se.vgregion.events.context.Patient;
import se.vgregion.domain.pdl.PdlContext;
import se.vgregion.domain.systems.CareSystem;
import se.vgregion.domain.systems.CareSystemViewer;
import se.vgregion.portal.bfr.infobroker.domain.InfobrokerPersonIdType;
import se.vgregion.service.search.CareSystems;

import java.util.ArrayList;

@Service("CareSystemsMock")
public class CareSystemsMock implements CareSystems {

    @Override
    public WithOutcome<WithPatient<ArrayList<WithInfoType<CareSystem>>>> byPatientId(PdlContext ctx, String patientId, InfobrokerPersonIdType patientIdType) {
        //noinspection unchecked
        ArrayList<WithInfoType<CareSystem>> systems = new ArrayList<WithInfoType<CareSystem>>();
        systems.add(    new WithInfoType<CareSystem>(
                InformationType.LAK,
                new CareSystem(
                        CareSystemViewer.BFR,
                        "SE2321000131-E000000000001",
                        "VGR",
                        "SE2321000131-S000000010252",
                        "Sahlgrenska, Radiologi 13",
                        null)
        )
        );
        systems.add(
                new WithInfoType<CareSystem>(
                        InformationType.UPP,
                        new CareSystem(
                                CareSystemViewer.BFR,
                                "SE2321000131-E000000000001",
                                "VGR",
                                "SE2321000131-S000000010251",
                                "Östra Sjukhuset, Hud 32",
                                null)
                )
        );
        systems.add(
                new WithInfoType<CareSystem>(
                        InformationType.LAK,
                        new CareSystem(
                                CareSystemViewer.BFR,
                                "SE2321000131-E000000000001",
                                "VGR",
                                "SE2321000131-S000000010252",
                                "NU-Sjukvården, Psykiatri 65",
                                null)
                )
        );
        systems.add(
                new WithInfoType<CareSystem>(
                        InformationType.LAK,
                        new CareSystem(
                                CareSystemViewer.BFR,
                                "SE2321000131-E000000000001",
                                "VGR",
                                "SE2321000131-S000000010254",
                                "Östra Sjukhuset, Barn- och Ungdomspsykiatri 23",
                                null)
                )
        );
        systems.add(
                new WithInfoType<CareSystem>(
                        InformationType.UPP,
                        new CareSystem(
                                CareSystemViewer.BFR,
                                "SE2321000132-E000000000001",
                                "Capio Axess",
                                "SE2321000132-S000000010254",
                                "Lundby Sjukhus, Radiologi 54",
                                null)
                )
        );

        WithPatient<ArrayList<WithInfoType<CareSystem>>> withPatient =
                new WithPatient<ArrayList<WithInfoType<CareSystem>>>(
                        new Patient("test"), systems
                );

        return WithOutcome.success(withPatient);
    }
}
