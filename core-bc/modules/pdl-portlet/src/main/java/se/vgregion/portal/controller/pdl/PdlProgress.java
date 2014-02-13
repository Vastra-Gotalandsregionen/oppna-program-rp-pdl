package se.vgregion.portal.controller.pdl;

public enum PdlProgress {
   SEARCH, CHOOSE, SYSTEMS;

   public PdlProgress nextStep() {
       switch(this) {
            case SEARCH:
               return CHOOSE;
            case CHOOSE:
                return SYSTEMS;
            case SYSTEMS:
                return SYSTEMS;
            default:
                return SEARCH;
       }
   }

    public PdlProgress previousStep() {
        switch(this) {
            case SEARCH:
                return SEARCH;
            case CHOOSE:
                return SEARCH;
            case SYSTEMS:
                return CHOOSE;
            default:
                return SEARCH;
        }
    }

    public static PdlProgress firstStep() {
        return SEARCH;
    }
}
