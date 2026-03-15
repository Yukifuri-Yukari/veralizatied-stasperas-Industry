package yukifuri.mc.vsindustry.util;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value={CONSTRUCTOR, FIELD, METHOD, PACKAGE, MODULE, PARAMETER, TYPE})
public @interface WorkInProgress {
    String value() default "null";
    String expectedComplete() default "null";
}
