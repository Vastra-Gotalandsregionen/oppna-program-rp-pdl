package se.vgregion.events.context;


import java.io.Serializable;

public interface SourceReferences extends Serializable {
    String targetCareSystem();

    // Get UUID for this reference.
    // Should be an UUID, no correlation to actual source system id's.
    String getId();

    SourceReferences combine(SourceReferences references);
}
