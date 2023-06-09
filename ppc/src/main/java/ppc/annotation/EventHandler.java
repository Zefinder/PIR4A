package ppc.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import ppc.manager.EventManager;

/**
 * <p>
 * This annotation indicates that the class is a listener and needs to be
 * registered during init time.
 * </p>
 * 
 * <p>
 * All methods annotated with this should not be invoked by the user. Methods
 * are called asynchronously by the {@link EventManager}
 * </p>
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface EventHandler {

}
