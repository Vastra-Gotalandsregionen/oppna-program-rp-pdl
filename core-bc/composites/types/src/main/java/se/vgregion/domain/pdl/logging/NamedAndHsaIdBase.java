package se.vgregion.domain.pdl.logging;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.lang.*;

/**
 * Created with IntelliJ IDEA.
 * User: portaldev
 * Date: 2013-11-21
 * Time: 21:17
 * To change this template use File | Settings | File Templates.
 */
@MappedSuperclass
public class NamedAndHsaIdBase implements Serializable {

    @Id
    private String hsaId;

    private String name;


    public String getHsaId() {
        return hsaId;
    }

    public void setHsaId(String hsaId) {
        this.hsaId = hsaId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static void main(String[] args) {
        java.lang.System.out.println("<class>" + Activity.class.getName() + "</class>");
        java.lang.System.out.println("<class>" + CareProvider.class.getName() + "</class>");
        java.lang.System.out.println("<class>" + CareUnit.class.getName() + "</class>");
        java.lang.System.out.println("<class>" + Log.class.getName() + "</class>");
        java.lang.System.out.println("<class>" + Patient.class.getName() + "</class>");
        java.lang.System.out.println("<class>" + java.lang.System.class.getName() + "</class>");
        java.lang.System.out.println("<class>" + User.class.getName() + "</class>");

    }


}
