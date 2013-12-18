package se.vgregion.domain.pdl.logging;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by clalu4 on 2013-12-15.
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface LogThisField {

    UserAction[] onActions() default {};

    //String format() default "%o:'%o'";

}
