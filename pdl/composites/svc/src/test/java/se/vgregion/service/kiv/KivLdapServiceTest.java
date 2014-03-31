package se.vgregion.service.kiv;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import se.vgregion.domain.decorators.Outcome;
import se.vgregion.domain.decorators.WithOutcome;
import se.vgregion.ldapservice.LdapService;
import se.vgregion.ldapservice.LdapUser;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class KivLdapServiceTest {

    @Mock
    private LdapService ldapService;

    private KivLdapService kivLdapService;

    @Before
    public void setup() {
        LdapUser ldapUser = mock(LdapUser.class);
        when(ldapUser.getAttributeValue("hsaIdentity")).thenReturn("theHsaId");
        when(ldapService.search(anyString(), eq("cn=theUserId"))).thenReturn(new LdapUser[]{ldapUser});
        when(ldapService.search(anyString(), eq("cn=asdfasdf"))).thenReturn(new LdapUser[0]);
        when(ldapService.search(anyString(), eq("cn=mar*"))).thenReturn(new LdapUser[]{ldapUser, ldapUser}); // Multiple users

        kivLdapService = new KivLdapService(ldapService);
    }

    @Test
    public void testFetchHsaId() throws Exception {

        WithOutcome<String> hsaId = kivLdapService.getHsaIdByVgrId("theUserId");

        System.out.println(hsaId.value);
        assertNotNull(hsaId.value);
    }

    @Test
    public void testFetchHsaIdExceptionNotFound() {
        WithOutcome<String> outcome = kivLdapService.getHsaIdByVgrId("asdfasdf");// Not found
        assertTrue(outcome.outcome.equals(Outcome.UNFULFILLED_FAILURE));
    }

    @Test
    public void testFetchHsaIdExceptionMultipleMatches() {
        try {
            WithOutcome<String> outcome = kivLdapService.getHsaIdByVgrId("mar*");// Multiple matches
            fail();
        } catch (RuntimeException e) {
            // Expected
        }
    }

}
