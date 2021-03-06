package se.vgregion.domain.systems;

import se.vgregion.events.context.SourceReferences;

import java.io.Serializable;
import java.util.*;

public class CareSystem implements Serializable {
    private static final long serialVersionUID = -8149026774965825742L;

    public final CareSystemViewer source;
    public final String careProviderHsaId;
    public final String careProviderDisplayName;
    public final String careUnitHsaId;
    public final String careUnitDisplayName;
    public final String displayId = java.util.UUID.randomUUID().toString(); // Used to keep track of Care System between requests.
    public final Map<String,SourceReferences> references;

    public CareSystem(
            CareSystemViewer source,
            String careProviderHsaId,
            String careProviderDisplayName,
            String careUnitHsaId,
            String careUnitDisplayName,
            Map<String, SourceReferences> references) {
        this.source = source;
        this.careProviderHsaId = careProviderHsaId;
        this.careProviderDisplayName = careProviderDisplayName;
        this.careUnitHsaId = careUnitHsaId;
        this.careUnitDisplayName = careUnitDisplayName;
        if(references != null) {
            this.references = Collections.unmodifiableMap(references);
        } else {
            this.references = Collections.unmodifiableMap(new HashMap<String,SourceReferences>());
        }

    }

    public CareSystem(CareSystemViewer source,
                      CareProviderUnit careProviderUnit,
                      Map<String, SourceReferences> references) {
        this.source = source;
        this.references = references;
        this.careProviderHsaId = careProviderUnit.careProviderHsaId;
        this.careProviderDisplayName = careProviderUnit.careProviderDisplayName;
        this.careUnitHsaId = careProviderUnit.careUnitHsaId;
        this.careUnitDisplayName = careProviderUnit.careUnitDisplayName;
    }

    public String getCareUnitHsaId() {
        return careUnitHsaId;
    }

    public String getCareProviderHsaId() {
        return careProviderHsaId;
    }

    public String getDisplayId() {
        return displayId;
    }

    public String getCareProviderDisplayName() {
        return careProviderDisplayName;
    }

    public String getCareUnitDisplayName() {
        return careUnitDisplayName;
    }

    public CareSystemViewer getSource() {
        return source;
    }

    public CareSystem addReferences(Map<String, SourceReferences> newReferences) {
        Map<String, SourceReferences> refs =
                new HashMap<String, SourceReferences>();

        refs.putAll(references);
        refs.putAll(newReferences);
        return mapReferences(refs);
    }

    public CareSystem mapReferences(Map<String, SourceReferences> newReferences) {
        return new CareSystem(
                source,
                careProviderHsaId,
                careProviderDisplayName,
                careUnitHsaId,
                careUnitDisplayName,
                newReferences
        );
    }

    @Override
    public String toString() {
        return "CareSystem{" +
                "source=" + source +
                ", careProviderHsaId='" + careProviderHsaId + '\'' +
                ", careProviderDisplayName='" + careProviderDisplayName + '\'' +
                ", careUnitHsaId='" + careUnitHsaId + '\'' +
                ", careUnitDisplayName='" + careUnitDisplayName + '\'' +
                ", displayId='" + displayId +
                '}';
    }
}
