package se.vgregion.domain.pdl.logging;

import se.vgregion.domain.logging.UserAction;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface LogThisField {

    UserAction[] onActions() default {};

    //String format() default "%o:'%o'";

}
