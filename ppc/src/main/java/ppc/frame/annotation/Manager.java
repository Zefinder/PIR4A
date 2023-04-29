package ppc.frame.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation indicates that the class is a manager that needs to be
 * initiated. Initialization order is given by its priority.
 *
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface Manager {
	ManagerPriority priority();
}
