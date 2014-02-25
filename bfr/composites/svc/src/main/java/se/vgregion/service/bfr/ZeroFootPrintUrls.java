package se.vgregion.service.bfr;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.vgregion.domain.bfr.Referral;
import se.vgregion.domain.bfr.Study;
import se.vgregion.domain.bfr.crypto.ZeroFootPrintEcb;
import se.vgregion.domain.decorators.Maybe;
import se.vgregion.domain.decorators.WithOutcome;

import java.util.ArrayList;
import java.util.List;

@Service
public class ZeroFootPrintUrls {

    @Value("${zfp.url}")
    private String zfpUrl;

    @Value("${zfp.password}")
    private String zfpPassword;

    @Value("${zfp.ecbkey}")
    private String zfpKey;

    public WithOutcome<Referral> addZfpUrls(WithOutcome<Referral> referralDetails, String employeeId) {
        if(referralDetails.success) {
            ArrayList<Study> studies = new ArrayList<Study>();
            for(Study study : referralDetails.value.studies) {
                List<String> uids = study.dicomSeriesStudyUids;
                List<String> urls = new ArrayList<String>();
                for(String uid : uids) {
                    Maybe<String> url = ZeroFootPrintEcb.getToken(
                            uid,
                            employeeId,
                            zfpPassword,
                            zfpKey);

                    if(url.success) {
                        urls.add(zfpUrl + url.value);
                    }

                }
                Study newStudy = study.mapStudyUrls(urls);
                studies.add(newStudy);
            }
            return referralDetails.mapValue(referralDetails.value.mapStudies(studies));
        } else {
            return referralDetails;
        }
    }
}
