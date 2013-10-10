package se.vgregion.service.pdl;


import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import riv.ehr.blocking.accesscontrol._3.CheckBlocksResultType;
import riv.ehr.blocking.accesscontrol._3.CheckResultType;
import riv.ehr.blocking.accesscontrol._3.CheckStatusType;
import se.riv.ehr.blocking.accesscontrol.checkblocks.v3.rivtabp21.CheckBlocksResponderInterface;
import se.riv.ehr.blocking.accesscontrol.checkblocks.v3.rivtabp21.CheckBlocksResponderService;
import se.riv.ehr.blocking.accesscontrol.checkblocksresponder.v3.CheckBlocksRequestType;
import se.riv.ehr.blocking.accesscontrol.checkblocksresponder.v3.CheckBlocksResponseType;
import se.vgregion.pdl.domain.PatientEngagement;
import se.vgregion.pdl.domain.PdlContext;
import se.vgregion.pdl.domain.PdlReport;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

public class PdlBlockingSpecification {


    @Mock
    private CheckBlocksResponderService blocksForPatient;

    @Mock
    private CheckBlocksResponderInterface blocksInterface;

    @InjectMocks
    private PdlServiceImpl service = new PdlServiceImpl();

    PatientLookup patientLookup = new PatientLookupImpl();

    @Before public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    private CheckBlocksResponseType blockedResult( int rowNumber, boolean isBlocked ) {
        CheckBlocksResponseType response = new CheckBlocksResponseType();

        CheckBlocksResultType result = new CheckBlocksResultType();
        List<CheckResultType> results = result.getCheckResults();

        CheckResultType check = new CheckResultType();
        CheckStatusType blockStatus = (isBlocked) ? CheckStatusType.BLOCKED : CheckStatusType.OK;
        check.setStatus(blockStatus );
        check.setRowNumber( rowNumber );
        results.add( check );
        response.setCheckBlocksResultType(result);

        return response;
    }

    @Test
    public void reportHasBlocks() throws Exception {

        when(blocksForPatient.getCheckBlocksResponderPort()).thenReturn(blocksInterface);
        when(blocksInterface.checkBlocks(anyString(),isA(CheckBlocksRequestType.class))).thenReturn(blockedResult(0, true));

        List<PatientEngagement> engagements = patientLookup.findPatient( "patient1" );
        PdlContext ctx = new PdlContext("patientHsaId", engagements, "careProviderHsaId", "careUnitHsaId", "employeeHsaId");

        PdlReport pdlReport = service.pdlReport(ctx);

        assertTrue(pdlReport.hasBlocks);
        assertEquals(1, pdlReport.blocks.size());
    }


    @Test
    public void reportWithoutBlocks() throws Exception {

        when(blocksForPatient.getCheckBlocksResponderPort()).thenReturn(blocksInterface);
        when(blocksInterface.checkBlocks(anyString(),isA(CheckBlocksRequestType.class))).thenReturn(blockedResult(0, false));

        List<PatientEngagement> engagements = patientLookup.findPatient( "patient1" );
        PdlContext ctx = new PdlContext("patientHsaId", engagements, "careProviderHsaId", "careUnitHsaId", "employeeHsaId");

        PdlReport pdlReport = service.pdlReport(ctx);

        assertFalse(pdlReport.hasBlocks);
        assertEquals(1, pdlReport.blocks.size());
    }
}
