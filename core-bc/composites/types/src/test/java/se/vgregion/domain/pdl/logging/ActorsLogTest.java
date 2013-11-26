package se.vgregion.domain.pdl.logging;

import junit.framework.Assert;
import org.apache.commons.collections.BeanMap;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: Claes Lundahl
 * Date: 2013-11-25
 * Time: 14:55
 * To change this template use File | Settings | File Templates.
 */
public class ActorsLogTest {

    @Test
    public void oneArgumentCloningConstructor() {
        ActorsLog actorsLog = new ActorsLog();
        BeanMap actorsLogMap = new BeanMap(actorsLog);
        for (Object key : actorsLogMap.keySet()) {
            String name = (String) key;
            if (String.class.equals(actorsLogMap.getType(name))) {
                actorsLogMap.put(key, key);
            }
        }

        PdlEventLog pdlLog = new PdlEventLog(actorsLog);
        BeanMap pdlLogMap = new BeanMap(pdlLog);

        for (Object key : actorsLogMap.keySet()) {
            String name = (String) key;
            if (String.class.equals(actorsLogMap.getType(name))) {
                Assert.assertEquals(key, pdlLogMap.get(key));
            }
        }
    }

}
