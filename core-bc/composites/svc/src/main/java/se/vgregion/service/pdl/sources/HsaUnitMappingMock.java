package se.vgregion.service.pdl.sources;

import se.vgregion.domain.pdl.CareSystem;
import se.vgregion.domain.pdl.CareSystemSource;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HsaUnitMappingMock {
    public static final Map<String, CareSystem> hsaUnitMapping;

    //SE2321000131-E000000003519 – Kärlkir - SE2321000131-E000000008059 - Verksamhet Kärl-Thorax
    //SE2321000131-E000000001134 - Radiologisk teknik - SE2321000131-E000000001123 - Verksamhet Medicinsk fysik och teknik
    //SE2321000131-E000000002764 - SU/S Med Obesitasmottagning - SE2321000131-E000000000776 - Verksamhet Medicin

    static {
        HashMap<String, CareSystem> tmpMapping = new HashMap<String, CareSystem>();

        tmpMapping.put("SE2321000131-E000000002389", new CareSystem(
                CareSystemSource.BFR,
                "SE2321000131-E000000000001",
                "VGR",
                "SE2321000131-E000000002266",
                "Verksamhet Medicin Geriatrik och Akutmottagning Östra"
        )
        );

        tmpMapping.put("SE2321000131-E000000000992", new CareSystem(
                CareSystemSource.BFR,
                "SE2321000131-E000000000001",
                "VGR",
                "SE2321000131-E000000000992",
                "SS Kir Vårdavd 137 Trauma"
        )
        );

        tmpMapping.put("SE2321000131-E000000000994", new CareSystem(
                CareSystemSource.BFR,
                "SE2321000131-E000000000001",
                "VGR",
                "SE2321000131-E000000000994",
                "Neuro Spinalenheten Avd 28 SS"
        )
        );

        tmpMapping.put("SE2321000131-E000000004004", new CareSystem(
                CareSystemSource.BFR,
                "SE2321000131-E000000000001",
                "VGR",
                "SE2321000131-E000000004004",
                "Neuro Spinalenheten Avd 28 SS")
        );

        tmpMapping.put("SE2321000131-E000000004004", new CareSystem(
                CareSystemSource.BFR,
                "SE2321000131-E000000000001",
                "VGR",
                "SE2321000131-E000000003990",
                "Verksamhet Ortopedi"
        )
        );

        tmpMapping.put("SE2321000131-E000000002171", new CareSystem(
                CareSystemSource.BFR,
                "SE2321000131-E000000000001",
                "VGR",
                "SE2321000131-E000000002165",
                "Verksamhet Kardiologi"
        )
        );

        tmpMapping.put("SE2321000131-E000000002689", new CareSystem(
                CareSystemSource.BFR,
                "SE2321000131-E000000000001",
                "VGR",
                "SE2321000131-E000000000777",
                "Verksamhet Njurmedicin"
        )
        );

        tmpMapping.put("SE2321000131-E000000003519", new CareSystem(
                CareSystemSource.BFR,
                "SE2321000131-E000000000001",
                "VGR",
                "SE2321000131-E000000003519",
                "Kärlkir"
        )
        );

        tmpMapping.put("SE2321000131-E000000003997", new CareSystem(
                CareSystemSource.BFR,
                "SE2321000131-E000000000001",
                "VGR",
                "SE2321000131-E000000003990",
                "Verksamhet Ortopedi"
        )
        );

        tmpMapping.put("SE2321000131-E000000001033", new CareSystem(
                CareSystemSource.BFR,
                "SE2321000131-E000000000001",
                "VGR",
                "SE2321000131-E000000000797",
                "Verksamhet Operation-Anestesi-IVA"
        )
        );

        tmpMapping.put("SE2321000131-E000000001002", new CareSystem(
                CareSystemSource.BFR,
                "SE2321000131-E000000000001",
                "VGR",
                "SE2321000131-E000000000977",
                "Verksamhet Neurosjukvård"
        )
        );

        tmpMapping.put("SE2321000131-E000000001721", new CareSystem(
                CareSystemSource.BFR,
                "SE2321000131-E000000000001",
                "VGR",
                "SE2321000131-E000000000740",
                "Verksamhet Medicin och Akutverksamhet"
        )
        );

        tmpMapping.put("SE2321000131-E000000001104", new CareSystem(
                CareSystemSource.BFR,
                "SE2321000131-E000000000001",
                "VGR",
                "SE2321000131-E000000000979",
                "Verksamhet Ögonsjukvård"
        )
        );

        tmpMapping.put("SE2321000131-E000000000808", new CareSystem(
                CareSystemSource.BFR,
                "SE2321000131-E000000000001",
                "VGR",
                "SE2321000131-E000000000789",
                "Verksamhet Medicin"
        )
        );

        tmpMapping.put("SE2321000131-E000000001134", new CareSystem(
                CareSystemSource.BFR,
                "SE2321000131-E000000000001",
                "VGR",
                "SE2321000131-E000000001134",
                "Radiologisk teknik"
        )
        );

        tmpMapping.put("SE2321000131-E000000002188", new CareSystem(
                CareSystemSource.BFR,
                "SE2321000131-E000000000001",
                "VGR",
                "SE2321000131-E000000008059",
                "Verksamhet Kärl-Thorax"
        )
        );

        tmpMapping.put("SE2321000131-E000000003999", new CareSystem(
                CareSystemSource.BFR,
                "SE2321000131-E000000000001",
                "VGR",
                "SE2321000131-E000000003990",
                "Verksamhet Ortopedi"
        )
        );

        tmpMapping.put("SE2321000131-E000000000995", new CareSystem(
                CareSystemSource.BFR,
                "SE2321000131-E000000000001",
                "VGR",
                "SE2321000131-E000000000977",
                "Verksamhet Neurosjukvård"
        )
        );

        tmpMapping.put("SE2321000131-E000000002713", new CareSystem(
                CareSystemSource.BFR,
                "SE2321000131-E000000000001",
                "VGR",
                "SE2321000131-E000000008516",
                "Verksamhet Psykiatri Affektiva II"
        )
        );

        tmpMapping.put("SE2321000131-E000000000964", new CareSystem(
                CareSystemSource.BFR,
                "SE2321000131-E000000000001",
                "VGR",
                "SE2321000131-E000000000774",
                "Verksamhet Kirurgi Sahlgrenska"
        )
        );

        tmpMapping.put("SE2321000131-E000000002764", new CareSystem(
                CareSystemSource.BFR,
                "SE2321000131-E000000000001",
                "VGR",
                "SE2321000131-E000000002764",
                "SU/S Med Obesitasmottagning"
        )
        );

        tmpMapping.put("SE2321000131-E000000002395", new CareSystem(
                CareSystemSource.BFR,
                "SE2321000131-E000000000001",
                "VGR",
                "SE2321000131-E000000002266",
                "Verksamhet Medicin Geriatrik och Akutmottagning Östra"
        )
        );

        tmpMapping.put("SE2321000131-E000000000973", new CareSystem(
                CareSystemSource.BFR,
                "SE2321000131-E000000000001",
                "VGR",
                "SE2321000131-E000000008934",
                "Verksamhet Geriatrik Lungmedicin och Allergologi"
        )
        );

        tmpMapping.put("SE2321000131-E000000000992", new CareSystem(
                CareSystemSource.BFR,
                "SE2321000131-E000000000001",
                "VGR",
                "SE2321000131-E000000003990",
                "Verksamhet Ortopedi"
        )
        );

        tmpMapping.put("SE2321000131-E000000003998", new CareSystem(
                CareSystemSource.BFR,
                "SE2321000131-E000000000001",
                "VGR",
                "SE2321000131-E000000003990",
                "Verksamhet Ortopedi"
        )
        );

        tmpMapping.put("SE2321000131-E000000004033", new CareSystem(
                CareSystemSource.BFR,
                "SE2321000131-E000000000001",
                "VGR",
                "SE2321000131-E000000000774",
                "Verksamhet Kirurgi Sahlgrenska"
        )
        );

        hsaUnitMapping = Collections.unmodifiableMap(tmpMapping);
    }
}
