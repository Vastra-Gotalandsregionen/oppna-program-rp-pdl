package se.vgregion.events.context;


import java.io.Serializable;

public interface SourceReferences extends Serializable {
    String targetCareSystem();

    SourceReferences combine(SourceReferences references);
}
