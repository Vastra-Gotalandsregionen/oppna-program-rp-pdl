package se.vgregion.domain.pdl.logging;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * User: portaldev
 * Date: 2013-11-21
 * Time: 19:58
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "pdl_log_care_unit")
public class CareUnit extends NamedAndHsaIdBase {

}
