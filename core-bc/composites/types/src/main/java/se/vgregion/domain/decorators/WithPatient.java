package se.vgregion.domain.decorators;

import se.vgregion.domain.pdl.Patient;

import java.io.Serializable;

public class WithPatient<T extends Serializable> implements Serializable {

    private static final long serialVersionUID = 7749941806431608222L;

    public final Patient patient;
    public final T value;

    public WithPatient(Patient patient, T value) {
        this.patient = patient;
        this.value = value;
    }

    public Patient getPatient() {
        return patient;
    }

    public T getValue() {
        return value;
    }

    public WithPatient<T> mapPatient(Patient newPatient) {
        return new WithPatient<T>(newPatient, value);
    }


    public <N extends Serializable> WithPatient<N> mapValue(N newValue) {
        return new WithPatient<N>(patient, newValue);
    }

    @Override
    public String toString() {
        return "WithPatient{" +
                "patient=" + patient +
                ", value=" + value +
                '}';
    }
}
