package ppc.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Class containing the instance of the listener to be listened.
 * 
 * @author Adrien Jakubiak
 * 
 */
public class RegisteredListener {

	private Method method;
	private Listener listener;

	/**
	 * Registers the listener to call it after.
	 * 
	 * @param listener the listener to register
	 */
	public RegisteredListener(final Listener listener, final Method method) {
		this.listener = listener;
		this.method = method;
	}

	/**
	 * Update the listener listening for this event.
	 * 
	 * @param event the listened event
	 */
	public void fireChange(Event event) {
		try {
			method.invoke(listener, event);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

}
