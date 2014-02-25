package se.vgregion.service.bfr;

import com.mawell.ib.patientsearch.RequestOrder;
import org.springframework.stereotype.Service;
import org.w3._2005._08.addressing.AttributedURIType;
import riv.ehr.ehrexchange.patienthistory._1.rivtabp20.PatientHistoryResponderInterface;
import se.ecare.ib.exportmessage.Request;
import se.vgregion.domain.bfr.Refferal;
import se.vgregion.domain.decorators.WithOutcome;

import javax.annotation.Resource;

@Service
public class RadiologySource {

    @Resource(name = "infoBroker")
    private PatientHistoryResponderInterface infoBroker;


    public WithOutcome<Refferal> requestByBrokerId(String brokerId) {
        Request request = ibRequest(brokerId);

        Refferal refferal = mapRequestToRequestDetails(request);

        return WithOutcome.success(refferal);
    }

    private Refferal mapRequestToRequestDetails(Request request) {
        //return new Refferal();
    }

    private Request ibRequest(String brokerId) {
        AttributedURIType attribURIType = new AttributedURIType();

        RequestOrder reqOrder = new RequestOrder();
        reqOrder.setInfobrokerId(brokerId);

        return infoBroker.getRequest(attribURIType, reqOrder);
    }

}
