package se.vgregion.domain.pdl.logging;

import java.util.Date;
import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: portaldev
 * Date: 2013-11-21
 * Time: 19:58
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "pdl_log")
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
