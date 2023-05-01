package ppc.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation indicates that the class is an event and needs to be
 * registered during init time.
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface Event {

}
