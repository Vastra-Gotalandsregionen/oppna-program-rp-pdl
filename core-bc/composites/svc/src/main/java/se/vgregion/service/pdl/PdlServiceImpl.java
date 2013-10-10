package se.vgregion.service.pdl;

import org.springframework.stereotype.Service;
import se.riv.ehr.blocking.querying.getblocksforpatient.v2.rivtabp21.GetBlocksForPatientResponderInterface;
import se.riv.ehr.blocking.querying.getblocksforpatient.v2.rivtabp21.GetBlocksForPatientResponderService;
import se.vgregion.pdl.domain.PdlContext;
import se.vgregion.pdl.domain.PdlReport;

import javax.annotation.Resource;

@Service
public class PdlServiceImpl implements PdlService {

    @Resource(name="blocksForPatient")
    private GetBlocksForPatientResponderService blocksForPatient;

    public PdlServiceImpl() {
    }

    public PdlReport pdlReport(PdlContext ctx) {
        GetBlocksForPatientResponderInterface port = blocksForPatient.getGetBlocksForPatientResponderPort();


        return new PdlReport();
    }
}
