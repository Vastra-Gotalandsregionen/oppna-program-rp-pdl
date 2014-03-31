package se.vgregion.service.kiv;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.vgregion.domain.decorators.Outcome;
import se.vgregion.domain.decorators.WithOutcome;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:ldap-context.xml", "classpath:ldap-context-properties.xml"})
public class KivLdapServiceIT {

    @Autowired
    private KivLdapService kivLdapService;

    @Test
    public void testFetchHsaId() throws Exception {

        WithOutcome<String> hsaId = kivLdapService.getHsaIdByVgrId("patbe5");

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
