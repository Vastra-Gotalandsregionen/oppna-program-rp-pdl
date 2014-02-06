package se.vgregion.service.hsa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import se.riv.hsa.hsaws.v3.HsaWsResponderInterface;
import se.riv.hsa.hsawsresponder.v3.CareUnitType;
import se.riv.hsa.hsawsresponder.v3.GetCareUnitListResponseType;
import se.vgregion.domain.decorators.Maybe;
import se.vgregion.domain.pdl.CareProviderUnit;
import se.vgregion.service.search.CareAgreement;
import se.vgregion.service.search.HsaUnitMapper;

import javax.annotation.Resource;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Service("hsaUnitMapping")
public class HsaUnitMappingCache implements HsaUnitMapper {

    private static class CareProviderUnitHsaId {
        private final String careProviderHsaId;
        private final String careUnitHsaId;

        private CareProviderUnitHsaId(String careProviderHsaId, String careUnitHsaId) {
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

    private static final Logger LOGGER = LoggerFactory.getLogger(HsaUnitMappingCache.class);

    private static AtomicReference<ConcurrentHashMap<CareProviderUnitHsaId, CareProviderUnit>> careProviderUnitsByUnitHsaId;

    static {
        careProviderUnitsByUnitHsaId =
                new AtomicReference<ConcurrentHashMap<CareProviderUnitHsaId, CareProviderUnit>>(new ConcurrentHashMap<CareProviderUnitHsaId , CareProviderUnit>());
    }

    @Resource(name = "hsaOrgmaster")
    private HsaWsResponderInterface hsaOrgmaster;

    @Autowired
    private CareAgreement careAgreements;

    public static void doCacheUpdate(Set<String> careGivers, HsaWsResponderInterface hsaOrgmaster) {

        ConcurrentHashMap<CareProviderUnitHsaId, CareProviderUnit> replaceCareProviderUnits =
                new ConcurrentHashMap<CareProviderUnitHsaId, CareProviderUnit>();

        for(String careGiver : careGivers) {
            try {

                // FIXME 2014-02-03 : Magnus Andersson > Hard coded value, use config
                GetCareUnitListResponseType careUnitList = hsaOrgmaster.getCareUnitList(
                        HsaWsUtil.getAttribute("SE165565594230-1000"),
                        HsaWsUtil.getAttribute(null),
                        HsaWsUtil.getLookupByHsaId(careGiver)
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
                LOGGER.error("Unable to refresh cache from HSA with the Care Provider {}.", careGiver, e);
            }
        }

        // Update with our new care provider units
        careProviderUnitsByUnitHsaId.lazySet(replaceCareProviderUnits);
    }

    @Scheduled(fixedDelay=5000)
    public void updateCache() {
        Set<String> careProviders = careAgreements.careProvidersWithAgreement();
        doCacheUpdate(careProviders, hsaOrgmaster);
    }


    @Override
    public Maybe<CareProviderUnit> toCareProviderUnit(String careProviderHsaId, String careUnitHsaId) {
        CareProviderUnitHsaId key = new CareProviderUnitHsaId(careProviderHsaId, careUnitHsaId);

        if(careProviderUnitsByUnitHsaId.get().contains(key)) {
            return Maybe.some(careProviderUnitsByUnitHsaId.get().get(key));
        }

        return Maybe.none();
    }
}


