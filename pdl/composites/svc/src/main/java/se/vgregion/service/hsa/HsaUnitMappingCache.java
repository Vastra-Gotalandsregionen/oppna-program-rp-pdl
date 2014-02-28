package se.vgregion.service.hsa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import se.riv.hsa.hsaws.v3.HsaWsFault;
import se.riv.hsa.hsaws.v3.HsaWsResponderInterface;
import se.riv.hsa.hsawsresponder.v3.CareUnitType;
import se.riv.hsa.hsawsresponder.v3.GetCareUnitListResponseType;
import se.riv.hsa.hsawsresponder.v3.GetCareUnitResponseType;
import se.vgregion.domain.decorators.Maybe;
import se.vgregion.domain.decorators.WithOutcome;
import se.vgregion.domain.systems.CareProviderUnit;
import se.vgregion.service.search.CareAgreement;
import se.vgregion.service.search.HsaUnitMapper;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

class CareProviderUnitHsaId {
    public final String careProviderHsaId;
    public final String careUnitHsaId;

    public CareProviderUnitHsaId(String careProviderHsaId, String careUnitHsaId) {
        this.careProviderHsaId = careProviderHsaId;
        this.careUnitHsaId = careUnitHsaId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CareProviderUnitHsaId)) return false;

        CareProviderUnitHsaId that = (CareProviderUnitHsaId) o;

        if (!careProviderHsaId.equals(that.careProviderHsaId)) return false;
        if (!careUnitHsaId.equals(that.careUnitHsaId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = careProviderHsaId.hashCode();
        result = 31 * result + careUnitHsaId.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "CareProviderUnitHsaId{" +
                "careProviderHsaId='" + careProviderHsaId + '\'' +
                ", careUnitHsaId='" + careUnitHsaId + '\'' +
                '}';
    }
}

