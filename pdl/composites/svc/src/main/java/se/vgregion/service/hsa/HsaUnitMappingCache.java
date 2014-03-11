package se.vgregion.service.hsa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import se.vgregion.domain.decorators.Maybe;
import se.vgregion.domain.decorators.WithOutcome;
import se.vgregion.domain.systems.CareProviderUnit;
import se.vgregion.service.search.CareAgreement;
import se.vgregion.service.search.HsaUnitMapper;
import urn.riv.hsa.HsaWs.v3.HsaWsFault;
import urn.riv.hsa.HsaWs.v3.HsaWsResponderInterface;
import urn.riv.hsa.HsaWsResponder.v3.CareUnitType;
import urn.riv.hsa.HsaWsResponder.v3.GetCareUnitListResponseType;
import urn.riv.hsa.HsaWsResponder.v3.GetCareUnitResponseType;

import javax.annotation.Resource;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
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

    static {
        careProviderUnitsByUnitHsaId = new AtomicReference<ConcurrentHashMap<CareProviderUnitHsaId, CareProviderUnit>>();
        careProviderUnitsByUnitHsaId.set(new ConcurrentHashMap<CareProviderUnitHsaId, CareProviderUnit>());
    }

    @Resource(name = "hsaOrgmaster")
    private HsaWsResponderInterface hsaOrgmaster;

    @Autowired
    private CareAgreement careAgreements;

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
            careProviderUnitsByUnitHsaId.lazySet(replaceCareProviderUnits);
        }
    }

    @Scheduled(fixedDelay=300000) // 5 min in milliseconds = 300 000
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


