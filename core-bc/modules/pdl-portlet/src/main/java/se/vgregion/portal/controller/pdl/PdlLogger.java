package se.vgregion.portal.controller.pdl;

import se.vgregion.domain.decorators.InfoTypeState;
import se.vgregion.domain.decorators.Maybe;
import se.vgregion.domain.decorators.SystemState;
import se.vgregion.domain.logging.PdlEventLog;
import se.vgregion.domain.logging.UserAction;
import se.vgregion.domain.pdl.InformationType;
import se.vgregion.domain.pdl.Patient;
import se.vgregion.domain.pdl.PdlContext;
import se.vgregion.domain.systems.CareSystem;
import se.vgregion.repo.log.LogRepo;

import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

public class PdlLogger {

    public static void log(UserAction action, LogRepo logRepo, PdlUserState state ) {
        PdlEventLog log = newPdlEventLog(action, state);
        logRepo.persist(log);
    }

    private static PdlEventLog newPdlEventLog(UserAction action, PdlUserState state) {
        PdlEventLog log = new PdlEventLog();
        Patient patient = state.getPatient();
        PdlContext ctx = state.getCtx().value;

        log.setUserAction(action);
        log.setPatientDisplayName(patient.getPatientDisplayName());
        log.setPatientId(patient.getPatientId());
        log.setEmployeeDisplayName(ctx.employeeDisplayName);
        log.setEmployeeId(ctx.employeeHsaId);
        log.setAssignmentId(ctx.currentAssignment.assignmentHsaId);
        log.setCareProviderDisplayName(ctx.currentAssignment.careProviderDisplayName);
        log.setCareProviderId(ctx.currentAssignment.careProviderHsaId);
        log.setCareUnitDisplayName(ctx.currentAssignment.careUnitDisplayName);
        log.setCareUnitId(ctx.currentAssignment.careUnitHsaId);
        log.setCreationTime(new Date());
        log.setSearchSession(state.getSearchSession());
        log.setSystemId("Regionportalen - Sök Patient PDL");

        boolean notAttest = action != UserAction.ATTEST_RELATION;
        if(notAttest) {
            Maybe<String> maybeText = viewedSystemsLog(state);
            String text = maybeText.success ? maybeText.value : "";
            log.setLogText(text);
        }

        return log;
    }

    private static Maybe<String> viewedSystemsLog(PdlUserState state) {
        StringBuilder viewedData = new StringBuilder();
        if(state.getCsReport() != null) {
            TreeMap<InfoTypeState<InformationType>, ArrayList<SystemState<CareSystem>>> careSystems =
                    state.getCsReport().aggregatedSystems.value;

            for(InfoTypeState<InformationType> key : careSystems.keySet()) {

                viewedData.
                        append("=== ").
                        append(key.value.getDesc().toUpperCase()).
                        append(" ===\n");

                if(key.isSelected()) {
                    for(SystemState<CareSystem> sys : careSystems.get(key)) {
                        if(sys.selected) {
                            viewedData.append("[X] ");
                        } else if(sys.needConfirmation){
                            viewedData.append("[B] ");
                        } else {
                            viewedData.append("[-] ");
                        }

                        viewedData.
                                append(sys.value.careProviderDisplayName).
                                append(" - ").
                                append(sys.value.careUnitDisplayName);

                        if(sys.blocked && sys.initiallyBlocked) {
                            viewedData.append(" (Spärrad information)");
                        } else if(!sys.blocked && sys.initiallyBlocked) {
                            viewedData.append(" (Passerad spärr)");
                        }
                        viewedData.append("\n");
                    }
                    viewedData.append("\n");
                }
            }
            return Maybe.some(viewedData.toString());
        }
        return Maybe.none();
    }

}
