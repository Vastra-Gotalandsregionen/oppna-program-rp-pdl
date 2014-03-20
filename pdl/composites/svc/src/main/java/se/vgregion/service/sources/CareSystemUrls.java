package se.vgregion.service.sources;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.vgregion.domain.decorators.Maybe;

import java.util.HashMap;

@Service
public class CareSystemUrls {

    @Value("${pdl.careapps}")
    private String careapps;

    public Maybe<String> getUrlForSystem( String systemId ) {
        HashMap<String, String> careSystemUrls = convertUrlsToMap();

        if(careSystemUrls.containsKey(systemId)) {
            return Maybe.some(careSystemUrls.get(systemId));
        } else {
            return Maybe.none();
        }
    }

    private HashMap<String, String> convertUrlsToMap() {
        HashMap<String, String> careSystemUrls = new HashMap<String, String>();

        String[] keyvalues = careapps.split(",");

        for(String keyvalue : keyvalues) {
            String[] pair = keyvalue.split("=");
            careSystemUrls.put(pair[0], pair[1]);
        }

        return careSystemUrls;
    }

    public HashMap<String,String> getUrls() {
        return convertUrlsToMap();
    }
}
