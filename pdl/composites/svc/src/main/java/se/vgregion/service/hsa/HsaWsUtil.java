package se.vgregion.service.hsa;

import org.w3c.addressing.v1.AttributedURIType;
import urn.riv.hsa.HsaWsResponder.v3.LookupHsaObjectType;

public class HsaWsUtil {

    private HsaWsUtil() {
        // Util class, no public constructor
    }

    public static AttributedURIType getAttribute( String value ) {
        AttributedURIType uri = new AttributedURIType();
        uri.setValue(value);

        return uri;
    }

    public static LookupHsaObjectType getLookupByHsaId(String hsaId) {
        LookupHsaObjectType lookup = new LookupHsaObjectType();
        lookup.setHsaIdentity(hsaId);
        return lookup;
    }
}
