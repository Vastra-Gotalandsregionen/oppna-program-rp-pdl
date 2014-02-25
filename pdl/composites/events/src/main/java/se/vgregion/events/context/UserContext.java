package se.vgregion.events.context;

import java.io.Serializable;

public class UserContext implements Serializable {
    private static final long serialVersionUID = -8437140062208882990L;

    public final String employeeDisplayName;
    public final String employeeHsaId;

    public UserContext(String employeeDisplayName, String employeeHsaId) {
        this.employeeDisplayName = employeeDisplayName;
        this.employeeHsaId = employeeHsaId;
    }

    public String getEmployeeDisplayName() {
        return employeeDisplayName;
    }

    public String getEmployeeHsaId() {
        return employeeHsaId;
    }

    @Override
    public String toString() {
        return "UserContext{" +
                "employeeDisplayName='" + employeeDisplayName + '\'' +
                ", employeeHsaId='" + employeeHsaId + '\'' +
                '}';
    }
}
