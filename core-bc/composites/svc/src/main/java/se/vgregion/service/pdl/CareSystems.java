package se.vgregion.service.pdl;

import se.vgregion.domain.pdl.CareSystem;
import se.vgregion.domain.pdl.Engagement;

import java.util.List;

public interface CareSystems {
    List<CareSystem> byInformationType( List<Engagement.InformationType> information);
}
