package se.vgregion.service.hsa;

import org.apache.cxf.common.i18n.UncheckedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.addressing.v1.AttributedURIType;
import se.vgregion.domain.assignment.Access;
import se.vgregion.domain.assignment.Assignment;
import se.vgregion.domain.decorators.WithOutcome;
import se.vgregion.domain.pdl.InformationType;
import se.vgregion.domain.pdl.PdlContext;
import se.vgregion.service.search.AccessControl;
import se.vgregion.service.search.CareAgreement;
import urn.riv.hsa.HsaWs.v3.HsaWsFault;
import urn.riv.hsa.HsaWs.v3.HsaWsResponderInterface;
import urn.riv.hsa.HsaWsResponder.v3.GetMiuForPersonResponseType;
import urn.riv.hsa.HsaWsResponder.v3.GetMiuForPersonType;
import urn.riv.hsa.HsaWsResponder.v3.MiuInformationType;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

@Service("HsaAccessControl")
public class HsaAccessControl implements AccessControl {

    private static final Logger LOGGER = LoggerFactory.getLogger(HsaAccessControl.class);
    private static final String ALL = ";alla;";
    private static final String VoB = "Vård och behandling";

    @Resource(name = "hsaOrgmaster")
    private HsaWsResponderInterface hsaOrgmaster;

    @Autowired
    private CareAgreement agreementService;

    @Value("${pdl.orgMasterServicesHsaId}")
    private String orgmasterHsaId;

    @Override
    public WithOutcome<PdlContext> getContextByEmployeeId(String hsaId) {
        AttributedURIType to = HsaWsUtil.getAttribute(orgmasterHsaId);

        GetMiuForPersonType miuRequest = new GetMiuForPersonType();
        miuRequest.setHsaIdentity(hsaId);

        try {
            GetMiuForPersonResponseType miuResponse = hsaOrgmaster.getMiuForPerson(
                HsaWsUtil.getAttribute(null),
                to,
                miuRequest
            );

            if (miuResponse.getMiuInformation().size() > 0) {
                TreeMap<String, Assignment> assignments = new TreeMap<String, Assignment>();

                for (MiuInformationType miu : miuResponse.getMiuInformation()) {
                    TreeSet<Access> access = new TreeSet<Access>();

                    // Filter out non VoB assignments.
                    if(miu.getMiuPurpose().equals(VoB)) {

                        for (String miuRight : miu.getMiuRights().getMiuRight()) {
                            if(miuRight.contains(ALL)) {
                                // Special case, expand ALL to all information types
                                ArrayList<String> mius = expandAllMiu(miuRight);
                                    for(String m : mius) {
                                        access.add(Access.fromMiuRights(m));
                                    }
                            } else {
                                access.add(Access.fromMiuRights(miuRight));
                            }
                        }

                        Assignment assignment = new Assignment(
                                miu.getHsaIdentity(),
                                miu.getMiuName(),
                                miu.getCareGiver(),
                                miu.getCareUnitHsaIdentity(),
                                miu.getCareGiverName(),
                                miu.getCareUnitName(),
                                access
                            );

                        boolean hasAgreement = agreementService.hasCareAgreement(assignment.getCareProviderHsaId());

                        if(hasAgreement) {
                            assignments.put(miu.getHsaIdentity(), assignment);
                        }
                    }
                }

                MiuInformationType firstEntry = miuResponse.getMiuInformation().get(0);
                String displayName = firstEntry.getGivenName() + " " + firstEntry.getMiddleAndSurName();
                PdlContext context = new PdlContext(displayName, hsaId, assignments);

                return WithOutcome.success(context);
            }
        } catch (HsaWsFault hsaWsFault) {
            LOGGER.error("Unable to do lookup for HSA-ID {}. Faultinfo: {}.", hsaId, hsaWsFault.getFaultInfo());
            return WithOutcome.clientError(new PdlContext("", hsaId, new TreeMap<String, Assignment>()));
        } catch (UncheckedException cfxFault ) {
            LOGGER.error("Unable to do lookup because of communication error.", cfxFault);
            return WithOutcome.commFailure(new PdlContext("", hsaId, new TreeMap<String, Assignment>()));
        } catch (RuntimeException ex ) {
            LOGGER.error("Unable to do lookup, undefined error.", ex);
            return WithOutcome.clientError(new PdlContext("", hsaId, new TreeMap<String, Assignment>()));
        }
        // We did not get enough information.
        return WithOutcome.unfulfilled(new PdlContext("", hsaId, new TreeMap<String, Assignment>()));
    }

    private ArrayList<String> expandAllMiu(String miuRight) {
        String[] rights = miuRight.split(";");

        String activity = rights[0];
        String scope = rights[2];
        ArrayList<String> mius = new ArrayList<String>();
        for(InformationType i : InformationType.values()) {
            mius.add(activity + ";" + i.toString().toLowerCase() + ";" + scope);
        }

        return mius;
    }
}
