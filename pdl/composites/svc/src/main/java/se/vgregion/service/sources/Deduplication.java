package se.vgregion.service.sources;

import se.vgregion.domain.decorators.WithInfoType;
import se.vgregion.domain.decorators.WithOutcome;
import se.vgregion.domain.decorators.WithPatient;
import se.vgregion.domain.pdl.InformationType;
import se.vgregion.domain.systems.CareSystem;
import se.vgregion.events.context.SourceReferences;

import java.util.*;

public class Deduplication {
    private Deduplication() {

    }

    public static WithOutcome<WithPatient<ArrayList<WithInfoType<CareSystem>>>> remapDeduplicate(
            WithOutcome<WithPatient<ArrayList<WithInfoType<CareSystem>>>> result
    ) {
        WithPatient<ArrayList<WithInfoType<CareSystem>>> patient =
                result.value.mapValue(deduplicate(result.value.value));

        WithOutcome<WithPatient<ArrayList<WithInfoType<CareSystem>>>> outcome =
                result.mapValue(patient);

        return outcome;
    }

    static class InfoTypeCareSystemKey {
        public final InformationType informationType;
        public final String careProviderHsaId;
        public final String careUnitHsaId;

        public InfoTypeCareSystemKey(WithInfoType<CareSystem> ics) {
            this.informationType = ics.informationType;
            this.careProviderHsaId = ics.value.careProviderHsaId;
            this.careUnitHsaId = ics.value.careUnitHsaId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof InfoTypeCareSystemKey)) return false;

            InfoTypeCareSystemKey that = (InfoTypeCareSystemKey) o;

            if (!careProviderHsaId.equals(that.careProviderHsaId)) return false;
            if (!careUnitHsaId.equals(that.careUnitHsaId)) return false;
            if (informationType != that.informationType) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = informationType.hashCode();
            result = 31 * result + careProviderHsaId.hashCode();
            result = 31 * result + careUnitHsaId.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "InfoTypeCareSystemKey{" +
                    "informationType=" + informationType +
                    ", careProviderHsaId='" + careProviderHsaId + '\'' +
                    ", careUnitHsaId='" + careUnitHsaId + '\'' +
                    '}';
        }
    }

    static ArrayList<WithInfoType<CareSystem>> deduplicate(ArrayList<WithInfoType<CareSystem>> careSystems) {
        Map<InfoTypeCareSystemKey,WithInfoType<CareSystem>> deduplicated =
                new HashMap<InfoTypeCareSystemKey, WithInfoType<CareSystem>>();

        if(careSystems.size() > 1) {
            deduplicated.put(new InfoTypeCareSystemKey(careSystems.get(0)),careSystems.get(0));

            for(WithInfoType<CareSystem> elem : careSystems) {
                InfoTypeCareSystemKey key =
                        new InfoTypeCareSystemKey(elem);

                if(deduplicated.containsKey(key) &&
                   deduplicated.get(key).value.displayId.equals(elem.value.displayId)) {
                    continue;
                }

                if(deduplicated.containsKey(key)) {
                    WithInfoType<CareSystem> elem2 = deduplicated.get(key);

                    Map<String, SourceReferences> ref2 = elem2.value.references;
                    CareSystem careSystem = elem.value.addReferences(ref2);
                    WithInfoType<CareSystem> infoType = elem.mapValue(careSystem);

                    deduplicated.put(key, infoType);
                } else {
                    deduplicated.put(key, elem);
                }
            }

            return new ArrayList<WithInfoType<CareSystem>>(deduplicated.values());
        }
        return careSystems;
    }
}
