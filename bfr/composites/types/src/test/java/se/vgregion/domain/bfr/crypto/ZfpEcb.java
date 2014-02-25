package se.vgregion.domain.bfr.crypto;


import org.junit.Test;
import se.vgregion.domain.decorators.Maybe;

import static org.junit.Assert.assertTrue;

public class ZfpEcb {

    //String id = "1.2.752.30.104.9910751.7741310.20081111190143";

    @Test
    public void zfpEcbSpike() throws Exception {
        String id = "1.2.752.30.104.1144953.9231176.20130413113626";
        String user = "TestUser";
        String password = "<Omitted>";
        String key = "244589c9e6fa56635f67f97cdbd226363465b7055cb86720";

        Maybe<String> token = ZeroFootPrintEcb.getToken(id, user, password, key);

        assertTrue(token.success);
        System.out.println("Token = " + token.value);
        // ZFP?lights=off&mode=Inbound#pl=<encypted-playload>
    }




}
