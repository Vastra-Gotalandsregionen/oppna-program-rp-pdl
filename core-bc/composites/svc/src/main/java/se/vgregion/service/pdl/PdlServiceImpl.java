package se.vgregion.service.pdl;

import org.springframework.stereotype.Service;
import se.vgregion.pdl.domain.PdlContext;
import se.vgregion.pdl.domain.PdlReport;

@Service
public class PdlServiceImpl {

    public PdlReport pdlReport(PdlContext ctx) {
        return new PdlReport();
    }
}
