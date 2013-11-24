package se.vgregion.domain.pdl.logging;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * User: portaldev
 * Date: 2013-11-21
 * Time: 19:58
 * To change this template use File | Settings | File Templates.
 */
//@Entity
//@Table(name = "pdl_log_user")
public class User extends NamedAndHsaIdBase {

    private String personId;

    private String assignment;

    private String title;

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getAssignment() {
        return assignment;
    }

    public void setAssignment(String assignment) {
        this.assignment = assignment;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