@Service("hsaUnitMapping")
public class HsaUnitMappingCache implements HsaUnitMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(HsaUnitMappingCache.class);

    private static AtomicReference<ConcurrentHashMap<CareProviderUnitHsaId, CareProviderUnit>> careProviderUnitsByUnitHsaId;

    @Resource(name = "hsaOrgmaster")
    private HsaWsResponderInterface hsaOrgmaster;

    @Autowired
    private CareAgreement careAgreements;

    static {
        careProviderUnitsByUnitHsaId =
                new AtomicReference<ConcurrentHashMap<CareProviderUnitHsaId, CareProviderUnit>>(new ConcurrentHashMap<CareProviderUnitHsaId , CareProviderUnit>());

        ConcurrentHashMap<CareProviderUnitHsaId, CareProviderUnit> replaceCareProviderUnits =
                new ConcurrentHashMap<CareProviderUnitHsaId, CareProviderUnit>();

        CareProviderUnit careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000002439",
                "Administration och Service"
        );
        CareProviderUnitHsaId key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000002437",
                "Akutklinik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000002435",
                "Anestesiklinik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000002505",
                "Kirurg- ortopedklinik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000002438",
                "Medicinklinik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000007328",
                "Barn- och ungdomsverksamheterna"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000008132",
                "Gynekologmottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000009344",
                "Psykiatriteam"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000007327",
                "Smärtcentrum"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000007534",
                "Sommartestaren"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000007329",
                "Vuxenspecialistcentrum"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000508",
                "Folktandvården Alingsås"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000467",
                "Folktandvården Billingen"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001660",
                "Folktandvården Bråten"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000468",
                "Folktandvården City"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000484",
                "Folktandvården Falköping"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000485",
                "Folktandvården Floby"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000494",
                "Folktandvården Floda"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000472",
                "Folktandvården Götene"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000481",
                "Folktandvården Grästorp"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000497",
                "Folktandvården Gråbo"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001658",
                "Folktandvården Gullspång"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000498",
                "Folktandvården Herrljunga"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000478",
                "Folktandvården Hjo"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000473",
                "Folktandvården Källby"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001663",
                "Folktandvården Karlsborg"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000501",
                "Folktandvården Lerum"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000474",
                "Folktandvården Magasinsgatan"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000482",
                "Folktandvården Nossebro"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000475",
                "Folktandvården Rörstrand"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000471",
                "Folktandvården Södra Ryd"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001662",
                "Folktandvården Sjukhuset Mariestad"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000477",
                "Folktandvården Skara"
        );
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "Folktandvården Skultorp",
                "SE2321000131-E000000000469"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000470",
                "Folktandvården Stöpen"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000483",
                "Folktandvården Stenstorp"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001659",
                "Folktandvården Töreboda"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001688",
                "Folktandvården Tibro"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000479",
                "Folktandvården Tidaholm"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000514",
                "Folktandvården Vårgårda"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000480",
                "Folktandvården Vara"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000005346",
                "Ambulerande Tandsköterskor Göteborg"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000139",
                "Folktandvården Åby"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000550",
                "Folktandvården Överlida"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000144",
                "Folktandvården Angered"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000146",
                "Folktandvården Askim"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000147",
                "Folktandvården Bergsjön"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000136",
                "Folktandvården Bifrost"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000149",
                "Folktandvården Björkekärr"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000490",
                "Folktandvården Boda"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000491",
                "Folktandvården Bollebygd"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000511",
                "Folktandvården Centrum Borås"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000492",
                "Folktandvården Dalsjöfors"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000150",
                "Folktandvården Frölunda Kulturhus"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000495",
                "Folktandvården Fristad"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000496",
                "Folktandvården Fritsla"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000155",
                "Folktandvården Gamlestaden Kulan"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000231",
                "Folktandvården Gibraltar"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000151",
                "Folktandvården Guldheden"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000153",
                "Folktandvården Högsbo"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000549",
                "Folktandvården Hillared"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000127",
                "Folktandvården Hindås"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000152",
                "Folktandvården Hjällbo"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000499",
                "Folktandvården Horred"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000500",
                "Folktandvården Kinna"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000154",
                "Folktandvården Kortedala"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000137",
                "Folktandvården Krokslätt"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000128",
                "Folktandvården Landvetter"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000138",
                "Folktandvården Lindome"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000129",
                "Folktandvården Mölnlycke"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000160",
                "Folktandvården Majorna"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000161",
                "Folktandvården Olskroken"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000135",
                "Folktandvården Partille"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000506",
                "Folktandvården Sätila"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000507",
                "Folktandvården Södra Torget"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000503",
                "Folktandvården Sandared"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000504",
                "Folktandvården Skene"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000162",
                "Folktandvården Skintebo"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000163",
                "Folktandvården Styrsö"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000005700",
                "Folktandvården SU/Mölndal"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000505",
                "Folktandvården Svenljunga"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000164",
                "Folktandvården Topas"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000510",
                "Folktandvården Tranemo"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000512",
                "Folktandvården Ulricehamn"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000513",
                "Folktandvården Viskafors"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000145",
                "FTV Akuttandvården Göteborg"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000005348",
                "Resurstandläkare Göteborg"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000126",
                "Folktandvården Älvängen"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000197",
                "Folktandvården Åmål"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000143",
                "Folktandvården Öckerö"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000002022",
                "Folktandvården Bengtsfors"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000148",
                "Folktandvården Biskopsgården"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000123",
                "Folktandvården Bohus"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000195",
                "Folktandvården Brålanda"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000190",
                "Folktandvården Dalaberg"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000184",
                "Folktandvården Dannebacken"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000002023",
                "Folktandvården Ed"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000167",
                "Folktandvården Färgelanda"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000186",
                "Folktandvården Granngården (stängd"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000183",
                "Folktandvården Hamburgsund"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000188",
                "Folktandvården Källstorp"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000133",
                "Folktandvården Kärna"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000157",
                "Folktandvården Kärra"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000130",
                "Folktandvården Kristinedalsgatan"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000180",
                "Folktandvården Kungshamn"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000156",
                "Folktandvården Kyrkbyn"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000169",
                "Folktandvården Lilla Edet"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000192",
                "Folktandvården Ljungskile"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000158",
                "Folktandvården Lundby"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000171",
                "Folktandvården Lysekil"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000185",
                "Folktandvården Maria Albert"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000173",
                "Folktandvården Mellerud"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000174",
                "Folktandvården Munkedal"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000124",
                "Folktandvården Nödinge"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000178",
                "Folktandvården Orust"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000232",
                "Folktandvården Selma"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000142",
                "Folktandvården Skärhamn"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000181",
                "Folktandvården Strömstad"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000140",
                "Folktandvården Strandvägen"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000189",
                "Folktandvården Sylte"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000182",
                "Folktandvården Tanumshede"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000165",
                "Folktandvården Torslanda"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000166",
                "Folktandvården Tuve"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000191",
                "Folktandvården Uddevalla City"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000193",
                "Folktandvården Vänersborg"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000131",
                "Folktandvården Västra gatan"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000132",
                "Folktandvården Ytterby"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000006077",
                "Utbildningskliniken för Barntandvård"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001642",
                "Utbildningskliniken för Vuxentandvård"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000230",
                "Brånemarkkliniken Göteborg"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000201",
                "Mun-H-Center Ågrenska Göteborg"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003929",
                "Specialistkliniken för bettfysiologi Borås"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000004615",
                "Specialistkliniken för bettfysiologi Göteborg"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001634",
                "Specialistkliniken för bettfysiologi Mölndal"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000004878",
                "Specialistkliniken för bettfysiologi Skövde"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000006453",
                "Specialistkliniken för bettfysiologi Uddevalla"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003927",
                "Specialistkliniken för endodonti  Borås"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000224",
                "Specialistkliniken för endodonti Göteborg"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000679",
                "Specialistkliniken för endodonti Mölndal"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000004877",
                "Specialistkliniken för endodonti Skövde"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000671",
                "Specialistkliniken för endodonti Uddevalla"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003930",
                "Specialistkliniken för käkkirurgi Borås"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000225",
                "Specialistkliniken för käkkirurgi Göteborg"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000682",
                "Specialistkliniken för käkkirurgi Mölndal"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000228",
                "Specialistkliniken för odontologisk radiologi Göteborg"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003924",
                "Specialistkliniken för oral protetik Borås"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001704",
                "Specialistkliniken för oral protetik Mölndal"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000004880",
                "Specialistkliniken för oral protetik Skövde"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000670",
                "Specialistkliniken för oral protetik Uddevalla"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000006473",
                "Specialistkliniken för ortodonti Borås"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003798",
                "Specialistkliniken för ortodonti Falköping"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000226",
                "Specialistkliniken för ortodonti Göteborg"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003799",
                "Specialistkliniken för ortodonti Lidköping"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000005279",
                "Specialistkliniken för ortodonti Mölndal"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000004876",
                "Specialistkliniken för ortodonti Skövde"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000667",
                "Specialistkliniken för ortodonti Uddevalla"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003316",
                "Specialistkliniken för ortodonti Vänersborg-Trollhättan"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003923",
                "Specialistkliniken för parodontologi Borås"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000227",
                "Specialistkliniken för parodontologi Göteborg"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001635",
                "Specialistkliniken för parodontologi Mölndal"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003858",
                "Specialistkliniken för parodontologi Skövde"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000668",
                "Specialistkliniken för parodontologi Uddevalla"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003928",
                "Specialistkliniken för pedodonti Borås"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000222",
                "Specialistkliniken för pedodonti Göteborg"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003261",
                "Specialistkliniken för pedodonti Mölndal"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003938",
                "Specialistkliniken för pedodonti Skövde"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000669",
                "Specialistkliniken för pedodonti Uddevalla"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000674",
                "Specialkliniken för sjukhustandv/oral med Östra sjukh Göteborg"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003926",
                "Specialkliniken för sjukhustandvård Borås"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003262",
                "Specialkliniken för sjukhustandvård Mölndal"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000007844",
                "Specialkliniken för sjukhustandvård Skövde"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000252",
                "Specialkliniken för sjukhustandvård Uddevalla"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000229",
                "Specialkliniken för sjukhustandvård/oral medicin Göteborg"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001166",
                "Frölunda Specialistsjukhus"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003599",
                "Hörselverksamheten"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003595",
                "Habiliteringen FyrBoDal"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003596",
                "Habiliteringen Göteborg och Södra Bohuslän"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003598",
                "Habiliteringen Södra Älvsborg"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003597",
                "Habiliteringen Skaraborg"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003600",
                "Synverksamheten"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003601",
                "Tolkverksamheten"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000002902",
                "Akutvårdscentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000002904",
                "Enhet för ambulanssjukvård"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000002909",
                "Enhet för rehabilitering och ortopedteknik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000004490",
                "Gyn-MVC Kungälv-Ale"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001216",
                "Klinisk fysiologi AVSLUTAD ENHET"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001189",
                "Lokalsjukhusklinik ANVÄNDS EJ I MEDCONTROL"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003512",
                "Närhälsan Örgryte-Härlanda hemsjukvård"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000008524",
                "Närhälsan Bollebygd rehabmottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000005319",
                "Närhälsan Ulricehamn rehabmottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001214",
                "NU-ambulans AVSLUTAD ENHET"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000007338",
                "Radiologisk klinik AVSLUTAD ENHET"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000009252",
                "Sjukvårdsrådgivning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000002435X",
                "Anestesiklinik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000006301",
                "Akutklinik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000004324",
                "Ambulanshelikopter"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001530",
                "Anestesi- och intensivvårdsklinik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001533",
                "Geriatrik- och rehabiliteringsklinik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001544",
                "Kirurg- och ortopedklinik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001552",
                "Laboratoriemedicin"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001555",
                "Medicinklinik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001569",
                "Psykiatrisk klinik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001560",
                "Röntgenklinik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000004648",
                "Hälsoäventyret Oasen"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000005829",
                "Närhälsan FoU primärvård Fyrbodal"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000009010",
                "Centrala Barnhälsovården"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000006009",
                "Närhälsan centrala barnhälsovården Fyrbodal"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000009992",
                "Närhälsan centrala barnhälsovården Göteborg och S Bohuslän"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000005743",
                "Närhälsan centrala barnhälsovården S Älvsborg"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000010129",
                "Närhälsan centrala barnhälsovården Skaraborg"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000009286",
                "Närhälsan psykologenheten mödra- och barnhälsovård Fyrbodal"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000008112",
                "Närhälsan psykologenheten mödra- och barnhälsovård Göteborg"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000010062",
                "Närhälsan psykologenheten mödra- och barnhälsovård S Bohuslän"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000009980",
                "Närhälsan psykologenheten mödra- och barnhälsovård Skaraborg"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000007196",
                "Närhälsan psykologenheten mödra- och barnhälsvård S Älvsborg"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000009324",
                "Område M1"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000004432",
                "Ungdomsmottagning Göta Älvdalen"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000009858",
                "Område M10 UM"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000009326",
                "Område M2"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003673",
                "Närhälsan Uddevalla barnmorskemottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000004486",
                "Område M3"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000006916",
                "Gyn-MVC Mölndal"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000004500",
                "Närhälsan Öckerö barnmorskemottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000004528",
                "Närhälsan Ale ungdomsmottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000005570",
                "Närhälsan Kungälv ungdomsmottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000004498",
                "Närhälsan Landvetter barnmorskemottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000005647",
                "Närhälsan Mölndal ungdomsmottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000005654",
                "Närhälsan Mölnlycke ungdomsmottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000004501",
                "Närhälsan Partille barnmorskemottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000005648",
                "Närhälsan Partille ungdomsmottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000006698",
                "Område M4"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000409",
                "Närhälsan Angered barnmorskemottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000697",
                "Närhälsan Askim barnmorskemottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000335",
                "Närhälsan Backa barnmorskemottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000339",
                "Närhälsan Bergsjön barnmorskemottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001682",
                "Närhälsan Biskopsgården barnmorskemottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000342",
                "Närhälsan Brämaregården barnmorskemottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000008391",
                "Närhälsan Eriksberg barnmorskemottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000006719",
                "Närhälsan Eriksberg gynekologmottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000006711",
                "Närhälsan Frölunda familjecentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000005097",
                "Närhälsan Frölunda Torg barnmorskemottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000391",
                "Närhälsan Gamlestadstorget barnmorskemottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000366",
                "Närhälsan Kungshöjd barnmorskemottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000007241",
                "Närhälsan Kungshöjd gynekologmottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000379",
                "Närhälsan Linnéstaden barnmorskemottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000713",
                "Närhälsan Majorna barnmorskemottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000007847",
                "Närhälsan Majorna gynekologmottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000005081",
                "Närhälsan Munkebäck barnmorskemottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000005934",
                "Närhälsan sexualmedicinskt centrum"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000388",
                "Närhälsan Torslanda barnmorskemottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000699",
                "Närhälsan Tuve barnmorskemottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000005894",
                "Närhälsan mödra-barnhälsovårdsteamet Haga"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000009856",
                "Område M5"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000008247",
                "Barnmorskemottagningen Mark-Svenljunga"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000005316",
                "Närhälsan Mark gynekologmottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000005261",
                "Barnmorskemottagningen Borås-Bollebygd"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000002608",
                "Ungdomsmottagningen Borås-Bollebygd"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000008246",
                "Barnmorskeverksamheten Mittenälvsborg"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003439",
                "Närhälsan Alingsås ungdomsmottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000002578",
                "Närhälsan Lerum ungdomsmottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000009860",
                "Område M8 MHV"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000009859",
                "Område M9 MHV"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003647",
                "Närhälsan Skövde barnmorskemottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000009231",
                "HSN jour Bäckefors"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000004893",
                "Närhälsan Åmål vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000002032",
                "Närhälsan Bäckefors jourcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000004905",
                "Närhälsan Bäckefors vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000004895",
                "Närhälsan Bengtsfors vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000009249",
                "Närhälsan Brastad vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000004897",
                "Närhälsan Dals-Ed vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000004901",
                "Närhälsan Färgelanda vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000005926",
                "Närhälsan Fjällbacka vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000005019",
                "Närhälsan Kungshamn vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000579",
                "Närhälsan Lysekil jourcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000004944",
                "Närhälsan Lysekil vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000004899",
                "Närhälsan Mellerud vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000004951",
                "Närhälsan Munkedal vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000009982",
                "Närhälsan Strömstad jourcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000004999",
                "Närhälsan Strömstad vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000005013",
                "Närhälsan Tanumshede vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001652",
                "Närhälsan Dagson vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000538",
                "Närhälsan Dalaberg vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000535",
                "Närhälsan Granngården vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000539",
                "Närhälsan Herrestad vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000536",
                "Närhälsan Källstorp vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000541",
                "Närhälsan Ljungskile vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000540",
                "Närhälsan Skogslyckan vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000007869",
                "Närhälsan Stenungsund jourcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000216",
                "Närhälsan Stenungsund vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000005470",
                "Närhälsan Stora Höga vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000005514",
                "Närhälsan Tjörn vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000543",
                "Närhälsan Vänerparken vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000545",
                "Närhälsan Vargön vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003843",
                "Trollhättan jourcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000009290",
                "Jourmottagningen Öckerö"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000004530",
                "Närhälsan Älvängen vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000005543",
                "Närhälsan Öckerö vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000310",
                "Närhälsan Backa vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001655",
                "Närhälsan Biskopsgården vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001708",
                "Närhälsan Bjurslätt vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000312",
                "Närhälsan Brämaregården vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000008388",
                "Närhälsan Eriksberg vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000007860",
                "Närhälsan Hisingen jourcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000329",
                "Närhälsan Kärra vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000007197",
                "Närhälsan Kungälv jourcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000321",
                "Närhälsan Kyrkbyn vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000005568",
                "Närhälsan Nordmanna vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000005569",
                "Närhälsan Solgärde vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000326",
                "Närhälsan Torslanda vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000327",
                "Närhälsan Tuve vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000212",
                "Närhälsan Åby vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000006738",
                "Närhälsan Askim vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000007859",
                "Närhälsan Frölunda jourcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000006736",
                "Närhälsan Frölunda vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000006737",
                "Närhälsan Högsbo vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000004576",
                "Närhälsan Hindås vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000213",
                "Närhälsan Krokslätt vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000006740",
                "Närhälsan Kungssten vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000004575",
                "Närhälsan Landvetter vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000214",
                "Närhälsan Lindome vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000004574",
                "Närhälsan Mölnlycke vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000006713",
                "Närhälsan Opaltorget vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000006741",
                "Närhälsan Styrsö vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001640",
                "Primärvårdsakuten Mölndal"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000009291",
                "Jourcentral Gamlestadstorget helgkväll"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000010343",
                "Närhälsan Örgryte-Härlanda hemsjukvård"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000006727",
                "Närhälsan Angered vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001638",
                "Närhälsan Björkekärr vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000313",
                "Närhälsan Ekmanska vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000006697",
                "Närhälsan flyktingmedicinsk mottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000007857",
                "Närhälsan Gamlestadstorget jourcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000005063",
                "Närhälsan Gamlestadstorget vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000316",
                "Närhälsan Gibraltargatan vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000006731",
                "Närhälsan Hjällbo vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000705",
                "Närhälsan kris- och traumamottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000007861",
                "Närhälsan Kungshöjd jourcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000320",
                "Närhälsan Kungshöjd vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000006726",
                "Närhälsan Lövgärdet vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000712",
                "Närhälsan Majorna vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000324",
                "Närhälsan Masthugget vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000380",
                "Närhälsan Olskroken vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000328",
                "Närhälsan Slottsskogen vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000007248",
                "Närhälsan Torpavallen vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000004980",
                "Närhälsan vårdcentral hemlösa"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000434",
                "Närhälsan Ängabo vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000429",
                "Närhälsan Floda vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000004567",
                "Närhälsan Furulund vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000430",
                "Närhälsan Gråbo vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001689",
                "Närhälsan Herrljunga vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000431",
                "Närhälsan Lerum vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000553",
                "Närhälsan Nossebro vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000007936",
                "Närhälsan Partille jourcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000269",
                "Närhälsan Partille vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000433",
                "Närhälsan Sörhaga vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001690",
                "Närhälsan Sollebrunn vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001691",
                "Närhälsan Vårgårda vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000005877",
                "Borås jourcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000419",
                "Närhälsan Boda vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000420",
                "Närhälsan Bollebygd vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000421",
                "Närhälsan Dalsjöfors vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000435",
                "Närhälsan Dalum vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000422",
                "Närhälsan Fristad vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000010120",
                "Närhälsan Fritsla vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000423",
                "Närhälsan Heimdal vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000437",
                "Närhälsan Horred vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000438",
                "Närhälsan Kinna vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000442",
                "Närhälsan Sätila vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000426",
                "Närhälsan Södra Torget vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000424",
                "Närhälsan Sandared vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000425",
                "Närhälsan Sjöbo vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000439",
                "Närhälsan Skene vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000441",
                "Närhälsan Svenljunga vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000427",
                "Närhälsan Trandared vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000443",
                "Närhälsan Tranemo vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000444",
                "Närhälsan Ulricehamn vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000428",
                "Närhälsan Viskafors vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000005308",
                "Skene jourcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000005307",
                "Ulricehamn jourcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000002641",
                "Familjehälsan Lidköping"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003639",
                "Familjehälsan Mariestad"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000458",
                "Närhälsan Ågårdsskogen vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000459",
                "Närhälsan Guldvingen vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000002609",
                "Närhälsan Gullspång vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003736",
                "Närhälsan Lidköping jourcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003640",
                "Närhälsan Mariestad jourcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000007323",
                "Närhälsan Mariestad vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000487",
                "Närhälsan Skara vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001665",
                "Närhälsan Töreboda vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000489",
                "Närhälsan Vara vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003733",
                "Psykisk hälsa Lidköping"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000461",
                "Närhälsan Billingen vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003739",
                "Närhälsan Falköping jourcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000455",
                "Närhälsan Floby vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000462",
                "Närhälsan Hentorp vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000465",
                "Närhälsan Hjo vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000002615",
                "Närhälsan Karlsborg vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000456",
                "Närhälsan Mösseberg vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000463",
                "Närhälsan Norrmalm vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000454",
                "Närhälsan Oden vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000002624",
                "Närhälsan Södra Ryd vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003648",
                "Närhälsan Skövde jourcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000457",
                "Närhälsan Stenstorp vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000464",
                "Närhälsan Tibro vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000466",
                "Närhälsan Tidaholm vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000552",
                "Närhälsan Tidan vårdcentral"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000002585",
                "Barn- och Ungdomsmedicinska mottagningen Alingsås"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000005735",
                "Barn- och Ungdomsmedicinska mottagningen Lerum"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000005311",
                "Barn- och ungdomsmedicinska mottagningen Mark-Svenljunga"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000008248",
                "Barn- och ungdomsmedicinska mottagningen Ulricehamn"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000005273",
                "Barn- och Ungdomsmedicinska mottagningen Viskan"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000005594",
                "Närhälsan Kungälv barn- och ungdomsmedicinsk mottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000005472",
                "Närhälsan Stenungsund barn- och ungdomsmedicinsk mottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000005515",
                "Närhälsan Tjörn barn- och ungdomsmedicinsk mottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000005544",
                "Närhälsan Öckerö barn- och ungdomsmedicinsk mottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003469",
                "Närhälsan Backa barn- och ungdomsmedicinsk mottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003470",
                "Närhälsan Biskopsgården barn- och ungdomsmedicinsk mottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000367",
                "Närhälsan Kungshöjd barn- och ungdomsmedicinsk mottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003472",
                "Närhälsan Frölunda barn- och ungdomsmedicinsk mottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000005680",
                "Närhälsan Mölndal barn- och ungdomsmedicinsk mottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000005634",
                "Närhälsan Mölnlycke barn- och ungdomsmedicinsk mottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000005616",
                "Närhälsan Partille barn- och ungdomsmedicinsk mottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000005731",
                "Bedömningsteamet"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000008434",
                "Multimodala Teamet HSN7"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000009307",
                "Område R1"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000005020",
                "Närhälsan Angered rehabmottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000005016",
                "Närhälsan Gamlestadstorget rehabmottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003510",
                "Närhälsan Olskroken rehabmottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000009308",
                "Område R2"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000009313",
                "Område R3"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000728",
                "Närhälsan Eriksberg rehabmottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000005093",
                "Närhälsan Frölunda rehabmottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000358",
                "Närhälsan Gibraltar rehabmottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000007624",
                "Närhälsan Majorna rehabmottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003445",
                "Område R6"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000009311",
                "Område R7"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000007201",
                "Närhälsan Solhem rehabmottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000009310",
                "Område R8"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000009309",
                "Område R9"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003738",
                "Närhälsan Falköping rehabmottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003646",
                "Närhälsan Skövde rehabmottagning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000005873",
                "Närhälsan sjukvårdsrådgivningen Alingsås"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000004604",
                "Närhälsan sjukvårdsrådgivningen Göteborg"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000006359",
                "Närhälsan sjukvårdsrådgivningen Skövde"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000004643",
                "Närhälsan sjukvårdsrådgivningen Uddevalla"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000006833",
                "Anestesi och operationsverksamhet"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000008451",
                "Intensivvårdsverksamhet"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001175",
                "Barn- och ungdomsklinik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001261",
                "Barn- och ungdomspsykiatrisk klinik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001363",
                "Kvinnoklinik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000009441",
                "Klinik för Bild- och funktionsmedicin"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001469",
                "Laboratoriemedicin"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001269",
                "Ortoped- och medicinteknik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001378",
                "Patologklinik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000006834",
                "Akutklinik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000008337",
                "Akutmedicinklinik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000007450",
                "Infektionsklinik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000008338",
                "Kardiologiklinik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001168",
                "Neuro- och rehabiliteringsklinik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000008339",
                "Specialistmedicinklinik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001171",
                "Ögonklinik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001276",
                "Öron- Näsa- Hals- och Käkkirurgisk klinik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001212",
                "Kirurgklinik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001172",
                "Ortopedklinik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000007849",
                "Öppenvårdsklinik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000007848",
                "Slutenvårdsklinik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000486",
                "Vårdcentral Götene"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000002486",
                "Ögonklinik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000010027",
                "Akutklinik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001802",
                "Anestesiklinik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000002460",
                "Barn- och ungdomsklinik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000002498",
                "Barn- och ungdomspsykiatrisk klinik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000002828",
                "Hjärt- och lungklinik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000002540",
                "Kirurgklinik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000008325",
                "Klinik för bild- och laboratoriemedicin"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000008331",
                "Klinik för hud-STD infektion vårdhygien och öron-näs-hals"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000002469",
                "Kvinnoklinik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000008332",
                "Medicin- och onkologklinik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000006942",
                "SÄS Hotell"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000002481",
                "Ortopedklinik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000002754",
                "Rehabiliteringsklinik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000009026",
                "Vuxenpsykiatrisk klinik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000794",
                "Verksamhet Akutsjukvård och Barnkirurgi"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001753",
                "Verksamhet Barnröntgen och Barnfysiologen"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000791",
                "Verksamhet Kardiologi inkl hjärtkirurgisk vård"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000009336",
                "Verksamhet Kvinnosjukvård"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000789",
                "Verksamhet Medicin"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000793",
                "Verksamhet Neonatologi"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000792",
                "Verksamhet Neurologi-Psykiatri-Habilitering"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000797",
                "Verksamhet Operation-Anestesi-IVA"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000002263",
                "Verksamhet Anestesi Operation IVA"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000009023",
                "Verksamhet Bemanningsservice"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000002276",
                "Verksamhet Beroende"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000002269",
                "Verksamhet Kirurgi Östra"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000002266",
                "Verksamhet Medicin Geriatrik och Akutmottagning Östra"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000748",
                "Verksamhet Neuropsykiatri"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000008515",
                "Verksamhet Psykiatri Affektiva I"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000008516",
                "Verksamhet Psykiatri Affektiva II"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000008517",
                "Verksamhet Psykiatri Psykos"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000002274",
                "Verksamhet Rättspsykiatri"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000006451",
                "Kontaktpunkt 1"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000979",
                "Verksamhet Ögonsjukvård"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000744",
                "Verksamhet An-Op-IVA"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000742",
                "Verksamhet Geriatrik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000740",
                "Verksamhet Medicin och Akutverksamhet"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003990",
                "Verksamhet Ortopedi"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003995",
                "Verksamhet Ortopedteknik och sterilteknik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001070",
                "Verksamhet Sjukgymnastik och Arbetsterapi"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000002275",
                "Verksamhet infektion"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000002173",
                "Verksamhet Klinisk fysiologi"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000006603",
                "Verksamhet Klinisk genetik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000002220",
                "Verksamhet Klinisk immunologi och transfusionsmedicin"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000002041",
                "Verksamhet Klinisk kemi"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000009206",
                "Verksamhet Klinisk mikrobiologi"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000002130",
                "Verksamhet Klinisk patologi och cytologi"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001123",
                "Verksamhet Medicinsk fysik och teknik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000009223",
                "Verksamhet radiologi"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001052",
                "Verksamhet reumatologi"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000980",
                "Verksamhet Öron- Näs- och Halssjukvård"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000771",
                "Verksamhet Anestesi-Operation-Intensivvård"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001045",
                "Verksamhet Handkirurgi och Plastikkirurgi"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001034",
                "Verksamhet Hud- och könssjukvård"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000774",
                "Verksamhet Kirurgi Sahlgrenska"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000777",
                "Verksamhet Njurmedicin"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000001715",
                "Verksamhet Onkologi"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000004088",
                "Verksamhet Transplantation"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000940",
                "Verksamhet Urologi"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000006389",
                "Verksamhet Ambulans Prehospital Akutsjukvård"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000008934",
                "Verksamhet Geriatrik Lungmedicin och Allergologi"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000008059",
                "Verksamhet Kärl-Thorax"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000002165",
                "Verksamhet Kardiologi"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000776",
                "Verksamhet Medicin"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000000977",
                "Verksamhet Neurosjukvård"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000007999",
                "Klinisk forskning verksamhetsledning"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003041",
                "Barn- och ungdomsmedicin BUM"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000006016",
                "Barn- och ungdomspsykiatri BUP"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003042",
                "Kvinnosjukvård"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000009940",
                "K1"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000009941",
                "K2"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000009942",
                "K3"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000009943",
                "K4"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000009944",
                "K5"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000009945",
                "K6"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003225",
                "Ögon"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003226",
                "Öron- näs- hals"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000008375",
                "Ambulans- och akutsjukvård"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000006290",
                "An-Op-IVA"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003221",
                "Kirurgi"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003223",
                "Ortopedi"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003224",
                "Urologi"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000009950",
                "M1"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000009951",
                "M2"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000009952",
                "M3"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000009953",
                "M4"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000009954",
                "M5"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000009955",
                "M6"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000006708",
                "Arbetsterapi och Sjukgymnastik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003331",
                "Hud"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003031",
                "Infektion"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003034",
                "Internmedicin Falköping"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003032",
                "Kardiologi"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000006426",
                "Medicin"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003038",
                "Neurologi och medicinsk rehabilitering"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000003036",
                "Njurmedicin"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000006045",
                "Vuxenpsykiatri Öppenvård"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000006046",
                "Vuxenpsykiatri Slutenvård"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000007397",
                "Bild och funktionsmedicin Radiologi SkaS"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000002968",
                "Verksamhetsområde Anestesi"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000002972",
                "Verksamhetsområde Arbetsterapi Sjukgymnastik"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000002967",
                "Verksamhetsområde Kirurgi - Urologi"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000002970",
                "Verksamhetsområde Kvinnosjukvård"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000002966",
                "Verksamhetsområde Medicin"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnit = new CareProviderUnit(
                "SE2321000131-E000000000001",
                "Västra Götalandsregionen",
                "SE2321000131-E000000004141",
                "Verksamhetsområde Ortopedi"
        );
        key = new CareProviderUnitHsaId(careProviderUnit.careProviderHsaId, careProviderUnit.careUnitHsaId);
        replaceCareProviderUnits.put(key, careProviderUnit);

        careProviderUnitsByUnitHsaId.lazySet(replaceCareProviderUnits);
    }


    public static void doCacheUpdate(Set<String> careProviders, HsaWsResponderInterface hsaOrgmaster) {

        ConcurrentHashMap<CareProviderUnitHsaId, CareProviderUnit> replaceCareProviderUnits =
                new ConcurrentHashMap<CareProviderUnitHsaId, CareProviderUnit>();

        for(String careProvider : careProviders) {
            try {

                LOGGER.debug("Lookup for care provider id {}.", careProvider);

                // FIXME 2014-02-03 : Magnus Andersson > Hard coded value, use config
                GetCareUnitListResponseType careUnitList = hsaOrgmaster.getCareUnitList(
                        HsaWsUtil.getAttribute("SE165565594230-1000"),
                        HsaWsUtil.getAttribute(java.util.UUID.randomUUID().toString()),
                        HsaWsUtil.getLookupByHsaId(careProvider)
                );

                String providerHsaId = careUnitList.getCareUnitGiverHsaIdentity();
                String providerName = careUnitList.getCareUnitGiverName();

                for(CareUnitType cu : careUnitList.getCareUnits().getCareUnit()){

                    CareProviderUnit careProviderUnit = new CareProviderUnit(
                            providerHsaId,
                            providerName,
                            cu.getHsaIdentity(),
                            cu.getCareUnitName()
                    );

                    CareProviderUnitHsaId key = new CareProviderUnitHsaId(providerHsaId, cu.getHsaIdentity());
                    replaceCareProviderUnits.put(key, careProviderUnit);
                }

            } catch(Exception e) {
                LOGGER.error("Unable to refresh cache from HSA with the Care Provider {}.", careProvider, e);
            }
        }

        if(replaceCareProviderUnits.size() > 0) {
            // Update with our new care provider units
            careProviderUnitsByUnitHsaId.lazySet(replaceCareProviderUnits);
        }
    }

    @Scheduled(fixedDelay=1800000) // 30 min in milliseconds = 1 800 000
    public void updateCache() {
        LOGGER.debug("Attempting to update cache for CareUnit lists");
        Set<String> careProviders = careAgreements.careProvidersWithAgreement();
        doCacheUpdate(careProviders, hsaOrgmaster);
    }


    @Override
    public WithOutcome<Maybe<CareProviderUnit>> toCareProviderUnit(String hsaUnitId) {
        // FIXME 2014-02-03 : Magnus Andersson > Hard coded value, use config
        Maybe<CareProviderUnit> emptyResult = Maybe.none();
        WithOutcome<Maybe<CareProviderUnit>> outcome = WithOutcome.success(emptyResult);

        try {
            GetCareUnitResponseType careUnitResponse = hsaOrgmaster.getCareUnit(
                    HsaWsUtil.getAttribute("SE165565594230-1000"),
                    HsaWsUtil.getAttribute(null),
                    HsaWsUtil.getLookupByHsaId(hsaUnitId)
            );

            boolean isValidResponse =
                    careUnitResponse != null &&
                            careUnitResponse.getCareGiver() != null &&
                            careUnitResponse.getCareUnitHsaIdentity() != null;

            if(isValidResponse) {
                String careProviderHsaId = careUnitResponse.getCareGiver();
                String careUnitHsaId = careUnitResponse.getCareUnitHsaIdentity();

                Maybe<CareProviderUnit> careProviderUnit =
                        toCareProviderUnit(careProviderHsaId, careUnitHsaId);

                outcome = WithOutcome.success(careProviderUnit);
            }
        } catch (HsaWsFault hsaWsFault) {
            outcome = WithOutcome.remoteFailure(emptyResult);
            LOGGER.error("Error when performing lookup on Unit HSA-ID to CareUnit HSA-ID with HSA-ID {}.", hsaUnitId, hsaWsFault);
        }

        return outcome;
    }

    @Override
    public Maybe<CareProviderUnit> toCareProviderUnit(String careProviderHsaId, String careUnitHsaId) {
        CareProviderUnitHsaId key = new CareProviderUnitHsaId(careProviderHsaId, careUnitHsaId);

        if(careProviderUnitsByUnitHsaId.get().containsKey(key)) {
            return Maybe.some(careProviderUnitsByUnitHsaId.get().get(key));
        }

        LOGGER.debug("Could not find {} - {} amongst careProviderUnits with agreement.", careProviderHsaId, careUnitHsaId);

        return Maybe.none();
    }
}


