package se.vgregion.service.pdl;


import org.junit.Test;
import se.vgregion.pdl.domain.PatientEngagement;
import se.vgregion.pdl.domain.PdlContext;
import se.vgregion.pdl.domain.PdlReport;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class PdlBlockingSpecification {

    private PdlServiceImpl service;

    @Test
    public void reportHasBlocks() throws Exception {

        PatientLookup patientLookup = new PatientLookupImpl();
        List<PatientEngagement> engagements = patientLookup.findPatient( "830414-4879" );

        // Engagements + SAML-ticket -> Context

        PdlContext ctx = new PdlContext();
        service = new PdlServiceImpl(); // Ansvar: Hämta och filtrera

        PdlReport pdlReport = service.pdlReport(ctx); // Beslutsunderlag
        // Engagements beskriver vart patientdata finns.

        assertTrue(pdlReport.hasBlocks);

        // pdlReport + informationSources =>

        //assertEquals( 1, pdlReport.blocks.size());
    }
}
