package se.vgregion.service.kiv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.vgregion.domain.decorators.WithOutcome;
import se.vgregion.ldapservice.LdapService;
import se.vgregion.ldapservice.LdapUser;

@Service
public class KivLdapService implements se.vgregion.service.search.LdapService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KivLdapService.class);

    private final LdapService ldapService;

    @Autowired
    public KivLdapService(LdapService ldapService) {
        this.ldapService = ldapService;
    }

    @Override
    public WithOutcome<String> getHsaIdByVgrId(String vgrId) {
        String matchingAttribute = "cn";
        LdapUser[] users = ldapService.search("", matchingAttribute + "=" + vgrId);

        if (users.length == 0) {
            LOGGER.error("No user was found matching cn=" + vgrId + ".");
            return WithOutcome.unfulfilled("");
        } else if (users.length == 1) {
            String hsaId = users[0].getAttributeValue("hsaIdentity");
            if (hsaId == null || "".equals(hsaId)) {
                LOGGER.error("No hsaId was found on user with " + matchingAttribute + "=" + vgrId + ".");
                return WithOutcome.unfulfilled("");
            }
            return WithOutcome.success(hsaId);
        } else {
            throw new RuntimeException("Multiple users were found matching pattern cn=" + vgrId + ".");
        }
    }
}
