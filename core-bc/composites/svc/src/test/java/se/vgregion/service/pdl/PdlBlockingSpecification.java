package se.vgregion.service.pdl;


import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import se.riv.ehr.blocking.querying.getblocksforpatient.v2.rivtabp21.GetBlocksForPatientResponderService;
import se.vgregion.pdl.domain.PatientEngagement;
import se.vgregion.pdl.domain.PdlContext;
import se.vgregion.pdl.domain.PdlReport;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class PdlBlockingSpecification {


    @Mock
    private GetBlocksForPatientResponderService blocksForPatient;

    @InjectMocks
    private PdlServiceImpl service = new PdlServiceImpl();

    @Before public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void reportHasBlocks() throws Exception {

        PatientLookup patientLookup = new PatientLookupImpl();
        List<PatientEngagement> engagements = patientLookup.findPatient( "830414-4879" );

        // Engagements + SAML-ticket -> Context

        PdlContext ctx = new PdlContext();

        PdlReport pdlReport = service.pdlReport(ctx); // Beslutsunderlag
        // Engagements beskriver vart patientdata finns.

        assertTrue(pdlReport.hasBlocks);

        // pdlReport + informationSources =>

        //assertEquals( 1, pdlReport.blocks.size());
    }
}
