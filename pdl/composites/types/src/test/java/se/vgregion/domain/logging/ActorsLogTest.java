package se.vgregion.domain.logging;

import junit.framework.Assert;
import org.apache.commons.beanutils.BeanMap;
import org.junit.Test;

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
