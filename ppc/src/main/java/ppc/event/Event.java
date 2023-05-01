package ppc.event;

import java.util.List;

import ppc.manager.EventManager;

/**
 * <p>
 * All classes extending this class represents an event.
 * </p>
 * 
 * <p>
 * Events are a simplified and generic way to communicate between instances.
 * However, to be considered as a usable event for the EventManager, the event
 * class must define a static {@link RegisteredListener} list that will be
 * returned by {@link #getHandlers()} (that must remain public).
 * </p>
 * 
 * <p>
 * To register an event, use the {@link EventManager#registerEvent(Event)}. If
 * the event needs to be registered during program initialization, use the
 * {@link ppc.annotation.Event} annotation. In that case, leave a constructor
 * without any parameters.
 * </p>
 * 
 * <p>
 * Here is an example of event that gets a name.
 * </p>
 * 
 * <pre>
 * &#64;ppc.annotation.Event
 * public class ExampleEvent extends Event {
 * 	private static final List&lt;RegisteredListener&gt; HANDLERS = new ArrayList<>();
 * 	private String name;
 * 
 * 	public ExampleEvent() {
 * 
 * 	}
 * 
 * 	public ExampleEvent(String name) {
 * 		this.name = name;
 * 	}
 * 
 * 	public String getName() {
 * 		return name;
 * 	}
 * 
 * 	&#64;Override
 * 	public List<RegisteredListener> getHandlers() {
 * 		return HANDLERS;
 * 	}
 * }
 * </pre>
 * 
 * <p>
 * To call an event, use the {@link EventManager#callEvent(Event)} method.
 * </p>
 * 
 * @author Adrien Jakubiak
 * 
 * @see EventManager
 * @see Listener
 */
public abstract class Event {

	/**
	 * Gets the list of handlers for this event
	 * 
	 * @return the list of handlers for this event
	 */
	public abstract List<RegisteredListener> getHandlers();
}
