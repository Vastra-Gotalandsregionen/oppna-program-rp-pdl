package se.vgregion.service.hsa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import java.util.Hashtable;
import java.util.Scanner;
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

    private static final String HEALTH_CARE_UNIT_CLASS_IN_LDAP = "hsaHealthCareUnit";

    private static AtomicReference<ConcurrentHashMap<CareProviderUnitHsaId, CareProviderUnit>> careProviderUnitsByUnitHsaId;

    static {
        careProviderUnitsByUnitHsaId = new AtomicReference<ConcurrentHashMap<CareProviderUnitHsaId, CareProviderUnit>>();
        careProviderUnitsByUnitHsaId.set(new ConcurrentHashMap<CareProviderUnitHsaId, CareProviderUnit>());
    }

    @Resource(name = "hsaOrgmaster")
    private HsaWsResponderInterface hsaOrgmaster;

    @Resource(name = "hsaLdapProperties")
    private Hashtable hsaLdapProperties;

    @Autowired
    private CareAgreement careAgreements;

    @Value("${pdl.orgMasterServicesHsaId}")
    private String orgmasterHsaId;

    private DirContext dirContext;

    public HsaUnitMappingCache() {
    }

    public HsaUnitMappingCache(
            HsaWsResponderInterface hsaOrgmaster,
            CareAgreement careAgreement,
            String orgmasterHsaId) {
        this.hsaOrgmaster = hsaOrgmaster;
        this.careAgreements = careAgreement;
        this.orgmasterHsaId = orgmasterHsaId;
    }

    @PostConstruct
    public void init() {

        try {
            dirContext = new InitialDirContext(hsaLdapProperties);
        } catch (NamingException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Fetches all care units for each care provider given as argument from webservice. For each care unit found an
     * object instance is stored in a map where the key is the concatenation of its care provider HSA ID and its own
     * HSA ID.
     *
     * @param careProviders
     * @param hsaOrgmaster
     * @param logicalAddressHsaId
     */
    public static void doCacheUpdate(Set<String> careProviders, HsaWsResponderInterface hsaOrgmaster, String logicalAddressHsaId) {

        ConcurrentHashMap<CareProviderUnitHsaId, CareProviderUnit> replaceCareProviderUnits =
                new ConcurrentHashMap<CareProviderUnitHsaId, CareProviderUnit>();

        for(String careProvider : careProviders) {
            try {

                LOGGER.debug("Lookup for care provider id {}.", careProvider);

                GetCareUnitListResponseType careUnitList = hsaOrgmaster.getCareUnitList(
                        HsaWsUtil.getAttribute(logicalAddressHsaId),
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
        doCacheUpdate(careProviders, hsaOrgmaster, orgmasterHsaId);
    }


    @Override
    public WithOutcome<Maybe<CareProviderUnit>> toCareProviderUnit(String hsaUnitId) {
        Maybe<CareProviderUnit> emptyResult = Maybe.none();
        WithOutcome<Maybe<CareProviderUnit>> outcome = WithOutcome.success(emptyResult);

        try {
            // Find the careUnitHsaId of which the unit with hsaUnitId is a member of.
            GetCareUnitResponseType careUnitResponse = hsaOrgmaster.getCareUnit(
                    HsaWsUtil.getAttribute(orgmasterHsaId),
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
            } else {
                // The hsaUnitId isn't a member unit

                // We take a chance that we find it in our cache under VGR.
                CareProviderUnit careProviderUnitFromCache = careProviderUnitsByUnitHsaId.get().get(
                        new CareProviderUnitHsaId(CareAgreement.VGR, hsaUnitId));

                if (careProviderUnitFromCache != null) {
                    String careProviderHsaId = careProviderUnitFromCache.careProviderHsaId;
                    String careUnitHsaId = careProviderUnitFromCache.careUnitHsaId;

                    Maybe<CareProviderUnit> careProviderUnit =
                            toCareProviderUnit(careProviderHsaId, careUnitHsaId);

                    outcome = WithOutcome.success(careProviderUnit);
                } else {
                    // Last resort - lookup in HSA LDAP
                    LOGGER.info("Last resort for " + hsaUnitId);

                    CareProviderUnit unit = lookupInLdap(hsaUnitId);

                    if (unit != null) {
                        Maybe<CareProviderUnit> some = Maybe.some(unit);

                        outcome = WithOutcome.success(some);
                    }
                }
            }
        } catch (HsaWsFault hsaWsFault) {
            outcome = WithOutcome.remoteFailure(emptyResult);
            LOGGER.error("Error when performing lookup on Unit HSA-ID to CareUnit HSA-ID with HSA-ID {}.", hsaUnitId, hsaWsFault);
        }

        return outcome;
    }

    /**
     * Searches for an entity with the given HSA ID. If found it is checked whether the entity is hsaHealthCareUnit.
     * Then it is assumed it has an attribute "hsaresponsiblehealthcareprovider". Together the results are used to
     * create a {@link CareProviderUnit} which is returned.
     *
     * @param hsaUnitId The HSA ID of (hopefully) an hsaHealthCareUnit
     * @return a {@link CareProviderUnit}
     */
    public CareProviderUnit lookupInLdap(String hsaUnitId) {

        if (dirContext == null) {
            LOGGER.warn("dirContext = null");
            return null;
        }

        try {

            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            NamingEnumeration<SearchResult> search = dirContext.search("", "hsaIdentity=" + hsaUnitId, searchControls);

            if (search.hasMore()) {
                SearchResult searchResult = search.next();
                NamingEnumeration<?> objectclass = searchResult.getAttributes().get("objectclass").getAll();

                while (objectclass.hasMore()) {
                    String next = (String) objectclass.next();

                    if (next.equals(HEALTH_CARE_UNIT_CLASS_IN_LDAP)) {
                        // It is a health care unit

                        String dnString = searchResult.getName();

                        String healthCareUnitName = getSimpleName(dnString);

                        String careProviderHsaId = (String) searchResult.getAttributes().get("hsaresponsiblehealthcareprovider").get();

                        NamingEnumeration<SearchResult> searchCareProvider = dirContext.search("", "hsaIdentity=" + careProviderHsaId, searchControls);

                        dnString = searchCareProvider.next().getName();

                        String careProviderDisplayName = getSimpleName(dnString);

                        CareProviderUnit unit = new CareProviderUnit(careProviderHsaId, careProviderDisplayName, hsaUnitId, healthCareUnitName);

                        return unit;
                    }
                }
            }
        } catch (NamingException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        LOGGER.info("Didn't find careProviderUnit with HSA ID " + hsaUnitId);
        return null;
    }

    private String getSimpleName(String dnString) {
        Scanner scanner = new Scanner(dnString);
        scanner.useDelimiter(",?[a-z]+="); // "ou=" and ",ou=" become delimiters so we step through everything between those matches.
        return scanner.next(); // The string after the first "ou=" is the simple name.
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


