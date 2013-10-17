package se.vgregion.service.pdl;

import se.vgregion.domain.pdl.CaregiverSystemDescription;
import se.vgregion.domain.pdl.Engagement;

import java.util.List;

public interface CaregiverSystems {
    List<CaregiverSystemDescription> byInformationType( List<Engagement.InformationType> information);
}
