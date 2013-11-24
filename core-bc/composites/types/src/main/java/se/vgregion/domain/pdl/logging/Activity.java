package se.vgregion.domain.pdl.logging;

import javax.persistence.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: portaldev
 * Date: 2013-11-21
 * Time: 19:58
 * To change this template use File | Settings | File Templates.
 */
//@Entity
//@Table(name = "pdl_log_activity")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private String id;

    private String type;

    private String level;

    private String args;

    private Date startDate;

    private String purpose;

}
