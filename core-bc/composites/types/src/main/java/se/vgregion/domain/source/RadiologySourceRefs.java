package se.vgregion.domain.source;

public class RadiologySourceRefs implements SourceReferences {

    private static final long serialVersionUID = 6013888161146061222L;

    public final String infobrokerId;


    public RadiologySourceRefs(String infobrokerId) {
        this.infobrokerId = infobrokerId;
    }

    public String getInfobrokerId() {
        return infobrokerId;
    }

    @Override
    public String toString() {
        return "RadiologySourceRefs{" +
                "infobrokerId='" + infobrokerId + '\'' +
                '}';
    }
}
