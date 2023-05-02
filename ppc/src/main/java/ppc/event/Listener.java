package ppc.event;

import ppc.annotation.EventHandler;
import ppc.manager.EventManager;

/**
 * <p>
 * Interface implemented by all classes that want to listen an event.
 * </p>
 * 
 * <p>
 * All listeners need to register themselves to the {@link EventManager} manager
 * using the {@link EventManager#registerListener(Listener)} method. The
 * {@link Event} must be registered before the listener listening for this
 * event. Else the registering will be ignored.
 * </p>
 * 
 * <p>
 * The method that will listen to an event is called an event handler, contains
 * the {@link Event} as method parameter and is annotated with the
 * {@link EventHandler} annotation. The method must have one and only one
 * parameter being the listened event and be public. Else the registering will
 * be ignored. The method's name doesn't matter as well as the return value
 * (which will be ignored).
 * </p>
 * 
 * <p>
 * Here is an example of a listener:
 * </p>
 * 
 * <pre>
 * public class ExampleListener implements Listener {
 *
 * public ExampleListener() {
 *    EventManager.getInstance().registerListener(this);
 * }
 * 
 * &#64;EventHandler
 * public void onExampleEvent(ExampleEvent event) {
 *    System.out.println("New name: " + event.getName());
 * }
 * </pre>
 * 
 * @see Event
 * @see ppc.annotation.Event
 * @see EventHandler
 * @see EventManager
 * 
 * @author Adrien Jakubiak
 *
 */
public interface Listener {

}
