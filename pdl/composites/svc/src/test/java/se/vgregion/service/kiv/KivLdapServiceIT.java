package se.vgregion.service.kiv;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.vgregion.domain.decorators.Outcome;
import se.vgregion.domain.decorators.WithOutcome;

import static org.junit.Assert.*;

/**
 * Run these tests when needed. They are set as @Ignore as they need a properties file in your user home with
 * e.g. credentials.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:ldap-context.xml", "classpath:ldap-context-properties.xml"})
public class KivLdapServiceIT {

    @Autowired
    private KivLdapService kivLdapService;

    @Test
    @Ignore
    public void testFetchHsaId() throws Exception {

        WithOutcome<String> hsaId = kivLdapService.getHsaIdByVgrId("patbe5");

        System.out.println(hsaId.value);
        assertNotNull(hsaId.value);
    }

    @Test
    @Ignore
    public void testFetchHsaIdExceptionNotFound() {
        WithOutcome<String> outcome = kivLdapService.getHsaIdByVgrId("asdfasdf");// Not found
        assertTrue(outcome.outcome.equals(Outcome.UNFULFILLED_FAILURE));
    }

    @Test
    @Ignore
    public void testFetchHsaIdExceptionMultipleMatches() {
        try {
            WithOutcome<String> outcome = kivLdapService.getHsaIdByVgrId("mar*");// Multiple matches
            fail();
        } catch (RuntimeException e) {
            // Expected
        }
    }

}
