package se.vgregion.service.pdl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.addressing.v1.AttributedURIType;
import se.riv.hsa.hsaws.v3.HsaWsFault;
import se.riv.hsa.hsaws.v3.HsaWsResponderInterface;
import se.riv.hsa.hsawsresponder.v3.GetMiuForPersonResponseType;
import se.riv.hsa.hsawsresponder.v3.GetMiuForPersonType;
import se.riv.hsa.hsawsresponder.v3.MiuInformationType;
import se.vgregion.domain.pdl.InformationType;
import se.vgregion.domain.pdl.PdlContext;
import se.vgregion.domain.assignment.Access;
import se.vgregion.domain.assignment.Assignment;
import se.vgregion.domain.decorators.WithOutcome;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

@Service
public class HsaAccessControl implements AccessControl {

    private static final Logger LOGGER = LoggerFactory.getLogger(HsaAccessControl.class.getName());
    private static final String ALL = ";alla;";
    private static final String VoB = "Vård och behandling";

    @Resource(name = "hsaOrgmaster")
    private HsaWsResponderInterface hsaOrgmaster;


    @Override
    public WithOutcome<PdlContext> getContextByEmployeeId(String hsaId) {
        AttributedURIType to = new AttributedURIType();
        to.setValue(hsaId);
        GetMiuForPersonType miuRequest = new GetMiuForPersonType();
        try {
            GetMiuForPersonResponseType miuResponse = hsaOrgmaster.getMiuForPerson(
                new AttributedURIType(),
                to,
                miuRequest
            );

            if (miuResponse.getMiuInformation().size() > 0) {
                WithOutcome<ArrayList<Assignment>> outcome = WithOutcome.success(null);

                TreeMap<String, Assignment> assignments = new TreeMap<String, Assignment>();

                for (MiuInformationType miu : miuResponse.getMiuInformation()) {
                    TreeSet<Access> access = new TreeSet<Access>();

                    // Filter out non VoB assignments.
                    if(miu.getMiuPurpose().equals(VoB)) {

                        for (String miuRight : miu.getMiuRights().getMiuRight()) {
                            if(miuRight.contains(ALL)) {
                                // Special case, expand ALL to all information types
                                WithOutcome<ArrayList<String>> mius = expandAllMiu(miuRight);
                                if(mius.isSuccess()) {
                                    for(String m : mius.value) {
                                        WithOutcome<Access> accessEntry = Access.fromMiuRights(m);
                                        outcome = WithOutcome.flatten(outcome, accessEntry);
                                    }
                                }
                            } else {
                                WithOutcome<Access> accessEntry = Access.fromMiuRights(miuRight);

                                outcome = WithOutcome.flatten(outcome, accessEntry); // Combining multiple outcomes into one outcome taking the first failure

                                access.add(accessEntry.value);
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

                        assignments.put(miu.getHsaIdentity(), assignment);
                    }
                }

                MiuInformationType firstEntry = miuResponse.getMiuInformation().get(0);
                String displayName = firstEntry.getGivenName() + " " + firstEntry.getMiddleAndSurName();
                PdlContext context = new PdlContext(displayName, hsaId, assignments);

                return outcome.mapValue(context);
            }
        } catch (HsaWsFault hsaWsFault) {
            LOGGER.error("Unable to do lookup for HSA-ID {}.", hsaId, hsaWsFault);
        }
        // We did not get enough information.
        return WithOutcome.unfulfilled(new PdlContext("", hsaId, new TreeMap<String, Assignment>()));
    }

    private WithOutcome<ArrayList<String>> expandAllMiu(String miuRight) {
        String[] rights = miuRight.split(";");
        if(rights.length == 3) {
            String activity = rights[0];
            String scope = rights[2];
            ArrayList<String> mius = new ArrayList<String>();
            for(InformationType i : InformationType.values()) {
                mius.add(activity + ";" + i.toString().toLowerCase() + ";" + scope);
            }

            return WithOutcome.success(mius);
        }
        return WithOutcome.clientError(null);
    }
}
