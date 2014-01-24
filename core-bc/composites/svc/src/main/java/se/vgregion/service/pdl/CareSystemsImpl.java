package se.vgregion.service.pdl;

import org.springframework.stereotype.Service;
import se.vgregion.domain.decorators.WithInfoType;
import se.vgregion.domain.decorators.WithOutcome;
import se.vgregion.domain.pdl.CareSystem;
import se.vgregion.domain.pdl.CareSystemSource;
import se.vgregion.domain.pdl.InformationType;
import se.vgregion.domain.pdl.PdlContext;

import java.util.ArrayList;

@Service("CareSystemsImpl")
public class CareSystemsImpl implements CareSystems {

    @Override
    public WithOutcome<ArrayList<WithInfoType<CareSystem>>> byPatientId(PdlContext ctx, String patientId) {
        //noinspection unchecked
        if(patientId.equals("196503130327")) {

            ArrayList<WithInfoType<CareSystem>> systems = new ArrayList<WithInfoType<CareSystem>>();

            systems.add(
                    new WithInfoType<CareSystem>(
                            InformationType.UND,
                            new CareSystem(
                                    CareSystemSource.BFR,
                                    "SE2321000131-E000000000001",
                                    "Västra Götalandsregionen",
                                    "SE2321000131-E000000006834",
                                    "Akutklinik"
                            )
                    )
            );
            systems.add(
                    new WithInfoType<CareSystem>(
                            InformationType.VBE,
                            new CareSystem(
                                    CareSystemSource.BFR,
                                    "SE2321000131-E000000000001",
                                    "Västra Götalandsregionen",
                                    "SE2321000131-E000000006834",
                                    "Akutklinik"
                            )
                    )
            );
            systems.add(
                    new WithInfoType<CareSystem>(
                            InformationType.UND,
                            new CareSystem(
                                    CareSystemSource.BFR,
                                    "SE5565189692-0001",
                                    "Capio AB",
                                    "SE5565189692-16M",
                                    "Lundby"
                            )
                    )
            );
            systems.add(
                    new WithInfoType<CareSystem>(
                            InformationType.VBE,
                            new CareSystem(
                                    CareSystemSource.BFR,
                                    "SE2321000131-E000000000001",
                                    "Västra Götalandsregionen",
                                    "SE2321000131-E000000001166",
                                    "Frölunda Specialistsjukhus"
                            )
                    )
            );

            WithOutcome<ArrayList<WithInfoType<CareSystem>>> systemsOutcome =
                    WithOutcome.success(systems);
            return systemsOutcome;
        } else if( patientId.equals("194302173333") ) {  // VG Test

            ArrayList<WithInfoType<CareSystem>> systems = new ArrayList<WithInfoType<CareSystem>>();


            systems.add(
                    new WithInfoType<CareSystem>(
                            InformationType.VBE,
                            new CareSystem(
                                    CareSystemSource.BFR,
                                    "SE2321000131-E000000000001",
                                    "Västra Götalandsregionen",
                                    "SE2321000131-E000000010252",
                                    "Frölunda Specialistsjukhus"
                            )
                    )
            );
            systems.add(
                    new WithInfoType<CareSystem>(
                            InformationType.LAK,
                            new CareSystem(
                                    CareSystemSource.OTH,
                                    "SE2321000131-E000000000001",
                                    "Västra Götalandsregionen",
                                    "SE2321000131-E000000001267",
                                    "Backa Läkarhus 2"
                            )
                    )
            );
            systems.add(
                    new WithInfoType<CareSystem>(
                            InformationType.LAK,
                            new CareSystem(
                                    CareSystemSource.OTH,
                                    "SE2321000131-E000000000001",
                                    "Västra Götalandsregionen",
                                    "SE2321000131-E000000001266",
                                    "Backa Läkarhus"
                            )
                    )
            );
            systems.add(
                    new WithInfoType<CareSystem>(
                            InformationType.VBE,
                            new CareSystem(
                                    CareSystemSource.BFR,
                                    "SE2321000131-E000000000001",
                                    "Västra Götalandsregionen",
                                    "SE2321000131-E000000001266",
                                    "Backa Läkarhus"
                            )
                    )
            );
            systems.add(
                    new WithInfoType<CareSystem>(
                            InformationType.UND,
                            new CareSystem(
                                    CareSystemSource.BFR,
                                    "SE2321000131-E000000000001",
                                    "Västra Götalandsregionen",
                                    "SE2321000131-E000000001266",
                                    "Backa Läkarhus"
                            )
                    )
            );
            systems.add(
                    new WithInfoType<CareSystem>(
                            InformationType.UND,
                            new CareSystem(
                                    CareSystemSource.BFR,
                                    "SE5565189692-0001",
                                    "Capio AB",
                                    "SE5565189692-16M",
                                    "Lundby, Ortopedi 12"
                            )
                    )
            );

            WithOutcome<ArrayList<WithInfoType<CareSystem>>> systemsOutcome =
                    WithOutcome.success(systems);
            return systemsOutcome;
        } else {
            ArrayList<WithInfoType<CareSystem>> systems = new ArrayList<WithInfoType<CareSystem>>();

            systems.add(    new WithInfoType<CareSystem>(
                    InformationType.UND,
                    new CareSystem(
                            CareSystemSource.BFR,
                            "SE2321000131-E000000000001",
                            "Capio AB",
                            "SE2321000131-E000000010252",
                            "Lundby närsjukhus, Ortopedi 2"
                    )
            )
            );
            systems.add(
                    new WithInfoType<CareSystem>(
                            InformationType.UND,
                            new CareSystem(
                                    CareSystemSource.BFR,
                                    "SE2321000131-E000000000001",
                                    "Capio AB",
                                    "SE2321000131-E000000020252",
                                    "Lundby närsjukhus, Ortopedi 1"
                            )
                    )
            );
            systems.add(
                    new WithInfoType<CareSystem>(
                            InformationType.UND,
                            new CareSystem(
                                    CareSystemSource.BFR,
                                    "SE2321000131-E000000000001",
                                    "Capio AB",
                                    "SE2321000131-E000000030252",
                                    "Lundby närsjukhus, Barn och ungdomspsykatri"
                            )
                    )
            );
            systems.add(
                    new WithInfoType<CareSystem>(
                            InformationType.VBE,
                            new CareSystem(
                                    CareSystemSource.RRE,
                                    "SE2321000131-E000000000001",
                                    "Capio AB",
                                    "SE2321000131-E000000040252",
                                    "Lundby närsjukhus, Ortopedi 12"
                            )
                    )
            );
            systems.add(
                    new WithInfoType<CareSystem>(
                            InformationType.UND,
                            new CareSystem(
                                    CareSystemSource.BFR,
                                    "SE2321000132-E000000000001",
                                    "Västra Götalandsregionen",
                                    "SE2321000132-E000000010251",
                                    "SU Sahlgrenska, Ortopedi 11"
                            )
                    )
            );
            systems.add(
                    new WithInfoType<CareSystem>(
                            InformationType.UND,
                            new CareSystem(
                                    CareSystemSource.BFR,
                                    "SE2321000132-E000000000001",
                                    "Västra Götalandsregionen",
                                    "SE2321000132-E000000010254",
                                    "NU-sjukvården, Kirurgi 12"
                            )
                    )
            );

            WithOutcome<ArrayList<WithInfoType<CareSystem>>> systemsOutcome =
                    WithOutcome.success(systems);
            return systemsOutcome;
        }
    }
}

