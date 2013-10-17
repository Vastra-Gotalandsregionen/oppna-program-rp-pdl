package se.vgregion.service.pdl;

import org.springframework.stereotype.Service;
import se.vgregion.domain.pdl.CaregiverSystemDescription;
import se.vgregion.domain.pdl.Engagement;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class CaregiverSystemsImpl implements CaregiverSystems {
    @Override
    public List<CaregiverSystemDescription> byInformationType(List<Engagement.InformationType> information) {
        return Collections
                .unmodifiableList(
                        Arrays
                                .asList(
                                        new CaregiverSystemDescription("Bild- och funktionsregistret"),
                                        new CaregiverSystemDescription("System X")
                                )
                );
    }
}
